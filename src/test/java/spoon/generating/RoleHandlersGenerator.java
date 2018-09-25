/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.generating;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spoon.SpoonException;
import spoon.metamodel.MMMethod;
import spoon.metamodel.MMMethodKind;
import spoon.metamodel.MetamodelProperty;
import spoon.metamodel.Metamodel;
import spoon.processing.AbstractManualProcessor;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.PrinterHelper;
import spoon.template.Substitution;

public class RoleHandlersGenerator extends AbstractManualProcessor {
	public static final String TARGET_PACKAGE = "spoon.reflect.meta.impl";

	// Class[] helperIfaces = new Class[]{CtBodyHolder.class,
	// CtTypeInformation.class, CtNamedElement.class};
	Map<String, MetamodelProperty> methodsByTypeRoleHandler = new HashMap<>();

	@Override
	public void process() {
		Metamodel metaModel = Metamodel.getInstance();

		//all root super MMFields
		List<MetamodelProperty> superFields = new ArrayList<>();

		metaModel.getConcepts().forEach(mmConcept -> {
			mmConcept.getRoleToProperty().forEach((role, rim) -> {
				addUniqueObject(superFields, rim.getSuperProperty());
			});
		});

		superFields.sort((a, b) -> {
			int d = a.getRole().name().compareTo(b.getRole().name());
			if (d != 0) {
				return d;
			}
			return a.getOwner().getName().compareTo(b.getOwner().getName());
		});
		PrinterHelper concept = new PrinterHelper(getFactory().getEnvironment());
		superFields.forEach(mmField -> {
			concept.write(mmField.getOwner().getName() + " CtRole." + mmField.getRole().name()).writeln().incTab()
			.write("ItemType: ").write(mmField.getTypeOfField().toString()).writeln();
			for (MMMethodKind mk : MMMethodKind.values()) {
				MMMethod mmMethod = mmField.getMethod(mk);
				if (mmMethod != null) {
					concept.write(mk.name()).write(": ").write(mmMethod.getSignature()).write(" : ").write(mmMethod.getReturnType().toString()).writeln();
				}
			}
			concept.decTab();
			concept.write("----------------------------------------------------------").writeln();
		});
		try (Writer w = new OutputStreamWriter(new FileOutputStream(file("target/report/concept.txt")))) {
			w.write(concept.toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		CtType<?> template = getTemplate("spoon.generating.meta.ModelRoleHandlerTemplate");

		CtClass<?> modelRoleHandlersClass = Substitution.createTypeFromTemplate(
				TARGET_PACKAGE + ".ModelRoleHandlers",
				template,
				new HashMap<>());
		CtNewArray<?> roleHandlersFieldExpr = (CtNewArray<?>) modelRoleHandlersClass.getField("roleHandlers").getDefaultExpression();
		superFields.forEach(rim -> {
			Map<String, Object> params = new HashMap<>();
			params.put("$getterName$", rim.getMethod(MMMethodKind.GET).getName());
			if (rim.getMethod(MMMethodKind.SET) != null) {
				params.put("$setterName$", rim.getMethod(MMMethodKind.SET).getName());
			}
			params.put("$Role$", getFactory().Type().createReference(CtRole.class));
			params.put("ROLE", rim.getRole().name());
			params.put("$TargetType$", rim.getOwner().getMetamodelInterface().getReference());
			CtTypeReference<?> nodeRef = rim.getOwner().getMetamodelInterface().getReference();
			CtTypeReference<?> valueTypeRef = fixMainValueType(getRoleHandlerSuperTypeQName(rim).endsWith("SingleHandler") ? rim.getTypeOfField() : rim.getTypeofItems());
			CtTypeReference<?> handlerSuperClassRef = getFactory().Type().createReference(getRoleHandlerSuperTypeQName(rim));
			handlerSuperClassRef.addActualTypeArgument(nodeRef);
			handlerSuperClassRef.addActualTypeArgument(valueTypeRef);
			params.put("AbstractHandler", handlerSuperClassRef);
			params.put("Node", nodeRef);
			params.put("ValueType", valueTypeRef);
			CtClass<?> modelRoleHandlerClass = Substitution.createTypeFromTemplate(
					getHandlerName(rim),
					getTemplate("spoon.generating.meta.RoleHandlerTemplate"),
					params);
			if (rim.getMethod(MMMethodKind.SET) == null) {
				modelRoleHandlerClass.getMethodsByName("setValue").forEach(m -> m.delete());
			}
			modelRoleHandlerClass.addModifier(ModifierKind.STATIC);
			modelRoleHandlersClass.addNestedType(modelRoleHandlerClass);
			roleHandlersFieldExpr.addElement(getFactory().createCodeSnippetExpression("new " + modelRoleHandlerClass.getSimpleName() + "()"));
		});
	}

	private CtTypeReference<?> fixMainValueType(CtTypeReference<?> valueType) {
		valueType = fixValueType(valueType);
		if (valueType instanceof CtWildcardReference) {
			return getFactory().Type().OBJECT;
		}
		return valueType;
	}
	private CtTypeReference<?> fixValueType(CtTypeReference<?> valueType) {
		valueType = valueType.clone();
		if (valueType instanceof CtTypeParameterReference) {
			if (valueType instanceof CtWildcardReference) {
				CtTypeReference<?> boundingType = ((CtWildcardReference) valueType).getBoundingType();
				if (boundingType instanceof CtTypeParameterReference) {
					((CtWildcardReference) valueType).setBoundingType(null);
				}
				return valueType;
			}
			return getFactory().createWildcardReference();
		}
		for (int i = 0; i < valueType.getActualTypeArguments().size(); i++) {
			valueType.getActualTypeArguments().set(i, fixValueType(valueType.getActualTypeArguments().get(i)));
		}
		valueType = valueType.box();
		return valueType;
	}

	private CtType<?> getTemplate(String templateQName) {
		return getFactory().Class().get(templateQName);
	}

	private File file(String name) {
		File f = new File(name);
		f.getParentFile().mkdirs();
		return f;
	}

	private static boolean containsObject(Iterable<? extends Object> iter, Object o) {
		for (Object object : iter) {
			if (object == o) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @param col
	 * @param o
	 * @return true if added
	 */
	private <T> boolean addUniqueObject(Collection<T> col, T o) {
		if (containsObject(col, o)) {
			return false;
		}
		col.add(o);
		return true;
	}

	String getHandlerName(MetamodelProperty field) {
		String typeName = field.getOwner().getName();
		return typeName + "_" + field.getRole().name() + "_RoleHandler";
	}

	public String getRoleHandlerSuperTypeQName(MetamodelProperty field) {
		switch (field.getContainerKind()) {
			case LIST:
				return "spoon.reflect.meta.impl.ListHandler";
			case SET:
				return "spoon.reflect.meta.impl.SetHandler";
			case MAP:
				return "spoon.reflect.meta.impl.MapHandler";
			case SINGLE:
				return "spoon.reflect.meta.impl.SingleHandler";
		}
		throw new SpoonException("Unexpected value container type: " + field.getContainerKind().name());
	}
}
