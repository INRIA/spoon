package spoon.test.ctType;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
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
import spoon.reflect.visitor.filter.NameFilter;
import spoon.test.ctType.testclasses.ErasureModelA;
import spoon.testing.utils.ModelUtils;

import static org.junit.Assert.*;

public class CtTypeParameterTest {

	@Test
	public void testTypeErasure() throws Exception {
		CtClass<?> ctModel = (CtClass<?>) ModelUtils.buildClass(ErasureModelA.class);
		checkType(ctModel);
	}
	
	private void checkType(CtType<?> type) throws NoSuchFieldException, SecurityException {
		List<CtTypeParameter> l_typeParams = type.getFormalCtTypeParameters();
		for (CtTypeParameter ctTypeParameter : l_typeParams) {
			checkTypeParamErasureOfType(ctTypeParameter, type.getActualClass());
		}
		
		for (CtTypeMember typeMemeber : type.getTypeMembers()) {
			if (typeMemeber instanceof CtFormalTypeDeclarer) {
				CtFormalTypeDeclarer ftDecl = (CtFormalTypeDeclarer) typeMemeber;
				l_typeParams = ftDecl.getFormalCtTypeParameters();
				if (typeMemeber instanceof CtExecutable<?>) {
					CtExecutable<?> exec = (CtExecutable<?>) typeMemeber;
					for (CtTypeParameter ctTypeParameter : l_typeParams) {
						checkTypeParamErasureOfExecutable(ctTypeParameter, exec);
					}
				} else if (typeMemeber instanceof CtType<?>) {
					CtType<?> nestedType = (CtType<?>) typeMemeber;
					checkType(nestedType);
				}
			}
		}
	}
	
	private void checkTypeParamErasureOfType(CtTypeParameter typeParam, Class<?> clazz) throws NoSuchFieldException, SecurityException {
		Field field = clazz.getDeclaredField("param"+typeParam.getSimpleName());
		assertEquals("TypeErasure of type param "+getTypeParamIdentification(typeParam), field.getType().getName(), typeParam.getTypeErasure().getQualifiedName());
	}

	private void checkTypeParamErasureOfExecutable(CtTypeParameter typeParam, CtExecutable<?> exec) throws NoSuchFieldException, SecurityException {
		CtParameter<?> param = exec.filterChildren(new NameFilter<>("param"+typeParam.getSimpleName())).first();
		assertNotNull("Missing param"+typeParam.getSimpleName() + " in "+ exec.getSignature(), param);
		int paramIdx = exec.getParameters().indexOf(param);
		Class declClass = exec.getParent(CtType.class).getActualClass();
		Executable declExec;
		if (exec instanceof CtConstructor) {
			declExec = declClass.getDeclaredConstructors()[0];
		} else {
			declExec = declClass.getDeclaredMethods()[0];
		}
		Class<?> paramType = declExec.getParameterTypes()[paramIdx];
		assertEquals("TypeErasure of executable param "+getTypeParamIdentification(typeParam), paramType.getName(), typeParam.getTypeErasure().getQualifiedName());
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
