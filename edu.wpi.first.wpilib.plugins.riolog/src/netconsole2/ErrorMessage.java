package netconsole2;

import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.util.Formatter;
import java.util.Locale;

public class ErrorMessage implements Message {
  public final float timestamp;
  public final int seqNumber;
  public final int numOccur;
  public final int errorCode;
  public final byte flags;
  public final String details;
  public final String location;
  public final String callStack;

  public ErrorMessage(ByteBuffer data) {
    data.rewind();
    timestamp = data.getFloat();
    seqNumber = data.getShort() & 0xffff;
    numOccur = data.getShort() & 0xffff;
    errorCode = data.getInt();
    flags = data.get();
    details = getSizedString(data);
    location = getSizedString(data);
    callStack = getSizedString(data);
  }

  private String getSizedString(ByteBuffer data) {
    int size = data.getShort() & 0xffff;
    ByteBuffer buf = data.slice();
    buf.limit(size);
    data.position(data.position() + size);
    return StandardCharsets.UTF_8.decode(buf).toString();
  }

  public Type getType() {
    if ((flags & 1) != 0) {
      return Type.kError;
    } else {
      return Type.kWarning;
    }
  }

  public boolean isError() {
    return getType() == Type.kError;
  }

  public boolean isWarning() {
    return getType() == Type.kWarning;
  }

  public void render(StyledAppendable output, RenderOptions options) {
    // timestamp
    if (options.showTimestamp) {
      output.startStyle(kStyleTimestamp);
      Formatter formatter = new Formatter(output, Locale.US);
      formatter.format(options.timestampFormat, timestamp);
      output.endStyle();
      output.append('\t');
      output = new IndentAppendable(output, "\t\t");
    }

    // error/warning label
    if (isError()) {
      output.startStyle(kStyleError);
      output.append("ERROR");
    } else {
      output.startStyle(kStyleWarning);
      output.append("WARNING");
    }
    output.endStyle();

    // error code
    if (options.showErrorCode) {
      output.append("  ");
      output.startStyle(kStyleErrorCode);
      output.append(String.valueOf(errorCode));
      output.endStyle();
    }

    // error details
    output.append("  ");
    output.startStyle(kStyleDetails);
    output.append(details);
    output.endStyle();

    // error location
    if ((isError() && options.showErrorLocation) || (isWarning() && options.showWarningLocation)) {
      output.append("  ");
      output.startStyle(kStyleLocation);
      output.append(location);
      output.endStyle();
    }

    // call stack
    if ((isError() && options.showErrorCallStack) || (isWarning() && options.showWarningCallStack)) {
      output.append('\n');
      output.startStyle(kStyleCallStack);
      output.append(callStack);
      output.endStyle();
    }
  }
}
