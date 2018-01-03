package netconsole2.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.BlockingQueue;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

import edu.wpi.first.wpilib.plugins.core.WPILibCore;

import netconsole2.Activator;
import netconsole2.ErrorMessage;
import netconsole2.ILogger;
import netconsole2.Message;
import netconsole2.PrintMessage;
import netconsole2.RioConsole;

public class RiologView extends ViewPart {
  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "netconsole2.views.RiologView";

  public static final String PRINT_FONT = "edu.wpi.first.wpilib.plugins.riolog.printFont";
  public static final String PRINT_BACKGROUND = "edu.wpi.first.wpilib.plugins.riolog.printBackgroundColor";
  public static final String PRINT_FOREGROUND = "edu.wpi.first.wpilib.plugins.riolog.printForegroundColor";
  public static final String ERROR_FOREGROUND = "edu.wpi.first.wpilib.plugins.riolog.errorForegroundColor";
  public static final String WARNING_FOREGROUND = "edu.wpi.first.wpilib.plugins.riolog.warningForegroundColor";
  public static final String DETAILS_FOREGROUND = "edu.wpi.first.wpilib.plugins.riolog.detailsForegroundColor";
  public static final String LOCATION_FOREGROUND = "edu.wpi.first.wpilib.plugins.riolog.locationForegroundColor";
  public static final String CALLSTACK_FOREGROUND = "edu.wpi.first.wpilib.plugins.riolog.callStackForegroundColor";

  private static final int MAX_LINES = 10000;
  private static final int EXTRA_LINES = 100;

  private final ILogger logger;
  private final RioConsole rioConsole;

  private final Message.RenderOptions renderOptions = new Message.RenderOptions();
  private final StyleRange[] styles = new StyleRange[9];

  private StyledText text;
  private Thread transferer;

  private final Lock lock = new ReentrantLock();

  private Image greenIcon;
  private Image redIcon;

  private Label connectionLabel = null;
  private Action autoReconnectAction;
  private Action clearAction;
  private Action pauseAction;
  private Action discardAction;
  private Action showWarningAction;
  private Action showPrintAction;
  private Action showTimestampAction;
  private Button pauseButton;
  private Button discardButton;
  private Button showWarningButton;
  private Button showPrintButton;

  /**
   * The constructor.
   */
  public RiologView() {
    logger = Activator.getDefault();
    rioConsole = new RioConsole(logger);

    // initialize styles
    styles[Message.kStyleTimestamp] = new StyleRange();
    styles[Message.kStylePrint] = new StyleRange();
    styles[Message.kStyleError] = new StyleRange();
    styles[Message.kStyleWarning] = new StyleRange();
    styles[Message.kStyleErrorCode] = new StyleRange();
    styles[Message.kStyleDetails] = new StyleRange();
    styles[Message.kStyleLocation] = new StyleRange();
    styles[Message.kStyleCallStack] = new StyleRange();
  }

