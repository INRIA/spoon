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
package spoon.support.compiler.jdt;

import org.junit.Test;
import spoon.compiler.builder.AdvancedOptions;
import spoon.compiler.builder.AnnotationProcessingOptions;
import spoon.compiler.builder.ClasspathOptions;
import spoon.compiler.builder.ComplianceOptions;
import spoon.compiler.builder.JDTBuilderImpl;
import spoon.compiler.builder.SourceOptions;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class JDTBuilderTest {
	private static final String TEST_CLASSPATH = "./src/test/java/spoon/test/";

	@Test
	public void testJdtBuilder() {
		final String[] builder = new JDTBuilderImpl() //
				.classpathOptions(new ClasspathOptions().classpath(TEST_CLASSPATH).bootclasspath(TEST_CLASSPATH).binaries(".").encoding("UTF-8")) //
				.complianceOptions(new ComplianceOptions().compliance(8)) //
				.annotationProcessingOptions(new AnnotationProcessingOptions().compileProcessors()) //
				.advancedOptions(new AdvancedOptions().continueExecution().enableJavadoc().preserveUnusedVars()) //
				.sources(new SourceOptions().sources(".")) //
				.build();

		assertEquals("-cp", builder[0]);
		assertEquals(TEST_CLASSPATH, builder[1]);
		assertEquals("-bootclasspath", builder[2]);
		assertEquals(TEST_CLASSPATH, builder[3]);
		assertEquals("-d", builder[4]);
		assertEquals(new File(".").getAbsolutePath(), builder[5]);
		assertEquals("-encoding", builder[6]);
		assertEquals("UTF-8", builder[7]);
		assertEquals("-1.8", builder[8]);
		assertEquals("-proc:none", builder[9]);
		assertEquals("-noExit", builder[10]);
		assertEquals("-enableJavadoc", builder[11]);
		assertEquals("-preserveAllLocals", builder[12]);
		assertEquals(".", builder[13]);
	}
}
