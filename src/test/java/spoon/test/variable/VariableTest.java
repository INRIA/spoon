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
package spoon.test.variable;

import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.io.TempDir;
import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.sniper.SniperJavaPrettyPrinter;
import spoon.testing.utils.ModelTest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static spoon.testing.assertions.SpoonAssertions.assertThat;

public class VariableTest {

    @Test
    public void testSetType() {
        // contract: one can use setType without having a very hard generics type checking error

        // to get this done, we use the refactoring below
        CtClass c = Launcher.parseClass("class C{ void f() {Object i=null; }}");
        List<CtLocalVariable> localVariables = c.getElements(new TypeFilter<>(CtLocalVariable.class));

        // before: "CtLocalVariable<?> lv" could not typecheck, with a hard generics error
        // now it works; we can use both "CtLocalVariable<?> lv" and "CtLocalVariable lv"
        // it's source level backward compatible for Spoon itself
        CtLocalVariable<?> lv = localVariables.get(0);
        lv.setType(c.getFactory().Type().createReference(Object.class));

        CtLocalVariable lv2 = localVariables.get(0);
        lv2.setType(c.getFactory().Type().createReference(Object.class));

    }

    public void refactorFortestSetType() {
        // rectoring all setType(CtTypeParameter<T>) -> setType(CtTypeParameter)
        // this is the refactoring done for this pull-request
        Launcher l = new Launcher();
        l.addInputResource("src/main/java");
        l.setSourceOutputDirectory("src/main/java");
        l.getEnvironment().setPrettyPrinterCreator(() -> {
                    return new SniperJavaPrettyPrinter(l.getEnvironment());
                }
        );
        l.addProcessor(new AbstractProcessor<CtMethod<?>>() {
            @Override
            public boolean isToBeProcessed(CtMethod<?> candidate) {
                if (!candidate.getSimpleName().equals("setType")) return false;
                if (candidate.getParameters().size()!=1) return false;
                final CtParameter<?> ctParameter = candidate.getParameters().get(0);
                if (!ctParameter.getType().getSimpleName().equals("CtTypeReference")) return false;
                if (ctParameter.getType().getActualTypeArguments().size()!=1) return false;
                return true;
            }

            @Override
            public void process(CtMethod<?> element) {
                final CtParameter<?> ctParameter = element.getParameters().get(0);
                ctParameter.getType().removeActualTypeArgument(ctParameter.getType().getActualTypeArguments().get(0));
            }
        });
        l.run();

    }



    @Test
    public void testJointDeclVariables() {
        // contract: we can get the information of the jointly declared local variables
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setComplianceLevel(10);
        launcher.addInputResource("./src/test/resources/spoon/test/var/Main.java");

        CtModel model = launcher.buildModel();
        List<CtLocalVariable> localVariables = model.getElements(new TypeFilter<>(CtLocalVariable.class));
        for (int i=0; i <= 7; i++) {
            assertEquals(false, localVariables.get(i).isPartOfJointDeclaration());
        }
        assertEquals(true, localVariables.get(8).isPartOfJointDeclaration());
        assertEquals(true, localVariables.get(9).isPartOfJointDeclaration());

    }

        @Test
        @DisabledForJreRange(max = JRE.JAVA_9)
    public void testInferredVariableAreMarked() {
        // contract: if a variable is declared with 'var' keyword, it must be marked as inferred in the model
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setComplianceLevel(10);
        launcher.addInputResource("./src/test/resources/spoon/test/var/Main.java");

        CtModel model = launcher.buildModel();
        List<CtLocalVariable> localVariables = model.getElements(new TypeFilter<>(CtLocalVariable.class));
        assertEquals(10, localVariables.size());

        TypeFactory typeFactory = launcher.getFactory().Type();

        assertTrue(localVariables.get(0).isInferred());
        assertEquals(typeFactory.stringType(), localVariables.get(0).getType());

        assertFalse(localVariables.get(1).isInferred());
        assertEquals(typeFactory.stringType(), localVariables.get(1).getType());

        assertTrue(localVariables.get(2).isInferred());
        assertEquals("java.io.FileReader", localVariables.get(2).getType().getQualifiedName());

        assertFalse(localVariables.get(3).isInferred());
        assertEquals("java.io.FileReader", localVariables.get(3).getType().getQualifiedName());

        assertTrue(localVariables.get(4).isInferred());
        assertEquals(typeFactory.booleanPrimitiveType(), localVariables.get(4).getType());

        assertFalse(localVariables.get(5).isInferred());
        assertEquals(typeFactory.booleanPrimitiveType(), localVariables.get(5).getType());

        assertTrue(localVariables.get(6).isInferred());
        assertEquals(typeFactory.integerPrimitiveType(), localVariables.get(6).getType());

        assertFalse(localVariables.get(7).isInferred());
        assertEquals(typeFactory.integerPrimitiveType(), localVariables.get(7).getType());
    }

