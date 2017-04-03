package spoon.test.ctType;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtFormalTypeDeclarer;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.test.ctType.testclasses.ErasureModelA;
import spoon.testing.utils.ModelUtils;

import static org.junit.Assert.*;

public class CtTypeParameterTest {

	@Test
	public void testTypeErasure() throws Exception {
		//contract: the type erasure computed by getTypeErasure is same as the one computed by the Java compiler
		CtClass<?> ctModel = (CtClass<?>) ModelUtils.buildClass(ErasureModelA.class);
		//visit all methods of type ctModel
		//visit all inner types and their methods recursively `getTypeErasure` returns expected value
		//for each formal type parameter or method parameter check if 
		checkType(ctModel);
	}
	
	private void checkType(CtType<?> type) throws NoSuchFieldException, SecurityException {
		List<CtTypeParameter> formalTypeParameters = type.getFormalCtTypeParameters();
		for (CtTypeParameter ctTypeParameter : formalTypeParameters) {
			checkTypeParamErasureOfType(ctTypeParameter, type.getActualClass());
		}
		
		for (CtTypeMember member : type.getTypeMembers()) {
			if (member instanceof CtFormalTypeDeclarer) {
				CtFormalTypeDeclarer ftDecl = (CtFormalTypeDeclarer) member;
				formalTypeParameters = ftDecl.getFormalCtTypeParameters();
				if (member instanceof CtExecutable<?>) {
					CtExecutable<?> exec = (CtExecutable<?>) member;
					for (CtTypeParameter ctTypeParameter : formalTypeParameters) {
						checkTypeParamErasureOfExecutable(ctTypeParameter);
					}
					for (CtParameter<?> param : exec.getParameters()) {
						checkParameterErasureOfExecutable(param);
					}
				} else if (member instanceof CtType<?>) {
					CtType<?> nestedType = (CtType<?>) member;
					// recursive call for nested type
					checkType(nestedType);
				}
			}
		}
	}
	
	private void checkTypeParamErasureOfType(CtTypeParameter typeParam, Class<?> clazz) throws NoSuchFieldException, SecurityException {
		Field field = clazz.getDeclaredField("param"+typeParam.getSimpleName());
		assertEquals("TypeErasure of type param "+getTypeParamIdentification(typeParam), field.getType().getName(), typeParam.getTypeErasure().getQualifiedName());
	}

	private void checkTypeParamErasureOfExecutable(CtTypeParameter typeParam) throws NoSuchFieldException, SecurityException {
		CtExecutable<?> exec = (CtExecutable<?>) typeParam.getParent();
		CtParameter<?> param = exec.filterChildren(new NameFilter<>("param"+typeParam.getSimpleName())).first();
		assertNotNull("Missing param"+typeParam.getSimpleName() + " in "+ exec.getSignature(), param);
		int paramIdx = exec.getParameters().indexOf(param);
		Class declClass = exec.getParent(CtType.class).getActualClass();
		Executable declExec;
		if (exec instanceof CtConstructor) {
			declExec = declClass.getDeclaredConstructors()[0];
		} else {
			declExec = getMethodByName(declClass, exec.getSimpleName());
		}
		Class<?> paramType = declExec.getParameterTypes()[paramIdx];
		// contract the type erasure given with Java reflection is the same as the one computed by spoon
		assertEquals("TypeErasure of executable param "+getTypeParamIdentification(typeParam), paramType.getName(), typeParam.getTypeErasure().toString());
	}
	
	private void checkParameterErasureOfExecutable(CtParameter<?> param) {
		CtExecutable<?> exec = param.getParent();
		CtTypeReference<?> typeErasure = param.getType().getTypeErasure();
		int paramIdx = exec.getParameters().indexOf(param);
		Class declClass = exec.getParent(CtType.class).getActualClass();
		Executable declExec;
		if (exec instanceof CtConstructor) {
			declExec = declClass.getDeclaredConstructors()[0];
		} else {
			declExec = getMethodByName(declClass, exec.getSimpleName());
		}
		Class<?> paramType = declExec.getParameterTypes()[paramIdx];
		assertEquals(0, typeErasure.getActualTypeArguments().size());
		// contract the type erasure of the method parameter given with Java reflection is the same as the one computed by spoon
		assertEquals("TypeErasure of executable "+exec.getSignature()+" parameter "+param.getSimpleName(), paramType.getName(), typeErasure.getQualifiedName());
	}
	
	
	private Executable getMethodByName(Class declClass, String simpleName) {
		for (Method method : declClass.getDeclaredMethods()) {
			if(method.getName().equals(simpleName)) {
				return method;
			}
		}
		fail("Method "+simpleName+" not found in "+declClass.getName());
		return null;
	}

	private String getTypeParamIdentification(CtTypeParameter typeParam) {
		String result = "<"+typeParam.getSimpleName()+">";
		CtFormalTypeDeclarer l_decl = typeParam.getParent(CtFormalTypeDeclarer.class);
		if (l_decl instanceof CtType) {
			return ((CtType) l_decl).getQualifiedName()+result;
		}
		if (l_decl instanceof CtExecutable) {
			CtExecutable exec = (CtExecutable) l_decl;
			if (exec instanceof CtMethod) {
				result=exec.getSignature()+result;
			}
			return exec.getParent(CtType.class).getQualifiedName()+"#"+result;
		}
		throw new AssertionError();
	}
}
