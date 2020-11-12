package spoon.architecture.runner;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SpoonArchitecturalCheckerTest {

	@TempDir
	File tempDir;
	@Test
	public void testInvalidPathThrowsNullPointer() {
		assertThrows(NullPointerException.class, () -> SpoonArchitecturalChecker.createCheckerWithoutDefault().runChecks(tempDir.getPath()));
		assertThrows(NullPointerException.class, () -> SpoonArchitecturalChecker.createChecker().runChecks(tempDir.getPath()));
	}
}
