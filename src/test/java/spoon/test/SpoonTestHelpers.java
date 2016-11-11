package spoon.test;

import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.OverridingMethodFilter;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SpoonTestHelpers {
	// only static methods
	private SpoonTestHelpers(){
	}

	public static List<CtType<? extends CtElement>> getAllInstantiableMetamodelInterfaces() {
		List<CtType<? extends CtElement>> result = new ArrayList<>();
		SpoonAPI interfaces = new Launcher();
		interfaces.addInputResource("src/main/java/spoon/reflect/declaration");
		interfaces.addInputResource("src/main/java/spoon/reflect/code");
		interfaces.addInputResource("src/main/java/spoon/reflect/reference");
		interfaces.buildModel();

		SpoonAPI implementations = new Launcher();
		implementations.addInputResource("src/main/java/spoon/support/reflect/declaration");
		implementations.addInputResource("src/main/java/spoon/support/reflect/code");
		implementations.addInputResource("src/main/java/spoon/support/reflect/reference");
		implementations.buildModel();

		for(CtType<? > itf : interfaces.getModel().getAllTypes()) {
			String impl = itf.getQualifiedName().replace("spoon.reflect", "spoon.support.reflect")+"Impl";
			CtType implClass = implementations.getFactory().Type().get(impl);
			if (implClass != null && !implClass.hasModifier(ModifierKind.ABSTRACT)) {
				result.add((CtType<? extends CtElement>) itf);
			}
		}
		return result;
	}

	/**
	 * returns true if typeReference point to a class of the metamodel or a List/set of a class of the metamodel.
	 */
	public static boolean isMetamodelRelatedType(CtTypeReference<?> typeReference) {
		CtTypeReference<Object> ctElRef = typeReference.getFactory().Code().createCtTypeReference(CtElement.class);

		// simple case, a sublcass of CtElement
		if (typeReference.isSubtypeOf(ctElRef)) {
			return true;
		}
		// limit case because of a bug to be fixed
		if (typeReference.getActualTypeArguments().size()>0 && "?".equals(typeReference.getActualTypeArguments()
				.get(0).getQualifiedName())) {
			return false;
		}
		return (typeReference.getActualTypeArguments().size()>0
				&& typeReference.getActualTypeArguments()
				.get(0).getTypeDeclaration()
				.isSubtypeOf(ctElRef))
				;
	}

	/** returns the corresponding setter, if several are possible returns the lowest one in the hierarchy */
	public static CtMethod<?> getSetterOf(CtType<?> baseType, CtMethod<?> getter) {
		String setterName = getter.getSimpleName().replaceFirst("^get", "set");
		Object[] tentativeSetters = baseType.getAllMethods().stream().filter(x->x.getSimpleName().equals(setterName)).toArray();
		if (tentativeSetters.length==0) {
			return null;
		}

		// return one that is as low as possible in the hierarchy
		for(Object o : tentativeSetters) {
			if (baseType.getPackage().getElements(new OverridingMethodFilter((CtMethod<?>) o)).size() == 0) {
				return (CtMethod<?>) o;
			}
		}

		//System.out.println(setterName+" "+tentativeSetters.length);
		return (CtMethod<?>) tentativeSetters[0];
	}

	/** specifies what a metamodel property is: a getter than returns a metamodel-related class and that is not derived */
	public static boolean isMetamodelProperty(CtType<?> baseType, CtMethod<?> m) {
		CtMethod<?> correspondingSetter = getSetterOf(baseType, m);
		return
				m.getSimpleName().startsWith("get")
						&& m.getParameters().size() == 0 // a getter has no parameter
						&& m.getAnnotation(DerivedProperty.class) == null
						&& (correspondingSetter == null || correspondingSetter.getAnnotation(UnsettableProperty.class) == null)
						&&
						// return type
						isMetamodelRelatedType(m.getType());
	}

}
