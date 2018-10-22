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
package spoon.test.staticFieldAccess;

import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.test.staticFieldAccess.processors.InsertBlockProcessor;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;


public class StaticAccessTest {

    Launcher spoon;
    Factory factory;
    SpoonModelBuilder compiler;

    @Before
    public void setUp()  throws Exception {
          spoon = new Launcher();
          factory = spoon.createFactory();
          compiler = spoon.createCompiler(
                factory,
                SpoonResourceHelper
                        .resources(
                                "./src/test/java/spoon/test/staticFieldAccess/internal/",
                                "./src/test/java/spoon/test/staticFieldAccess/StaticAccessBug.java"
                        ));
        compiler.build();
    }

    @Test
    public void testReferences() {
        CtType<?> type = factory.Type().get("spoon.test.staticFieldAccess.StaticAccessBug");
        CtBlock<?> block = type.getMethod("references").getBody();
        assertTrue(block.getStatement(0).toString().contains("Extends.MY_STATIC_VALUE"));
        assertTrue(block.getStatement(1).toString().contains("Extends.MY_OTHER_STATIC_VALUE"));
    }


    @Test
    public void testProcessAndCompile() throws Exception{
        compiler.instantiateAndProcess(Arrays.asList(InsertBlockProcessor.class.getName()));

        // generate files
        File tmpdir = new File("target/spooned/staticFieldAccess");
        tmpdir.mkdirs();
        //    tmpdir.deleteOnExit();
        factory.getEnvironment().setSourceOutputDirectory(tmpdir);
        compiler.generateProcessedSourceFiles(OutputType.COMPILATION_UNITS);

        // try to reload generated datas
        spoon = new Launcher();
        compiler = spoon.createCompiler(
                SpoonResourceHelper
                        .resources(tmpdir.getAbsolutePath()));
        assertTrue(compiler.build());
    }

}
