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
package spoon.visualisation.command;

import io.github.interacto.command.CommandImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.support.compiler.VirtualFile;
import spoon.visualisation.spoon.SpoonElementVisitor;
import spoon.visualisation.spoon.SpoonTreeScanner;

/**
 * Base class for commands that analyses Java code to extract its Spoon tree.
 */
abstract class SpoonTreeCmdBase extends CommandImpl {
	/** The Java code to analyse. */
	@Nullable String code;
	/** Hides or not the implicit Spoon elements. */
	final boolean hideImplicit;
	/** The analysis level to consider. */
	final @NotNull TreeLevel treeLevel;

	SpoonTreeCmdBase(final boolean hideImplicit, final @NotNull String code, final @NotNull TreeLevel treeLevel) {
		super();
		this.hideImplicit = hideImplicit;
		this.treeLevel = treeLevel;
		this.code = code;
	}

	@Override
	protected void doCmdBody() {
		switch(treeLevel) {
			case AUTO:
				buildClassLevel();
				break;
			case CLASS_ELEMENT:
				buildClassElementLevel();
				break;
			case STATEMENT:
				buildStatementLevel();
				break;
			case EXPRESSION:
				buildExpressionLevel();
				break;
		}
	}

	/**
	 * Tries to build a Spoon model.
	 * @param theCode The code to analyse.
	 * @param levelsToIgnore The number of levels not to display (depends on the analysis level).
	 * @return The Spoon model.
	 */
	@NotNull CtModel buildCode(final String theCode, final int levelsToIgnore) {
		final Launcher launcher = new Launcher();
		final Environment env = launcher.getEnvironment();

		launcher.addInputResource(new VirtualFile(theCode, "chunk.java"));
		env.setNoClasspath(true);
		env.setAutoImports(true);
		env.disableConsistencyChecks();
		env.setLevel("OFF");
		env.setComplianceLevel(11);

		launcher.buildModel().getRootPackage().accept(new SpoonTreeScanner(createSpoonVisitor(levelsToIgnore), hideImplicit));

		return launcher.getModel();
	}

	/**
	 * Tries to build the Spoon model by considering the given code as a Java expression.
	 */
	void buildExpressionLevel() {
		buildCode("public class ShowMeYourSpoonCapsule { public Object showmeyourspoonmethod() { return "
			+ code + ";}}", 5);
	}

	/**
	 * Tries to build the Spoon model by considering the given code as a Java statement.
	 */
	@NotNull CtModel buildStatementLevel() {
		return buildCode("public class ShowMeYourSpoonCapsule { public void showmeyourspoonmethod() {" + code + "}}", 4);
	}

	/**
	 * Tries to build the Spoon model by considering the given code as Java class elements.
	 */
	@NotNull CtModel buildClassElementLevel() {
		return buildCode("public class ShowMeYourSpoonCapsule { " + code + "}", 2);
	}

	/**
	 * Tries to build the Spoon model by considering the given code as a Java class.
	 */
	void buildClassLevel() {
		CtModel model = buildCode(code, 1);

		if(model.getAllTypes().isEmpty()) {
			model = buildClassElementLevel();
		}

		if(model.getAllTypes().isEmpty()) {
			model = buildStatementLevel();
		}

		if(model.getAllTypes().isEmpty()) {
			buildExpressionLevel();
		}
	}

	abstract SpoonElementVisitor createSpoonVisitor(final int levelsToIgnore);

	@Override
	public boolean canDo() {
		return code != null;
	}

	public void setCode(final @NotNull String code) {
		this.code = code;
	}
}
