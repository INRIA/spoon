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
package spoon.reflect.code;

import spoon.support.DerivedProperty;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;

import java.util.List;

import static spoon.reflect.path.CtRole.COMMENT_TAG;

/**
 * This code element defines a javadoc comment
 *
 * Example:
 * <pre>
 * &#x2F;**
 *  * Description
 *  * @tag a tag in the javadoc
 * *&#x2F;
 * </pre>
 */
public interface CtJavaDoc extends CtComment {
	/**
	 * Get all the tag of the javadoc
	 * @return the tag list
	 */
	@PropertyGetter(role = COMMENT_TAG)
	List<CtJavaDocTag> getTags();

	/**
	 * Define the list of tags
	 * @param tags the new list of tags
	 */
	@PropertySetter(role = COMMENT_TAG)
	<E extends CtJavaDoc> E setTags(List<CtJavaDocTag> tags);

	/**
	 * Add a new tag at the end of the list
	 * @param tag the new tag
	 */
	@PropertySetter(role = COMMENT_TAG)
	<E extends CtJavaDoc> E addTag(CtJavaDocTag tag);

	/**
	 * Add a new tag at the index position
	 * @param index the index of the new tag
	 * @param tag the new tag
	 */
	@PropertySetter(role = COMMENT_TAG)
	<E extends CtJavaDoc> E addTag(int index, CtJavaDocTag tag);

	/**
	 * Remove a tag from the index
	 * @param index the position of the tag to remove
	 */
	@PropertySetter(role = COMMENT_TAG)
	<E extends CtJavaDoc> E removeTag(int index);

	/**
	 * Remove a specific tag
	 * @param tag the tag to remove
	 */
	@PropertySetter(role = COMMENT_TAG)
	<E extends CtJavaDoc> E removeTag(CtJavaDocTag tag);

	/**
	 * Get the short summary of the javadoc (first sentence of the javadoc)
	 * @return the summary of the javadoc
	 */
	@DerivedProperty
	String getShortDescription();

	/**
	 * Get the long description of the javadoc
	 * @return the long description of the javadoc
	 */
	@DerivedProperty
	String getLongDescription();

	@Override
	CtJavaDoc clone();
}