  private void contributeToActionBars() {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  /**
   * This is a callback that will allow us to create the viewer and initialize
   * it.
   */
  public void createPartControl(Composite parent) {
    GridLayout glayout = new GridLayout();
    glayout.numColumns = 1;
    parent.setLayout(glayout);

    IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
    ITheme theme = themeManager.getCurrentTheme();
    ColorRegistry colorRegistry = theme.getColorRegistry();
    FontRegistry fontRegistry = theme.getFontRegistry();

    styles[Message.kStylePrint].foreground = colorRegistry.get(PRINT_FOREGROUND);
    styles[Message.kStyleError].foreground = colorRegistry.get(ERROR_FOREGROUND);
    styles[Message.kStyleError].fontStyle = SWT.BOLD;
    styles[Message.kStyleWarning].foreground = colorRegistry.get(WARNING_FOREGROUND);
    styles[Message.kStyleWarning].fontStyle = SWT.BOLD;
    styles[Message.kStyleDetails].foreground = colorRegistry.get(DETAILS_FOREGROUND);
    styles[Message.kStyleLocation].foreground = colorRegistry.get(LOCATION_FOREGROUND);
    styles[Message.kStyleCallStack].foreground = colorRegistry.get(CALLSTACK_FOREGROUND);

    text = new StyledText(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
    {
      GridData gdata = new GridData();
      gdata.grabExcessVerticalSpace = true;
      gdata.grabExcessHorizontalSpace = true;
      gdata.horizontalAlignment = SWT.FILL;
      gdata.verticalAlignment = SWT.FILL;
      text.setLayoutData(gdata);
    }
    text.setBackground(colorRegistry.get(PRINT_BACKGROUND));
    text.setForeground(styles[Message.kStylePrint].foreground);
    text.setFont(fontRegistry.get(PRINT_FONT));
    text.setText("This is the robot console. Print statements and error messages will appear here.\nConsole output requires a Driver Station to be connected to the robot.\n\n");

    themeManager.addPropertyChangeListener(new IPropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty().equals(PRINT_FONT)) {
          text.setFont(((FontRegistry) event.getSource()).get(PRINT_FONT));
        } else if (event.getProperty().equals(PRINT_BACKGROUND)) {
          Color color = ((ColorRegistry) event.getSource()).get(PRINT_BACKGROUND);
          text.setBackground(color);
        } else if (event.getProperty().equals(PRINT_FOREGROUND)) {
          Color color = ((ColorRegistry) event.getSource()).get(PRINT_FOREGROUND);
          text.setForeground(color);
        } else if (event.getProperty().equals(ERROR_FOREGROUND)) {
          styles[Message.kStyleError].foreground = ((ColorRegistry) event.getSource()).get(ERROR_FOREGROUND);
        } else if (event.getProperty().equals(WARNING_FOREGROUND)) {
          styles[Message.kStyleWarning].foreground = ((ColorRegistry) event.getSource()).get(WARNING_FOREGROUND);
        } else if (event.getProperty().equals(DETAILS_FOREGROUND)) {
          styles[Message.kStyleDetails].foreground = ((ColorRegistry) event.getSource()).get(DETAILS_FOREGROUND);
        } else if (event.getProperty().equals(LOCATION_FOREGROUND)) {
          styles[Message.kStyleLocation].foreground = ((ColorRegistry) event.getSource()).get(LOCATION_FOREGROUND);
        } else if (event.getProperty().equals(CALLSTACK_FOREGROUND)) {
          styles[Message.kStyleCallStack].foreground = ((ColorRegistry) event.getSource()).get(CALLSTACK_FOREGROUND);
        } else {
          return;
        }
        rioConsole.putDummyMessage();
      }
    });

    Composite row = new Composite(parent, SWT.NONE);

    row.setLayout(new FillLayout(SWT.HORIZONTAL));

    {
      GridData gdata = new GridData();
      gdata.grabExcessVerticalSpace = false;
      gdata.grabExcessHorizontalSpace = true;
      gdata.horizontalAlignment = SWT.FILL;
      gdata.verticalAlignment = SWT.CENTER;
      row.setLayoutData(gdata);
    }

