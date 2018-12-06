/**
 *  This file originally comes from JavaParser and is distributed under the terms of
 * a) the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * b) the terms of the Apache License
 */
package spoon.javadoc.internal;

/**
* An element of a description: either an inline tag or a piece of text.
*
* <p>So for example <code>a text</code> or <code>{@link String}</code> could be valid description
* elements.
*/
public interface JavadocDescriptionElement {
	/** pretty-prints the Javadoc fragment */
	String toText();
}
