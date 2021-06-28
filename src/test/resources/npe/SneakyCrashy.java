package npe;

public class SneakyCrashy {

  @SuppressWarnings("unchecked")
  public static <T extends Throwable> void uncheckedThrow(Throwable t) throws T {
    if (t != null)
      throw (T)t; // rely on vacuous cast
    else
      throw new Error("Unknown Exception");
  }

}