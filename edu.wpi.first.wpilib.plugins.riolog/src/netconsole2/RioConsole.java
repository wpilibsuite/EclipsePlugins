package netconsole2;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RioConsole {
  private ILogger logger;

  /**
   * The constructor.
   */
  public RioConsole(ILogger logger) {
    this.logger = logger;
    rioConnector = new RioConnector(logger);
  }

  /**
   * Read a tagged segment into a byte buffer.
   * segmentData must have a capacity of at least 65535.
   * Returns tag, or -1 on error.
   */
  private static int readSegment(ByteBuffer segmentData, DataInputStream inputStream) throws IOException {
    // read 2-byte length.  Ignore zero length frames
    int len;
    do {
     len = inputStream.readUnsignedShort();
    } while (len == 0);

    // read 1-byte tag
    int tag = inputStream.readUnsignedByte();
    //logger.log("got segment len=" + len + " tag=" + tag);

    // subtract 1 for tag
    len -= 1;

    segmentData.clear();
    segmentData.limit(len);
    byte[] data = segmentData.array();

    int bytesRead = 0;
    while (bytesRead < len) {
      int nRead = inputStream.read(data, bytesRead, len - bytesRead);
      if (nRead < 0) {
        return -1;
      }
      bytesRead += nRead;
    }
    //logger.log("finished reading segment");
    return tag;
  }

  private Thread listener;
  private Thread sender;

  private final RioConnector rioConnector;
  private final Lock lock = new ReentrantLock();
  private final Condition wakeupListener = lock.newCondition();
  private Socket socket = null;
  private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
  private boolean autoReconnect = true;
  private final AtomicBoolean cleanup = new AtomicBoolean(false);
  private final AtomicBoolean reconnect = new AtomicBoolean(false);
  private final AtomicBoolean discard = new AtomicBoolean(false);
  private final AtomicBoolean paused = new AtomicBoolean(false);
  private final AtomicBoolean showWarning = new AtomicBoolean(true);
  private final AtomicBoolean showPrint = new AtomicBoolean(true);
  private Consumer<Boolean> connectedCallback = null;

  @Override
  protected void finalize() {
    stop();
  }

  public void stop() {
    cleanup.set(true);
    closeSocket();
    if (listener != null) {
      listener.interrupt();
    }
  }

  public void reconnect() {
    reconnect.set(true);
    closeSocket();
  }

  public boolean getAutoReconnect() {
    return autoReconnect;
  }

  public void setAutoReconnect(boolean value) {
    lock.lock();
    try {
      autoReconnect = value;
      if (value) {
        wakeupListener.signal();
      }
    } finally {
      lock.unlock();
    }
  }

  public boolean getPaused() {
    return paused.get();
  }

  public boolean setPaused(boolean value) {
    return paused.getAndSet(value);
  }

  public boolean getDiscard() {
    return discard.get();
  }

  public boolean setDiscard(boolean value) {
    return discard.getAndSet(value);
  }

  public boolean getShowWarning() {
    return showWarning.get();
  }

  public boolean setShowWarning(boolean value) {
    return showWarning.getAndSet(value);
  }

  public boolean getShowPrint() {
    return showPrint.get();
  }

  public boolean setShowPrint(boolean value) {
    return showPrint.getAndSet(value);
  }

  public void putDummyMessage() {
    try {
      messageQueue.put(new DummyMessage());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void setSocket(Socket socket) {
    lock.lock();
    this.socket = socket;
    lock.unlock();
  }

  public void setConnectedCallback(Consumer<Boolean> callback) {
    lock.lock();
    this.connectedCallback = callback;
    lock.unlock();
  }

  public BlockingQueue<Message> getMessageQueue() {
    return messageQueue;
  }

  private static byte[] emptyFrame = new byte[] {0, 0};

  private DataInputStream connect(int team) {
    //logger.log("connecting to team " + team);
    Socket mySocket;
    try {
      mySocket = rioConnector.connect(team);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return null;
    }
    if (mySocket == null) {
      return null;
    }
    setSocket(mySocket);
    DataInputStream in;
    OutputStream out;
    try {
      mySocket.setTcpNoDelay(true);  // for keep alives
      in = new DataInputStream(mySocket.getInputStream());
      out = mySocket.getOutputStream();
    } catch (IOException e) {
      closeSocket();
      return null;
    }
    lock.lock();
    Consumer<Boolean> connCb = connectedCallback;
    lock.unlock();
    if (connCb != null) {
      connCb.accept(Boolean.TRUE);
    }
    //logger.log("socket connected");

    // kick off keep alive thread
    if (sender == null) {
      sender = new Thread(() -> {
        while (!Thread.interrupted() && !cleanup.get()) {
          try {
            Thread.currentThread().sleep(2000);
            out.write(emptyFrame);
            out.flush();
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
          } catch (IOException e) {
            logger.log("failed to send keep alive, reconnecting");
            reconnect();
            break;
          }
        }
        sender = null;
      }, "RioConsoleSender");
      sender.setDaemon(true);
      sender.start();
    }
    return in;
  }

  private void handleSegment(int tag, ByteBuffer data) {
    if (discard.get()) {
      return;
    }

    Message m;
    if (tag == 11) {  // Error or warning
      m = new ErrorMessage(data);
      if (m.getType() == Message.Type.kWarning && !showWarning.get()) {
        return;
      }
    } else if (tag == 12 && showPrint.get()) {  // Console
      m = new PrintMessage(data);
    } else {
      return;  // Ignore other tags
    }

    try {
      messageQueue.put(m);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public void startListening(Supplier<Integer> teamNumberSupplier) {
    listener = new Thread(() -> {
      ByteBuffer data = ByteBuffer.allocate(65536);
      DataInputStream in = null;
      while (!Thread.interrupted() && !cleanup.get()) {
        if (in == null || reconnect.getAndSet(false)) {
          lock.lock();
          try {
            while (!autoReconnect) {
              wakeupListener.await();
            }
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
          } finally {
            lock.unlock();
          }
          //logger.log("starting reconnect");
          in = null;
          Integer teamNumber = teamNumberSupplier.get();
          if (teamNumber == null) {
            try {
              // wait a bit so we don't hammer the CPU
              Thread.currentThread().sleep(5000);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              break;
            }
            continue;
          }
          int team = teamNumber.intValue();
          in = connect(team);
          if (in == null) {
            continue;
          }
        }

        if (cleanup.get()) {
          break;
        }

        int tag = -1;
        try {
          tag = readSegment(data, in);
        } catch (IOException e) {
          logger.log("socket disconnected during read");
          lock.lock();
          Consumer<Boolean> connCb = connectedCallback;
          lock.unlock();
          if (connCb != null) {
            connCb.accept(Boolean.FALSE);
          }
          setSocket(null);
          in = null;
          continue;
        }

        handleSegment(tag, data);
      }
      logger.log("exiting listener");
      closeSocket();
    }, "RioConsoleListener");
    listener.setDaemon(true);
    listener.start();
  }

  public void closeSocket() {
    lock.lock();
    Socket s = socket;
    socket = null;
    lock.unlock();
    try {
      if (s != null) {
        s.close();
      }
    } catch (IOException e) {
      // ignore
    }
  }
}
