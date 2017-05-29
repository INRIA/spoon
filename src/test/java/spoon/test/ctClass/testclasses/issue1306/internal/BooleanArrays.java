package spoon.test.ctClass.testclasses.issue1306.internal;


/**
 * Reusable assertions for arrays of {@code boolean}s.
 * 
 * @author Alex Ruiz
 * @author Joel Costigliola
 * @author Mikhail Mazursky
 * @author Nicolas François
 */
public class BooleanArrays {

  private static final BooleanArrays INSTANCE = new BooleanArrays();

  /**
   * Returns the singleton instance of this class.
   * 
   * @return the singleton instance of this class.
   */
  public static BooleanArrays instance() {
    return INSTANCE;
  }

  BooleanArrays() {
  }

}