    @Test
    @DisabledForJreRange(max = JRE.JAVA_9)
    public void testInferredVariableArePrintedWithVar(@TempDir File outputDir) throws IOException {
        // contract: if a variable is marked as inferred in the model, it must be pretty-printed with a 'var' keyword
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setComplianceLevel(10);
        launcher.addInputResource("./src/test/resources/spoon/test/var/Main.java");

        launcher.setSourceOutputDirectory(outputDir);

        launcher.run();

        File outputFile = new File(outputDir, "fr/inria/sandbox/Main.java");
        assertTrue(outputFile.exists());

        String fileContent = StringUtils.join(Files.readLines(outputFile, Charset.defaultCharset()));

        assertTrue(fileContent.contains("var mySubstring = \"bla\";"));
        assertTrue(fileContent.contains("var myFile = new java.io.FileReader(new java.io.File(\"/tmp/myfile\")"));
        assertTrue(fileContent.contains("var myboolean = true;"));
        assertTrue(fileContent.contains("for (var i = 0;"));
    }

    @Test
    @DisabledForJreRange(max = JRE.JAVA_10)
    public void testVarInLambda() {
        // contract: we should handle local variable syntax for lambda parameters properly (since Java 11)
        // example: (var x, var y) -> x + y;
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setComplianceLevel(11);
        launcher.addInputResource("./src/test/resources/spoon/test/var/VarInLambda.java");
        CtModel model = launcher.buildModel();

        CtLambda<?> lambda = model.getElements(new TypeFilter<>(CtLambda.class)).get(0);
        assertTrue(lambda.getParameters().get(0).isInferred());
        assertTrue(lambda.getParameters().get(1).isInferred());
        assertEquals("java.lang.Integer", lambda.getParameters().get(0).getType().getQualifiedName());
        assertEquals("java.lang.Long", lambda.getParameters().get(1).getType().getQualifiedName());
        assertEquals("(var x,var y) -> x + y", lambda.toString()); // we should print var, if it was in the original code
    }

	@ModelTest(value = "./src/test/resources/spoon/test/unnamed/UnnamedVar.java", complianceLevel = 22)
	void testUnnamedVariable(Factory factory) throws IOException {
		// contract: each appearance of an unnamed variable is recognized and printed correctly
		// each method in the source class has one unnamed variable
		// we compare the output from printing to the original source
		CtType<?> type = factory.Type().get("spoon.test.unnamed.UnnamedVar");
		assertThat(type.getMethods()).isNotEmpty();
		List<String> lines = java.nio.file.Files.readAllLines(type.getPosition().getFile().toPath());
		for (CtMethod<?> method : type.getMethods()) {
			List<CtVariable<?>> locals = method.getBody().getElements(new TypeFilter<>(CtVariable.class));
			assertThat(locals).describedAs(method.getSimpleName()).hasSize(1);
			CtVariable<?> variable = locals.get(0);
			assertThat(variable).getSimpleName().isEqualTo("_");
			if (variable instanceof CtLocalVariable<?> v) {
				assertTrue(v.isUnnamed());
			} else if (variable instanceof CtParameter<?> v) {
				assertTrue(v.isUnnamed());
			} else if (variable instanceof CtCatchVariable<?> v) {
				assertTrue(v.isUnnamed());
			}
			assertThat(variable).getPosition().isNotEqualTo(SourcePosition.NOPOSITION);
			String line = lines.get(variable.getPosition().getLine() - 1);
			assertThat(line).contains(variable.toString());
		}
	}
}
