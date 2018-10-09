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
package spoon.test.filters;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtReturn;
import spoon.support.compiler.jdt.CompilationUnitFilter;

import static org.junit.Assert.assertEquals;

public class CUFilterTest {

    @Test
    public void testWithoutFilters() {
        final Launcher launcher = new Launcher();
        launcher.addInputResource("./src/test/resources/noclasspath/same-package");
        launcher.buildModel();
        final CtModel model = launcher.getModel();
        assertEquals(2, model.getAllTypes().size());
        assertEquals("spoon.test.same.B", model.getAllTypes().iterator().next()
                .getMethod("createB").getType().getQualifiedName());
    }

    @Test
    public void testSingleExcludeWithFilter() {
        final Launcher launcher = new Launcher();
        launcher.getEnvironment().setNoClasspath(true);
        launcher.addInputResource("./src/test/resources/noclasspath/same-package");
        launcher.getModelBuilder().addCompilationUnitFilter(
                new CompilationUnitFilter() {
            @Override
            public boolean exclude(final String path) {
                return path.endsWith("B.java");
            }
        });
        launcher.buildModel();
        final CtModel model = launcher.getModel();

        assertEquals(1, model.getAllTypes().size());
        // make sure `B` is not available in `model.getAllTypes`
        assertEquals("A", model.getAllTypes().iterator().next().getSimpleName());
        // make sure declaration of `B` is known in `model`
        final CtReturn ctReturn = model.getAllTypes().iterator().next()
                .getMethod("createB").getBody().getStatement(0);
        final CtConstructorCall ctConstructorCall =
                (CtConstructorCall)ctReturn.getReturnedExpression();
        assertEquals("spoon.test.same.B", ctConstructorCall.getType().getQualifiedName());
    }

    @Test
    public void testSingleExcludeWithoutFilter() {
        final Launcher launcher = new Launcher();
        launcher.getEnvironment().setNoClasspath(true);
        launcher.addInputResource("./src/test/resources/noclasspath/same-package/A.java");
        launcher.buildModel();
        final CtModel model = launcher.getModel();

        assertEquals(1, model.getAllTypes().size());
        // make sure `B` is not available in `model.getAllTypes`
        assertEquals("A", model.getAllTypes().iterator().next().getSimpleName());
        // make sure declaration of `B` is unknown in `model`
        final CtReturn ctReturn = model.getAllTypes().iterator().next()
                .getMethod("createB").getBody().getStatement(0);
        final CtConstructorCall ctConstructorCall =
                (CtConstructorCall)ctReturn.getReturnedExpression();
        assertEquals("spoon.test.same.B", ctConstructorCall.getType().getQualifiedName());
    }
}
