package netconsole2;

public interface ILogger {
  void log(String msg);
  void log(String msg, Exception e);
}
