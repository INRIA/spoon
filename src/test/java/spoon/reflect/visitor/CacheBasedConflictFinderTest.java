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
package spoon.reflect.visitor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import spoon.Launcher;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.testclasses.simpleNestedClassWithFields;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CacheBasedConflictFinderTest {

    private CacheBasedConflictFinder cacheBasedConflictFinder;

    @BeforeAll
    public void setup() {
        Factory factory = new Launcher().getFactory();
        CtType<?> type = factory.Class().get(simpleNestedClassWithFields.class);
        this.cacheBasedConflictFinder = new CacheBasedConflictFinder(type);
    }

    @Test
    void testHasFieldConflictsWithConflictingField() {
        // contract: hasFieldConflict returns true when a conflicting field name is passed as argument

        Boolean shouldBeTrue = cacheBasedConflictFinder.hasFieldConflict("testField");
        assertTrue(shouldBeTrue);
    }

    @Test
    void testHasFieldConflictsWithNonConflictingField() {
        // contract: hasFieldConflict returns false when a non-conflicting field name is passed as argument

        Boolean shouldBeFalse = cacheBasedConflictFinder.hasFieldConflict("testField1");
        assertFalse(shouldBeFalse);
    }

    @Test
    void testHasNestedTypeConflictsWithConflictingArgument() {
        // contract: hasNestedTypeConflict returns true when a argument is passed which is name of already existing
        // nested types

        Boolean shouldBeTrue = cacheBasedConflictFinder.hasNestedTypeConflict("subClass");
        assertTrue(shouldBeTrue);
    }

    @Test
    void testHasNestedTypeConflictsWithNonConflictingArgument() {
        // contract: hasNestedTypeConflict returns false when a argument is passed which isn't name of an already
        // existing nested type

        Boolean shouldBeFalse = cacheBasedConflictFinder.hasNestedTypeConflict("subClass1");
        assertFalse(shouldBeFalse);
    }
}