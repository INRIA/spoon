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
package spoon.test.role;

import org.junit.Test;

import spoon.SpoonException;
import spoon.reflect.path.CtRole;
import spoon.support.reflect.code.CtAssertImpl;
import spoon.support.reflect.declaration.CtAnonymousExecutableImpl;
import spoon.support.reflect.declaration.CtConstructorImpl;
import spoon.support.reflect.declaration.CtFieldImpl;
import spoon.support.reflect.declaration.CtMethodImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestCtRole {
    @Test
    public void testGetCtRoleByName() {
        // contract: one should be able to get CtRole based on its name (without '_' whatever the case is, or with '_' in uppercase)

        String name = "DECLARING_TYPE"; // exactly the same name: OK
        assertEquals(CtRole.DECLARING_TYPE, CtRole.fromName(name));

        name = "declaringType"; // camel case: OK
        assertEquals(CtRole.DECLARING_TYPE, CtRole.fromName(name));

        name = "declaringtype"; // lower case: OK
        assertEquals(CtRole.DECLARING_TYPE, CtRole.fromName(name));

        name = "declaring_type"; // lower case with underscore: not accepted
        assertNull(CtRole.fromName(name));

        for (CtRole role : CtRole.values()) {
            assertEquals(role, CtRole.fromName(role.name().replaceAll("_", "").toLowerCase()));
        }
    }

    @Test
    public void testCtRoleGetSubRolesNotNull() {
        // contract: CtRole#getSubRoles() never returns null

        for (CtRole role : CtRole.values()) {
            assertNotNull(role.getSubRoles());
        }
    }

    @Test
    public void testCtRoleSubRoleMatchesWithSuperRole() {
        // contract: CtRole#getSubRoles() and CtRole#getSuperRole() are empty or links to each other
    	int countOfSubRoles = 0;
        for (CtRole role : CtRole.values()) {
        	for (CtRole subRole : role.getSubRoles()) {
        		countOfSubRoles++;
				assertSame(role, subRole.getSuperRole());
			}
        	if (role.getSuperRole() != null) {
        		assertTrue(role.getSuperRole().getSubRoles().contains(role));
        	}
        }
        assertTrue(countOfSubRoles > 0);
    }
    @Test
    public void testCtRoleGetSubRole() {
    	// contract: we can match the correct subrole for CtRole.METHOD, CtRole.CONSTRUCTOR, CtRole.FIELD and CtRole.ANNONYMOUS_EXECUTABLE
    	assertSame(CtRole.METHOD, CtRole.TYPE_MEMBER.getMatchingSubRoleFor(new CtMethodImpl<>()));
    	assertSame(CtRole.CONSTRUCTOR, CtRole.TYPE_MEMBER.getMatchingSubRoleFor(new CtConstructorImpl()));
    	assertSame(CtRole.FIELD, CtRole.TYPE_MEMBER.getMatchingSubRoleFor(new CtFieldImpl()));
    	assertSame(CtRole.ANNONYMOUS_EXECUTABLE, CtRole.TYPE_MEMBER.getMatchingSubRoleFor(new CtAnonymousExecutableImpl()));
    }
    @Test
    public void testCtRoleGetSubRoleFailsOnOthers() {
    	// contract: an exception is thrown when no possible role can be found for this element
    	try {
    		CtRole.TYPE_MEMBER.getMatchingSubRoleFor(new CtAssertImpl<>());
    		fail();
    	} catch (SpoonException e) {
    		//OK
    	}
    }
    @Test
    public void testCtRoleGetSubRoleFailsOnNull() {
    	try {
    		CtRole.TYPE_MEMBER.getMatchingSubRoleFor(null);
    		fail();
    	} catch (SpoonException e) {
    		//OK
    	}
    }
}
