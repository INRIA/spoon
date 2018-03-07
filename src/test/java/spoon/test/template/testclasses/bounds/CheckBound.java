package spoon.test.template.testclasses.bounds;

import java.util.ArrayList;
import java.util.List;

public class CheckBound {

  List l;

  public void foo() {
    if (new ArrayList<>().size() > 10)
      throw new IndexOutOfBoundsException();
  }

  public void foo2() {
	if (new ArrayList<>().size() > 11)
	  throw new IndexOutOfBoundsException();
  }	

  public void fbar() {
    if (l.size() > 10)
      throw new IndexOutOfBoundsException();
  }

  public void baz() {
    if (new ArrayList<>().size() > 10) {

    }
  }

  public void bou() {
    if (new ArrayList<>().size() > 10) {
      System.out.println();
    }
  }

  public void bov() {
    System.out.println("noise");
    if (new ArrayList<>().size() > 10) // no block
      System.out.println();
  }

  public void bos() {
	  if (new ArrayList<>().size() == new ArrayList<>().size()) // same expressions
	      System.out.println();
  }

}