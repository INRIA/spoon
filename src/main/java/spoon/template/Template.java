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

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;

/**
 * <p>
 * A template code is simply a piece of code that uses a
 * {@link TemplateParameter}'s instance. It must then invoke the
 * {@link TemplateParameter#S()} method.
 *
 * <p>
 * When the template parameter is a String it is used to rename element of the code such as fields or methods.
 * When it is another primitive type (or a boxing
 * type) representing a literal, or a Class, the template parameter can be
 * directly accessed. To use a standard parameter containing a String type, use a CtLiteral&lt;String&gt;
 *
 * <pre>
 *       import spoon.template.Template;
 *       import spoon.template.Value;
 *
 *       public class SimpleTemplate implements Template {
 *           // template parameter fields
 *            \@Parameter String _parameter_;
 *
 *            \@Parameter CtLiteral&lt;String&gt; _anotherParameter;
 *
 *
 *           // parameters binding
 *            \@Local
 *           public SimpleTemplate(String parameter, CtLiteral&lt;String&gt; anotherParameter) {
 *               _parameter_ = parameter;
 *               _anotherParameter = anotherParameter;
 *           }
 *
 *           // template method
 *           public void methodwith_parameter_() {
 *               System.out.println(_anotherParameter);
 *           }
 *       }
 * </pre>
 *
 * <p>
 * The template parameters must be bound to their values in the template's
 * constructor (which should be defined as a template's
 * {@link spoon.template.Local}. A possible use of a template would be to
 * insert the template into a target class, by using
 * {@link Substitution#insertAll(CtType, Template)}:
 *
 * <pre>
 *       spoon.reflect.CtClass target=...;
 *       CtLiteral&lt;String&gt; anotherParameter = factory.createLiteral();
 *       anotherParameter.setValue(&quot;hello templated world&quot;);
 *
 *       Template template=new SimpleTemplate(&quot;ParameterizedName&quot;, anotherParameter);
 *       Substitution.insertAll(target,template);
 * </pre>
 *
 * <p>
 * If the target class is an empty class named <code>A</code>, the resulting
 * code will be:
 *
 * <pre>
 * public class A {
 * 	public void methodwithParameterizedName() {
 * 		System.out.println(&quot;hello templated world&quot;);
 *    }
 * }
 * </pre>
 */
public interface Template<T extends CtElement> {
	/**
	 * Returns the code which results from applying the template.
	 *
	 * @param targetType
	 * 		the type that defines the context of the substitution.
	 * 		It may be null for templates with no context.
	 */
	T apply(CtType<?> targetType);

}
