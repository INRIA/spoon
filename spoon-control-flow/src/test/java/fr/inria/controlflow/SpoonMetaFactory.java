/**
 * The MIT License
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package fr.inria.controlflow;

//import fr.inria.diversify.util.Log;

import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by marodrig on 16/06/2014.
 */
public class SpoonMetaFactory {

	public Factory buildNewFactory(String srcDirectory, int javaVersion) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		ArrayList<String> a = new ArrayList<String>();
		a.add(srcDirectory);
		return buildNewFactory(a, javaVersion);
	}

	public Factory buildNewFactory(Collection<String> srcDirectory,
	                               int javaVersion) throws
			ClassNotFoundException, IllegalAccessException, InstantiationException {
		//String srcDirectory = DiversifyProperties.getProperty("project") + "/" + DiversifyProperties.getProperty("src");

		StandardEnvironment env = new StandardEnvironment();
		env.setComplianceLevel(javaVersion);
		env.setVerbose(true);
		env.setDebug(true);

		DefaultCoreFactory f = new DefaultCoreFactory();

		Factory factory = new FactoryImpl(f, env);

		JDTBasedSpoonCompiler compiler = new JDTBasedSpoonCompiler(factory);

		for (String s : srcDirectory) {
			for (String dir : s.split(System.getProperty("path.separator"))) {
				//Log.debug("add {} to classpath", dir);
				File dirFile = new File(dir);
				if (dirFile.isDirectory()) {
					compiler.addInputSource(dirFile);
				}
			}
		}
		try {
			compiler.build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return factory;
	}


}