    // Create the help context id for the viewer's control
    PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, "netconsole2.text");
    loadIcons();
    makeActions();
    makeButtons(row);
    hookContextMenu();
    contributeToActionBars();
    startListening();
  }

  private void loadIcons() {
    greenIcon = Activator.getImageDescriptor("icons/Aqua-Ball-Green-icon-16x16.png").createImage();
    redIcon = Activator.getImageDescriptor("icons/Aqua-Ball-Red-icon-16x16.png").createImage();
  }

  private void makeButtons(Composite parent) {
    pauseButton = new Button(parent, SWT.TOGGLE);
    pauseButton.setText(pauseAction.getText());
    pauseButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        pauseAction.setChecked(pauseButton.getSelection());
        pauseAction.run();
      }
    });
    discardButton = new Button(parent, SWT.TOGGLE);
    discardButton.setText(discardAction.getText());
    discardButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        discardAction.setChecked(discardButton.getSelection());
        discardAction.run();
      }
    });
    showWarningButton = new Button(parent, SWT.TOGGLE);
    showWarningButton.setText(showWarningAction.getText());
    showWarningButton.setSelection(true);
    showWarningButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        showWarningAction.setChecked(showWarningButton.getSelection());
        showWarningAction.run();
      }
    });
    showPrintButton = new Button(parent, SWT.TOGGLE);
    showPrintButton.setText(showPrintAction.getText());
    showPrintButton.setSelection(true);
    showPrintButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        showPrintAction.setChecked(showPrintButton.getSelection());
        showPrintAction.run();
      }
    });
    Button clearButton = new Button(parent, SWT.PUSH);
    clearButton.setText(clearAction.getText());
    clearButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        clearAction.run();
      }
    });
  }

  @Override
  public void dispose() {
    rioConsole.stop();
    if (transferer != null) {
      transferer.interrupt();
    }
    super.dispose();
  }

  private void fillContextMenu(IMenuManager manager) {
    manager.add(pauseAction);
    manager.add(discardAction);
    manager.add(showWarningAction);
    manager.add(showPrintAction);
    manager.add(new Separator());
    manager.add(clearAction);
    manager.add(new Separator());

    Action copyAction = new Action("Copy") {
      @Override
      public void run() {
        text.copy();
      }
    };
    copyAction.setToolTipText("Copy selected text");
    copyAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
        .getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
    manager.add(copyAction);

    Action selectAllAction = new Action("Select All") {
      @Override
      public void run() {
        text.selectAll();
      }
    };
    selectAllAction.setToolTipText("Select all text");
    manager.add(selectAllAction);
  }

  private void fillLocalPullDown(IMenuManager manager) {
    manager.add(pauseAction);
    manager.add(discardAction);
    manager.add(showWarningAction);
    manager.add(showPrintAction);
    manager.add(new Separator());
    manager.add(clearAction);
  }

  private void fillLocalToolBar(IToolBarManager manager) {
    manager.add(new ControlContribution("connection status") {
      @Override
      protected Control createControl(Composite parent) {
        connectionLabel = new Label(parent, SWT.NONE);
        connectionLabel.setImage(redIcon);
        return connectionLabel;
      }
    });
    manager.add(autoReconnectAction);
    manager.add(new Separator());
    manager.add(pauseAction);
    manager.add(discardAction);
    manager.add(showWarningAction);
    manager.add(showPrintAction);
    manager.add(showTimestampAction);
    manager.add(new Separator());
    manager.add(clearAction);
  }

  private void hookContextMenu() {
    MenuManager menuMgr = new MenuManager("#PopupMenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {
      @Override
      public void menuAboutToShow(IMenuManager manager) {
        RiologView.this.fillContextMenu(manager);
      }
    });
    Menu menu = menuMgr.createContextMenu(text);
    text.setMenu(menu);
  }

  private void makeActions() {
    autoReconnectAction = new Action("Automatically Reconnect") {
      @Override
      public void run() {
        final boolean checked = isChecked();
        rioConsole.setAutoReconnect(checked);
        if (!checked) {
          rioConsole.closeSocket();
        }
      }
    };
    autoReconnectAction.setToolTipText("Automatically reconnect to the robot");
    autoReconnectAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
        .getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
    autoReconnectAction.setChecked(true);

    clearAction = new Action("Clear Log") {
      @Override
      public void run() {
        text.setText("");
      }
    };
    clearAction.setToolTipText("Empty the textbox");
    clearAction.setImageDescriptor(Activator.getImageDescriptor("icons/clear_co.png"));

    pauseAction = new Action("Pause Display") {
      @Override
      public void run() {
        final boolean checked = isChecked();
        lock.lock();
        try {
          boolean wasPaused = rioConsole.setPaused(checked);
          if (checked && !wasPaused) {
            pauseButton.setText("Paused (0 Messages)");
          } else if (!checked && wasPaused) {
            pauseButton.setText("Pause Display");
            rioConsole.putDummyMessage();
          }
        } finally {
          lock.unlock();
        }
        pauseButton.setSelection(checked);
      }
    };
    pauseAction.setToolTipText("Stop adding messages to the textbox");
    pauseAction.setImageDescriptor(Activator.getImageDescriptor("icons/lock_co.png"));
    pauseAction.setChecked(false);

    discardAction = new Action("Discard Incoming") {
      @Override
      public void run() {
        final boolean checked = isChecked();
        rioConsole.setDiscard(checked);
        discardButton.setSelection(checked);
      }
    };
    discardAction.setToolTipText("Drop all incoming messages");
    discardAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
        .getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE));
    discardAction.setChecked(false);

    showWarningAction = new Action("Show Warnings") {
      @Override
      public void run() {
        final boolean checked = isChecked();
        rioConsole.setShowWarning(checked);
        showWarningButton.setSelection(checked);
      }
    };
    showWarningAction.setToolTipText("Show warning messages");
    showWarningAction.setImageDescriptor(Activator.getImageDescriptor("icons/warning-orange-16x16.png"));
    showWarningAction.setChecked(true);

    showPrintAction = new Action("Show Prints") {
      @Override
      public void run() {
        final boolean checked = isChecked();
        rioConsole.setShowPrint(checked);
        showPrintButton.setSelection(checked);
      }
    };
    showPrintAction.setToolTipText("Show console print messages");
    showPrintAction.setImageDescriptor(Activator.getImageDescriptor("icons/console_view.png"));
    showPrintAction.setChecked(true);

    showTimestampAction = new Action("Show Timestamps") {
      @Override
      public void run() {
        lock.lock();
        try {
	  renderOptions.showTimestamp = isChecked();
        } finally {
          lock.unlock();
        }
      }
    };
    showTimestampAction.setToolTipText("Show message timestamps");
    showTimestampAction.setImageDescriptor(Activator.getImageDescriptor("icons/stopwatch.png"));
    showTimestampAction.setChecked(renderOptions.showTimestamp);
  }

  /**
   * Passing the focus request to the viewer's control.
   */
  public void setFocus() {
    text.setFocus();
  }

  private static class StyledTextBuilder implements Message.StyledAppendable {
    private final StyleRange[] styles;
    private final ArrayList<Integer> ranges = new ArrayList<>();
    private final ArrayList<StyleRange> styleRanges = new ArrayList<>();
    private final StringBuilder builder = new StringBuilder();
    private int styleStart = -1;

    public StyledTextBuilder(StyleRange[] styles) {
      this.styles = styles;
    }

    public void toStyledText(StyledText text) {
      if (styleStart != -1) {
        endStyle();
      }
      final int textStart = text.getCharCount();
      final String str = builder.toString();
      text.append(str);
      final int[] intRanges = ranges.stream().mapToInt(Integer::intValue).toArray();
      for (int i = 0; i < intRanges.length; i += 2) {
        intRanges[i] += textStart;
      }
      final StyleRange[] styleRangesArray = styleRanges.toArray(new StyleRange[styleRanges.size()]);
      text.setStyleRanges(textStart, str.length(), intRanges, styleRangesArray);
    }

    @Override
    public Message.StyledAppendable append(char c) {
      builder.append(c);
      return this;
    }

    @Override
    public Message.StyledAppendable append(CharSequence csq) {
      builder.append(csq);
      return this;
    }

    @Override
    public Message.StyledAppendable append(CharSequence csq, int start, int end) {
      builder.append(csq, start, end);
      return this;
    }

    @Override
    public Message.StyledAppendable startStyle(int style) {
      if (style >= styles.length) {
        return this;
      }
      StyleRange styleRange = styles[style];
      if (styleRange == null) {
        return this;
      }
      styleRanges.add(styleRange);
      if (styleStart != -1) {
        endStyle();
      }
      styleStart = builder.length();  // start
      ranges.add(styleStart);
      return this;
    }

    @Override
    public Message.StyledAppendable endStyle() {
      if (styleStart != -1) {
        ranges.add(builder.length() - styleStart);  // length
        styleStart = -1;
      }
      return this;
    }
  }

  private void startListening() {
    rioConsole.setConnectedCallback(connected -> {
      Display.getDefault().asyncExec(() -> {
        //logger.log("setting green icon");
        if (connectionLabel != null) {
          connectionLabel.setImage(connected.booleanValue() ? greenIcon : redIcon);
        }
      });
    });

    rioConsole.startListening(() -> {
      String teamStr = WPILibCore.getDefault().getProjectProperties(null).getProperty("team-number", "0");
      try {
        return Integer.valueOf(teamStr, 10);
      } catch (NumberFormatException e) {
        logger.log("could not parse team \"" + teamStr + "\"");
        return null;
      }
    });

    transferer = new Thread(() -> {
      final BlockingQueue<Message> messageQueue = rioConsole.getMessageQueue();
      final ArrayList<Message> temp = new ArrayList<>();

      while (!Thread.interrupted()) {
        Message msg;
        try {
          // it's more efficient to batch updates, so sleep a little here
          Thread.currentThread().sleep(30);
          msg = messageQueue.take();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
        temp.add(msg);
        messageQueue.drainTo(temp);
        Display.getDefault().syncExec(() -> {
          if (text.isDisposed())
            return;
          if (rioConsole.getPaused()) {
            if (temp.size() == 1) {
              pauseButton.setText("Paused (1 Message)\u2002");
            } else {
              pauseButton.setText("Paused (" + String.valueOf(temp.size()) + " Messages)");
            }
          } else {
            StyledTextBuilder builder = new StyledTextBuilder(styles);
            for (Message m : temp) {
              m.render(builder, renderOptions);
              builder.append('\n');
            }
            builder.toStyledText(text);
            // limit total number of lines
            if (text.getLineCount() > (MAX_LINES + EXTRA_LINES)) {
              text.replaceTextRange(0, text.getOffsetAtLine(text.getLineCount() - MAX_LINES), "");
            }
            text.setTopIndex(text.getLineCount() - 1);
            temp.clear();
          }
        });
      }
    }, "Riolog-Transfer");
    transferer.setDaemon(true);
    transferer.start();
  }
}
