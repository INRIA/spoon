package spoon.test.ctClass.testclasses.issue1306.internal;

public class BooleanArraysBaseTest {

  protected boolean[] actual;
  protected BooleanArrays arrays;


  public void setUp() {
    actual = spoon.test.ctClass.testclasses.issue1306.test.BooleanArrays.arrayOf(true, false);
    arrays = new BooleanArrays();
  }

}