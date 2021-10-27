/**
 * Copyright (C) 2006-2021 INRIA and contributors
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
package spoon.test.constructor;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CtConstructorCallTest {

    @Test
    void testRemoveArgument() {
        // contract: removeArgument removes the passed argument among two arguments in the call

        Factory factory = new Launcher().getFactory();
        CtExpression<?> argumentToBeRemoved = factory.createLiteral(true);
        CtExpression<?> additionalArgument = factory.createLiteral(false);
        CtConstructorCall<?> call = factory.createConstructorCall(
                factory.createCtTypeReference(Object.class), argumentToBeRemoved, additionalArgument
        );
        assertThat(call.getArguments().size(), is(2));

        call.removeArgument(argumentToBeRemoved);

        assertThat(call.getArguments().size(), is(1));
        assertThat(call.getArguments().get(0), is(additionalArgument));
    }

    @Test
    void testRemoveActualTypeArgument() {
        // contract: removeActualTypeArgument removes the passed type among two types

        Factory factory = new Launcher().getFactory();
        CtTypeReference<Boolean> typeToBeRemoved = factory.createCtTypeReference(Boolean.class);
        CtTypeReference<Integer> additionalType = factory.createCtTypeReference(Integer.class);
        CtConstructorCall<?> call = factory.createConstructorCall(factory.createCtTypeReference(Object.class));
        call.addActualTypeArgument(typeToBeRemoved);
        call.addActualTypeArgument(additionalType);
        assertThat(call.getActualTypeArguments().size(), is(2));

        call.removeActualTypeArgument(typeToBeRemoved);

        assertThat(call.getActualTypeArguments().size(), is(1));
        assertThat(call.getActualTypeArguments().get(0), is(additionalType));
    }
}