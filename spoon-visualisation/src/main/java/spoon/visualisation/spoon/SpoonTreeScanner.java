/*
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package spoon.visualisation.spoon;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtScanner;

/**
 * Scans Java code using a Spoon scanner to extract Spoon tree information.
 */
public class SpoonTreeScanner extends CtScanner {
	/** If true, the scanner will ignore implicit elements */
	private final boolean hideImplicit;
	/** The printer that is in charge of showing the Spoon tree. */
	private final @NotNull SpoonElementVisitor printer;
	/** The current deepness level. */
	private int level;
	/** the role of the current element. May be null. */
	private @Nullable CtRole currRole;


	/**
	 * @param printer The printer that is in charge of showing the Spoon tree.
	 * The first argument (integer), is the deepness of the current element in the tree structure.
	 * The second argument (string), is the label of the current element to display.
	 * The third argument (list) contains the start and end line of the element. May be empty.
	 * @param hideImplicit If true, the scanner will ignore implicit elements
	 */
	public SpoonTreeScanner(final @NotNull SpoonElementVisitor printer, final boolean hideImplicit) {
		super();
		this.hideImplicit = hideImplicit;
		this.printer = printer;
		level = 0;
	}


	@Override
	public void scan(final CtRole role, final CtElement element) {
		// Filtering out implicit elements;
		if(element != null && hideImplicit && element.isImplicit()) {
			return;
		}

		currRole = role;
		super.scan(role, element);
		currRole = null;
	}


	@Override
	protected void enter(final CtElement elt) {
		level++;

		final SourcePosition pos = elt.getPosition();
		final List<Integer> lines = pos.isValidPosition() ? List.of(pos.getSourceStart(), pos.getSourceEnd()) : List.of();

		if(elt instanceof CtType<?>) {
			printer.visitElement(elt, level, createNodeLabel(elt, Optional.of(": " + ((CtType<?>) elt).getSimpleName())), lines);
			return;
		}

		if(elt instanceof CtNamedElement) {
			printer.visitElement(elt, level, createNodeLabel(elt, Optional.of(": " + ((CtNamedElement) elt).getSimpleName())), lines);
			return;
		}

		if(elt instanceof CtReference) {
			printer.visitElement(elt, level, createNodeLabel(elt, Optional.of(": " + ((CtReference) elt).getSimpleName())), lines);
			return;
		}

		if(elt instanceof CtVariableAccess<?>) {
			final CtVariableAccess<?> varaccess = (CtVariableAccess<?>) elt;
			final String txt = ": " + ((varaccess.getVariable() != null) ? varaccess.getVariable().getSimpleName() : "(null)");
			printer.visitElement(elt, level, createNodeLabel(elt, Optional.of(txt)), lines);
			return;
		}

		if(elt instanceof CtTypeAccess<?>) {
			final CtTypeAccess<?> typeaccess = (CtTypeAccess<?>) elt;
			final String txt = ": " + ((typeaccess.getAccessedType() != null) ? typeaccess.getAccessedType().getSimpleName() : "(null)");
			printer.visitElement(elt, level, createNodeLabel(elt, Optional.of(txt)), lines);
			return;
		}

		if(elt instanceof CtLiteral<?>) {
			printer.visitElement(elt, level, createNodeLabel(elt, Optional.of(": " + ((CtLiteral<?>) elt).getValue())), lines);
			return;
		}

		if(elt instanceof CtAbstractInvocation<?>) {
			final CtAbstractInvocation<?> invoc = (CtAbstractInvocation<?>) elt;
			final String txt = ": " + ((invoc.getExecutable() != null) ? invoc.getExecutable().getSimpleName() : "(null)");
			printer.visitElement(elt, level, createNodeLabel(elt, Optional.of(txt)), lines);
			return;
		}

		if(elt instanceof CtAnnotation<?>) {
			final CtAnnotation<?> annot = (CtAnnotation<?>) elt;
			final String txt = ": " + ((annot.getAnnotationType() != null) ? annot.getAnnotationType().getSimpleName() : "(null)");
			printer.visitElement(elt, level, createNodeLabel(elt, Optional.of(txt)), lines);
			return;
		}

		printer.visitElement(elt, level, createNodeLabel(elt, Optional.empty()), lines);
	}


	@Override
	protected void exit(final CtElement e) {
		level--;
		super.exit(e);
	}


	/**
	 * Create an initial text flow for naming the nodes of the Spoon AST.
	 * @param elt The Spoon element to analyse.
	 * @return The created text flow that can be completed with other text elements.
	 */
	TreeNodeLabel createNodeLabel(final CtElement elt, final Optional<String> adds) {
		final String simpleName = elt.getClass().getSimpleName();
		// We assume that the Spoon API follows this rule:
		// the implementation class name is the interface name plus 'Impl'
		// We want to display the main interface corresponding to the given class.
		// So looking for this interface. If not found, the current class is used.
		final Class<?> cl = Arrays
			.stream(elt.getClass().getInterfaces())
			.filter(interf -> simpleName.equals(interf.getSimpleName() + "Impl"))
			.findFirst()
			.orElse(elt.getClass());

		final Optional<String> implicit = elt.isImplicit() ? Optional.of("(implicit)") : Optional.empty();
		final Optional<String> role = currRole == null ? Optional.empty() : Optional.of("(role: " + currRole + ")");

		return new TreeNodeLabel(cl.getSimpleName(), cl.getName(), implicit, role, adds);
	}
}
