package netconsole2;

public class DummyMessage implements Message {
  public Type getType() {
    return Type.kDummy;
  }

  public void render(StyledAppendable output, RenderOptions options) {
  }
}
