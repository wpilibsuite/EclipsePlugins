package netconsole2;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RioConnector {
  private Pattern dsPattern = Pattern.compile("\"robotIP\"[^:]*:[^0-9]*([0-9]+)");

  private final Lock lock = new ReentrantLock();
  private final Condition cvDone = lock.newCondition();
  private boolean done = false;
  private final HashMap<String, Thread> attempts = new HashMap<>();
  private Socket socket = null;

  private static final int CONNECTION_TIMEOUT_MS = 2000;
  private static final int MIN_TIMEOUT_MS = 2000;
  private static final int TOTAL_TIMEOUT_SEC = 5;

  private ILogger logger;

  public RioConnector(ILogger logger) {
    this.logger = logger;
  }

  public Socket connect(int team) throws InterruptedException {
    if (team <= 0) {
      return null;
    }
    lock.lock();
    try {
      done = false;
      socket = null;
    } finally {
      lock.unlock();
    }

    // start connection attempts to various address possibilities
    startConnect(new byte[] {10, (byte)(team / 100), (byte)(team % 100), 2});
    startConnect(new byte[] {(byte)127, 0, 0, 1});
    startConnect(new byte[] {(byte)172, 22, 11, 2});
    startConnect("roboRIO-" + team + "-FRC.local");
    startConnect("roboRIO-" + team + "-FRC.lan");
    startConnect("roboRIO-" + team + "-FRC.frc-field.local");
    startDsConnect();
    startTimeDelay(MIN_TIMEOUT_MS);

    // wait for a connection attempt to be successful, or timeout
    lock.lock();
    try {
      while (!done && !attempts.isEmpty()) {
        if (!cvDone.await(TOTAL_TIMEOUT_SEC, TimeUnit.SECONDS)) {
          //logger.log("Connection timed out.");
          return null;
        }
      }

      if (socket == null) {
        //logger.log("Not connected to robot");
        return null;
      }

      logger.log("Connected to robot at " + socket.getInetAddress().getHostAddress());
      return socket;
    } finally {
      lock.unlock();
    }
  }

  private void startDsConnect() {
    startAttempt("DS", new Thread(() -> {
      try {
        // Try to connect to DS on the local machine
        Socket socket = new Socket();
        InetAddress dsAddress = InetAddress.getByAddress(new byte[] {127, 0, 0, 1});
        socket.connect(new InetSocketAddress(dsAddress, 1742), CONNECTION_TIMEOUT_MS);

        // Read JSON "{...}".  This is very limited, does not handle
        // quoted "}" or nested {}, but is sufficient for this purpose.
        InputStream ins = new BufferedInputStream(socket.getInputStream());
        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        int b;
        // Throw away characters until {
        while ((b = ins.read()) >= 0 && b != '{' && !isDone()) {}

        // Read characters until }
        while ((b = ins.read()) >= 0 && b != '}' && !isDone()) {
          buf.write(b);
        }

        if (isDone()) {
          return;
        }

        String json = buf.toString("UTF-8");

        // Look for "robotIP":12345, and get 12345 portion
        Matcher m = dsPattern.matcher(json);
        if (!m.find()) {
          logger.log("DS did not provide robotIP");
          return;
        }
        long ip = 0;
        try {
          ip = Long.parseLong(m.group(1));
        } catch (NumberFormatException e) {
          if (!isDone()) {
            logger.log("DS provided invalid IP: \"" + m.group(1) + "\"", e);
          }
        }

        // If zero, the DS isn't connected to the robot
        if (ip == 0) {
          return;
        }

        // Kick off connection to that address
        InetAddress address = InetAddress.getByAddress(new byte[] {
            (byte)((ip >> 24) & 0xff),
            (byte)((ip >> 16) & 0xff),
            (byte)((ip >> 8) & 0xff),
            (byte)(ip & 0xff)});
        logger.log("DS provided " + address.getHostAddress());
        startConnect(address);
      } catch (Exception e) {
        if (!isDone()) {
          //logger.log("could not get IP from DS");
        }
      } finally {
        finishAttempt("DS");
      }
    }));
  }

  // Address resolution can take a while, so we do this in a separate
  // thread, and then kick off a connect attempt on every IPv4 that
  // resolves.
  private void startConnect(String host) {
    startAttempt(host, new Thread(() -> {
      try {
        for (InetAddress current : InetAddress.getAllByName(host)) {
          if (!current.isMulticastAddress()) {
            if (current instanceof Inet4Address) {
              if (isDone()) {
                return;
              }
              logger.log("resolved " + host + " to " + current.getHostAddress());
              startConnect(current);
            }
          }
        }
      } catch (Exception e) {
        if (!isDone()) {
          //logger.log("could not resolve " + host);
        }
      } finally {
        finishAttempt(host);
      }
    }));
  }

  private void startConnect(InetAddress address) {
    startAttempt(address.getHostAddress(), new Thread(() -> {
      try {
        tryConnect(address);
      } finally {
        finishAttempt(address.getHostAddress());
      }
    }));
  }

  private void startConnect(byte[] address) {
    try {
      startConnect(InetAddress.getByAddress(address));
    } catch (UnknownHostException e) {
      logger.log("Error converting address:" + address + ".", e);
    }
  }

  private void tryConnect(InetAddress address) {
    try {
      Socket socket = new Socket();
      socket.connect(new InetSocketAddress(address, 1741), CONNECTION_TIMEOUT_MS);
      putResult(socket);
    } catch (Exception e) {
      if (!isDone()) {
        //logger.log("Could not connect to " + address.getHostAddress());
      }
    }
  }

  private void startTimeDelay(int timeout) {
    startAttempt("timeout", new Thread(() -> {
      try {
        Thread.currentThread().sleep(timeout);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } finally {
        finishAttempt("timeout");
      }
    }));
  }

  private void startAttempt(String loc, Thread thr) {
    lock.lock();
    try {
      // don't start a new attempt if we're already running one against the
      // same address
      if (attempts.containsKey(loc)) {
        return;
      }
      attempts.put(loc, thr);
    } finally {
      lock.unlock();
    }
    thr.setDaemon(true);
    thr.start();
  }

  private void finishAttempt(String loc) {
    lock.lock();
    try {
      attempts.remove(loc);
      if (attempts.isEmpty()) {
        cvDone.signal();
      }
    } finally {
      lock.unlock();
    }
  }

  private boolean isDone() {
    lock.lock();
    try {
      return done;
    } finally {
      lock.unlock();
    }
  }

  private void putResult(Socket socket) {
    // return result through global and signal
    lock.lock();
    try {
      if (!done) {
        this.socket = socket;
        done = true;
        cvDone.signal();
      }
    } finally {
      lock.unlock();
    }
  }
}

