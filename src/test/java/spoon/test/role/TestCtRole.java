package spoon.test.role;

import org.junit.Test;
import spoon.reflect.path.CtRole;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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
}
