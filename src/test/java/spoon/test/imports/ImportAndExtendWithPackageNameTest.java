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
package spoon.test.imports;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class ImportAndExtendWithPackageNameTest {

    private static final String inputResource =
            "./src/test/resources/import-resources/ImportAndExtendWithPackageName.java";

    @Test
    public void testBuildModel() {
        final Launcher runLaunch = new Launcher();
        runLaunch.getEnvironment().setNoClasspath(true);
        runLaunch.addInputResource(inputResource);
        runLaunch.buildModel();

        final Collection<CtType<?>> types = runLaunch.getModel().getAllTypes();
        assertEquals(1, types.size());

        final CtType type = types.iterator().next();
        assertEquals("ImportAndExtendWithPackageName", type.getSimpleName());

        final CtTypeReference superClass = type.getSuperclass();
        assertEquals("LLkParser", superClass.getSimpleName());
    }
}
