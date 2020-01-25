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
package spoon.test.main;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import spoon.ContractVerifier;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Ignore
public class MoshiTest {

	@Test
	public void test() {
		Launcher spoon = new Launcher();
		spoon.getEnvironment().setPrettyPrintingMode(Environment.PRETTY_PRINTING_MODE.FULLYQUALIFIED);
		spoon.addInputResource("moshi/moshi/src/main/java");
		// mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
		spoon.getModelBuilder().setSourceClasspath("/home/martin/.m2/repository/com/squareup/okio/okio/1.11.0/okio-1.11.0.jar","/home/martin/.m2/repository/junit/junit/4.12/junit-4.12.jar","/home/martin/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar","/home/martin/.m2/repository/org/assertj/assertj-core/1.7.0/assertj-core-1.7.0.jar");
		spoon.getEnvironment().setShouldCompile(true);
		spoon.run();
	}

}
