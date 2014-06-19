package spoon.test.parent;

import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.factory.Factory;

public class ParentTest {
 @Test
 public void testParent() throws Exception {
   // toString should not throw a parent exception even if parents are not set
   try {
     Launcher spoon = new Launcher();
     Factory factory = spoon.createFactory();
     CtLiteral<Object> literal = factory.Core().createLiteral();
     literal.setValue(1);
     CtBinaryOperator minus = factory.Core().createBinaryOperator();
     minus.setKind(BinaryOperatorKind.MINUS);
     minus.setRightHandOperand(literal);
     minus.setLeftHandOperand(literal);
     System.out.println(minus.toString());
   } catch (Exception e) {
       Assert.fail();
   }
 }
}
