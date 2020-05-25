/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor.filter;

import java.util.HashSet;
import java.util.Set;

import spoon.SpoonException;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.chain.CtQueryAware;
import spoon.reflect.visitor.chain.CtQueryable;

/**
 * Expects {@link CtType} as input
 * and produces all {@link CtTypeMember}s declared in input class
 * or any super class or super interface.
 * It first returns own type members, then type members of superclass, etc.
 */
public class AllTypeMembersFunction implements CtConsumableFunction<CtTypeInformation>, CtQueryAware {

	public enum Mode {
		/**
		 * Returns all type members - including private
		 */
		ALL,
		/**
		 * Returns only type members, which are accessible from the input `type`.
		 * It means that inherited private type members are skipped
		 */
		SKIP_PRIVATE
	}

	private CtQuery query;
	private final Class<?> memberClass;
	private Set<String> distinctSet;
	private Mode mode = Mode.ALL;

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
		String inputQName = input.getQualifiedName();
		final CtQuery q = ((CtQueryable) input).map(new SuperInheritanceHierarchyFunction(distinctSet == null ? new HashSet<>() : distinctSet).includingSelf(true));
		q.forEach(new CtConsumer<CtType<?>>() {
			@Override
			public void accept(CtType<?> type) {
				boolean isInputType = inputQName.equals(type.getQualifiedName());
				loop: for (CtTypeMember typeMember : type.getTypeMembers()) {
					if (memberClass == null || memberClass.isInstance(typeMember)) {
						switch (mode) {
						case ALL:
							break;
						case SKIP_PRIVATE:
							if (typeMember.hasModifier(ModifierKind.PRIVATE) && !isInputType) {
								continue loop;
							}
							break;
						default:
							throw new SpoonException("Unexpected mode " + mode);
						}
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

	/**
	 * @param mode defines how whether type members with limited visibility are returned
	 */
	public AllTypeMembersFunction setMode(Mode mode) {
		this.mode = mode;
		return this;
	}
}
