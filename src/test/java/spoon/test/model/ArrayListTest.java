/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 * <p>
 * Copyright (C) 2006-2019 INRIA and contributors
 * <p>
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */

package spoon.test.model;

import org.junit.jupiter.api.DisplayName;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.compiler.VirtualFile;
import spoon.test.GitHubIssue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Switchcase Tests")
public class ArrayListTest {

    private static CtModel createModelFromString(String code) {
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setComplianceLevel(14);
        launcher.getEnvironment().setNoClasspath(true);
        launcher.addInputResource(new VirtualFile(code));
        return launcher.buildModel();
    }

    @GitHubIssue(issueNumber = 4698, fixed = false)
    void testArrayListRoleInParent() {

        String code = "import java.util.ArrayList;\n" +
                "public class A {\n" +
                "  ArrayList<String> test;\n" +
                "}";
        CtModel model = createModelFromString(code);

        var test = model.getElements(new TypeFilter<>(CtTypeReference.class))
                .stream()
                .filter(a -> a.getSimpleName().equals("ArrayList"))
                .findFirst();
        assertTrue(test.isPresent());
        var typeDeclaration = test.get().getTypeDeclaration();

        // This statement triggers the bug, probably because
        // the DefaultJavaPrettyPrinter.printElement() clone the ArrayList
        typeDeclaration.toString();

        assertEquals(CtRole.CONTAINED_TYPE, typeDeclaration.getRoleInParent());
    }
}
