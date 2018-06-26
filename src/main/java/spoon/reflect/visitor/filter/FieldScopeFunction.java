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
package spoon.reflect.visitor.filter;

import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;

/**
 * This Query expects a {@link CtField} as input
 * and returns all CtElements,
 * which are in visibility scope of that field.
 * In other words, it returns all elements,
 * which might be reference to that field.
 * <br>
 * It can be used to search for variable declarations or
 * variable references which might be in name conflict with input field.
 * <br>
 * Usage:<br>
 * <pre> {@code
 * CtField param = ...;
 * param.map(new FieldScopeFunction()).forEach(...process result...);
 * }
 * </pre>
 */
public class FieldScopeFunction implements CtConsumableFunction<CtField<?>> {

	public FieldScopeFunction() {
	}

	@Override
	public void apply(CtField<?> field, CtConsumer<Object> outputConsumer) {
		if (field.hasModifier(ModifierKind.PRIVATE)) {
			searchForPrivateField(field, outputConsumer);
		} else if (field.hasModifier(ModifierKind.PUBLIC)) {
			searchForPublicField(field, outputConsumer);
		} else if (field.hasModifier(ModifierKind.PROTECTED)) {
			searchForProtectedField(field, outputConsumer);
		} else {
			searchForPackageProtectedField(field, outputConsumer);
		}
	}
	protected void searchForPrivateField(CtField<?> field, CtConsumer<Object> outputConsumer) {
		//private field can be referred from the scope of current top level type only and children
		field.getTopLevelType()
			.filterChildren(null)
			.forEach(outputConsumer);
	}
	protected void searchForProtectedField(CtField<?> field, CtConsumer<Object> outputConsumer) {
		//protected field can be referred from the scope of current top level type only and children
		field.getFactory().getModel()
			//search for all types which inherits from declaring type of this field
			.filterChildren(new SubtypeFilter(field.getDeclaringType().getReference()))
			//visit all elements in scope of these inherited types
			.filterChildren(null)
			.forEach(outputConsumer);
	}
	protected void searchForPublicField(CtField<?> field, CtConsumer<Object> outputConsumer) {
		//public field is visible everywhere
		field.getFactory().getModel()
			//visit all children of root package
			.filterChildren(null)
			.forEach(outputConsumer);
	}
	protected void searchForPackageProtectedField(CtField<?> field, CtConsumer<Object> outputConsumer) {
		//package protected fields are visible in scope of the package of the top level type of the `field`
		field.getTopLevelType().getPackage()
			//visit all children of package, where top level type of the field is declared
			.filterChildren(null)
			.forEach(outputConsumer);
	}
}
