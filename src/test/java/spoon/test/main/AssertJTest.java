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
import org.junit.Test;
import spoon.Launcher;
import spoon.compiler.Environment;

@Ignore
public class AssertJTest {

	@Test
	public void test() {
		Launcher spoon = new Launcher();
		spoon.getEnvironment().setPrettyPrintingMode(Environment.PRETTY_PRINTING_MODE.FULLYQUALIFIED);

		String cp = "/home/martin/.m2/repository/junit/junit/4.12/junit-4.12.jar:/home/martin/.m2/repository/org/junit/vintage/junit-vintage-engine/5.3.1/junit-vintage-engine-5.3.1.jar:/home/martin/.m2/repository/org/apiguardian/apiguardian-api/1.0.0/apiguardian-api-1.0.0.jar:/home/martin/.m2/repository/org/junit/platform/junit-platform-engine/1.3.1/junit-platform-engine-1.3.1.jar:/home/martin/.m2/repository/org/junit/jupiter/junit-jupiter-api/5.3.1/junit-jupiter-api-5.3.1.jar:/home/martin/.m2/repository/org/junit/platform/junit-platform-commons/1.3.1/junit-platform-commons-1.3.1.jar:/home/martin/.m2/repository/org/junit/jupiter/junit-jupiter-engine/5.3.1/junit-jupiter-engine-5.3.1.jar:/home/martin/.m2/repository/org/junit/jupiter/junit-jupiter-params/5.3.1/junit-jupiter-params-5.3.1.jar:/home/martin/.m2/repository/org/opentest4j/opentest4j/1.1.1/opentest4j-1.1.1.jar:/home/martin/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:/home/martin/.m2/repository/net/bytebuddy/byte-buddy/1.9.1/byte-buddy-1.9.1.jar:/home/martin/.m2/repository/org/mockito/mockito-core/2.20.1/mockito-core-2.20.1.jar:/home/martin/.m2/repository/net/bytebuddy/byte-buddy-agent/1.8.13/byte-buddy-agent-1.8.13.jar:/home/martin/.m2/repository/org/objenesis/objenesis/2.6/objenesis-2.6.jar:/home/martin/.m2/repository/com/github/marschall/memoryfilesystem/1.2.1/memoryfilesystem-1.2.1.jar:/home/martin/.m2/repository/com/google/guava/guava/25.0-jre/guava-25.0-jre.jar:/home/martin/.m2/repository/com/google/code/findbugs/jsr305/1.3.9/jsr305-1.3.9.jar:/home/martin/.m2/repository/org/checkerframework/checker-compat-qual/2.0.0/checker-compat-qual-2.0.0.jar:/home/martin/.m2/repository/com/google/errorprone/error_prone_annotations/2.1.3/error_prone_annotations-2.1.3.jar:/home/martin/.m2/repository/com/google/j2objc/j2objc-annotations/1.1/j2objc-annotations-1.1.jar:/home/martin/.m2/repository/org/codehaus/mojo/animal-sniffer-annotations/1.14/animal-sniffer-annotations-1.14.jar";
		spoon.addInputResource("assertj-core/src/main/java");
		// mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
		spoon.getModelBuilder().setSourceClasspath(cp.split(":"));
		spoon.run();
		System.out.println("javac -cp "+cp+" `find spooned -name '*.java'`");
	}

}
