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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.visualisation.spoon.StreamPrinter;
import spoon.visualisation.spoon.SpoonElementVisitor;

/**
 * The command that saves the current Spoon AST into a text file
 */
public class SaveTreeText extends SpoonTreeCmdBase {
	private final @Nullable File file;
	private PrintStream printer;

	/**
	 * @param file The file to save the text into
	 * @param hideImplicit Hide implicit elements?
	 * @param code The code to convert as text
	 * @param treeLevel The tree level to use
	 */
	public SaveTreeText(final @Nullable File file, final boolean hideImplicit, final @NotNull String code,
			final @NotNull TreeLevel treeLevel) {
		super(hideImplicit, code, treeLevel);
		this.file = file;
	}

	@Override
	SpoonElementVisitor createSpoonVisitor(final int levelsToIgnore) {
		try {
			printer = new PrintStream(file);
			return new StreamPrinter(printer, levelsToIgnore);
		}catch(final FileNotFoundException | SecurityException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	protected void doCmdBody() {
		super.doCmdBody();
		if(printer != null) {
			printer.close();
		}
	}

	@Override
	public boolean canDo() {
		return file != null && super.canDo();
	}
}
