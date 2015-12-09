/**
 * Copyright (C) 2006-2015 INRIA and contributors
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

import spoon.support.template.DefaultParameterMatcher;
import spoon.support.template.ParameterMatcher;

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
	 * clashes). By default, the name of a template parameter is the simple name
	 * of the annotated field. However, in some cases, it can be useful to set a
	 * different name to a parameter in order to avoid name clashes, in
	 * particular when a parameter represents the name of a templated field. For
	 * instance:
	 *
	 * <pre>
	 * class T extends Template {
	 * 	// this parameter will contain the actual value of the _i_ field's name
	 * 	\@Parameter(&quot;_i_&quot;)
	 * 	String __i_;
	 *
	 * 	int _i_;
	 * }
	 * </pre>
	 */
	String value() default "";

	/**
	 * Precises the type of the parameter matcher for this particular parameter
	 * when using the {@link spoon.template.TemplateMatcher} engine (optional).
	 * By default, the parameter will match under any form. Specifying an
	 * implementation of {@link ParameterMatcher} here allows the matching of
	 * more specific forms.
	 */
	Class<? extends ParameterMatcher> match() default DefaultParameterMatcher.class;
}
