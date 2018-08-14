package spoon.test.method_overriding;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.junit.Test;

import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.method_overriding.testclasses2.ObjectInterface;
import spoon.testing.utils.ModelUtils;

import static org.junit.Assert.*;

public class MethodOverriddingTest {

	@Test
	public void testShadowInterfaceMethodsCanOverrideObjectMethods() {
		//contract: Interface (made by reflection) method equals overrides Object#equals
		Factory f = new Launcher().getFactory();
		CtType<?> iface = f.Interface().get(Comparator.class);
		assertTrue(iface.isShadow());
		CtMethod<?> comparatorEquals = iface.getMethodsByName("equals").get(0);
		
		CtType<?> object = f.Class().get(Object.class);
		assertTrue(object.isShadow());
		CtMethod<?> objectEquals = object.getMethodsByName("equals").get(0);
		
		assertTrue(comparatorEquals.isOverriding(objectEquals));
	}

	@Test
	public void testInterfaceMethodsCanOverrideObjectMethods() throws Exception {
		//contract: Interface (made from sources) method equals overrides Object#equals
		Launcher launcher = new Launcher();
		Factory f = launcher.getFactory();
		SpoonModelBuilder comp = launcher.createCompiler();
		comp.addInputSources(SpoonResourceHelper.resources("./src/test/java/spoon/test/method_overriding/testclasses2/ObjectInterface.java"));
		comp.build();
		CtType<?> iface = f.Interface().get(ObjectInterface.class);
		assertFalse(iface.isShadow());
		CtMethod<?> ifaceEquals = iface.getMethodsByName("equals").get(0);
		
		CtType<?> object = iface.getFactory().Class().get(Object.class);
		assertTrue(object.isShadow());
		CtMethod<?> objectEquals = object.getMethodsByName("equals").get(0);
		
		assertTrue(ifaceEquals.isOverriding(objectEquals));
	}

	@Test
	public void testMethodOverride() {
		checkMethodOverride((m1, m2)->m1.isOverriding(m2));
	}

	@Test
	public void testMethodOverrideByReference() {
		checkMethodOverride((m1, m2)->m1.getReference().isOverriding(m2.getReference()));
	}

	private void checkMethodOverride(BiFunction<CtMethod<?>, CtMethod<?>, Boolean> isOverriding) {
		Factory factory = ModelUtils.build(new File("src/test/java/spoon/test/method_overriding/testclasses").listFiles());
		Map<String, List<CtMethod>> methodsByName = new HashMap<>();
		factory.getModel().filterChildren(new TypeFilter<>(CtMethod.class)).forEach((CtMethod m)->{
			List<CtMethod> methods = methodsByName.get(m.getSimpleName());
			if(methods==null) {
				methods = new ArrayList<>();
				methodsByName.put(m.getSimpleName(), methods);
			}
			methods.add(m);
		});
		assertFalse(methodsByName.isEmpty());
		for (Map.Entry<String, List<CtMethod>> e : methodsByName.entrySet()) {
			combine(e.getValue(), 0, isOverriding);
		}
	}

	private void combine(List<CtMethod> value, int start, BiFunction<CtMethod<?>, CtMethod<?>, Boolean> isOverriding) {
		CtMethod m1 = value.get(start);
		if(start+1 < value.size()) {
			for (CtMethod m2 : value.subList(start+1, value.size())) {
				if(m1.getDeclaringType().isSubtypeOf(m2.getDeclaringType().getReference())) {
					checkOverride(m1, m2, isOverriding);
				} else if(m2.getDeclaringType().isSubtypeOf(m1.getDeclaringType().getReference())) {
					checkOverride(m2, m1, isOverriding);
				} else {
					checkNotOverride(m1, m2, isOverriding);
				}
			}
			combine(value, start+1, isOverriding);
		}
	}

	private void checkOverride(CtMethod m1, CtMethod m2, BiFunction<CtMethod<?>, CtMethod<?>, Boolean> isOverriding) {
		assertTrue(descr(m1)+" overriding "+descr(m2), 		isOverriding.apply(m1, m2));
		assertFalse(descr(m2)+" NOT overriding "+descr(m1), isOverriding.apply(m2, m1));
	}

	private void checkNotOverride(CtMethod m1, CtMethod m2, BiFunction<CtMethod<?>, CtMethod<?>, Boolean> isOverriding) {
		assertFalse(descr(m1)+" NOT overriding "+descr(m2), isOverriding.apply(m1, m2));
		assertFalse(descr(m2)+" NOT overriding "+descr(m1), isOverriding.apply(m2, m1));
	}

	private String descr(CtMethod m) {
		return m.getDeclaringType().getSimpleName()+"#"+m.getSimpleName();
	}
}
