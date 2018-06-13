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
package spoon.template;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation should be placed on templates' fields or methods to indicate
 * that they represent template parameters. It is only mandatory for names,
 * literals, and types, where it avoids having to use
 * {@link spoon.template.TemplateParameter} and allows for the direct accesses
 * of the parameters. A parameter is never considered as a templated element and
 * it is not necessary to annotate it with a {@link Local} annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Parameter {
	/**
	 * Defines the name of the parameter (optional, mostly to avoid name
	 * clashes). In most cases, the annotation does not have a "value" and the name of a template parameter is the simple name
	 * of the annotated field.
	 *
	 * <pre>
	 * class T extends Template {
	 * 	\@Parameter
	 * 	CtExpression&lt;String&gt; $i;
	 *
	 * 	String s = $i.S();
	 * }
	 * </pre>
	 *
	 * However, in rare cases, eg to rename named elements that are in the same scope
	 * as the template parameter, such as renaming of fields or nested types, the annotation value
	 * is used to set a
	 * template parameter name (aka a proxy). In this case:
	 * contract 1: if "value" is set, then the template field type must be String
	 * contract 2: if a proxy parameter is declared and named "x" (@Parameter("x")), then a type member named "x" must exist (the one to be renamed).
	 *
	 * <pre>
	 * class T extends Template {
	 * 	// this parameter will contain the actual value of the _i_ field's name
	 * 	\@Parameter(&quot;_i_&quot;)
	 * 	String __i_;
	 *
	 * 	int _i_; // the field to be renamed
	 * }
	 * </pre>
	 */
	String value() default "";

}
