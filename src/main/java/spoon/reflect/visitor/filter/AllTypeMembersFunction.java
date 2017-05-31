/**
 * Copyright (C) 2006-2017 INRIA and contributors
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

import java.util.HashSet;
import java.util.Set;

import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.chain.CtQueryAware;
import spoon.reflect.visitor.chain.CtQueryable;

/**
 * Expects {@link CtType} as input
 * and produces all {@link CtTypeMember}s declared in input class
 * or any super class or super interface
 */
public class AllTypeMembersFunction implements CtConsumableFunction<CtTypeInformation>, CtQueryAware {

	private CtQuery query;
	private final Class<?> memberClass;
	private Set<String> distinctSet;

	/**
	 * returns all type members
	 */
	public AllTypeMembersFunction() {
		this.memberClass = null;
	}

	/**
	 * returns all type members which are instance of `memberClass`.<br>
	 * Example:<br>
	 * <code>
	 * CtField allFields = ctType.map(new AllTypeMembersFunction(CtField.class)).list();
	 * </code>
	 */
	public AllTypeMembersFunction(Class<?> memberClass) {
		this.memberClass = memberClass;
	}

	/**
	 * The types whose qualified name is in distinctSet are not visited.
	 * The qualified name of each type visited by this mapping function is added to `distinctSet`
	 * @param distinctSet - Set of qualified names of types, which has to be ignored, because they were already processed
	 */
	public AllTypeMembersFunction distinctSet(Set<String> distinctSet) {
		this.distinctSet = distinctSet;
		return this;
	}

	@Override
	public void apply(CtTypeInformation input, final CtConsumer<Object> outputConsumer) {
		final CtQuery q = ((CtQueryable) input).map(new SuperInheritanceHierarchyFunction(distinctSet == null ? new HashSet<String>() : distinctSet).includingSelf(true));
		q.forEach(new CtConsumer<CtType<?>>() {
			@Override
			public void accept(CtType<?> type) {
				for (CtTypeMember typeMember : type.getTypeMembers()) {
					if (memberClass == null || memberClass.isInstance(typeMember)) {
						outputConsumer.accept(typeMember);
					}
					if (query.isTerminated()) {
						q.terminate();
					}
				}
			}
		});
	}

	@Override
	public void setQuery(CtQuery query) {
		this.query = query;
	}
}
