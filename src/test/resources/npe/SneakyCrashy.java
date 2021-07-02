package npe;

public interface SneakyCrashy {
  <T extends Throwable> void sneakyThrows() throws T;
}