package spoon.test.role;

import org.junit.Test;
import spoon.reflect.path.CtRole;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
}
