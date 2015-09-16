import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.test.parent.Foo;

public class Bar {
	private <T extends CtElement> T get(Tacos<T> elemType) {
		CtClass<Object> fooClass = factory.Class().get(Foo.class);
		CtMethod nullParent = fooClass.getMethodsByName("nullParent").get(0);
		return (T) nullParent.getBody().getElements(elemType::isInstance).get(0);
	}
}