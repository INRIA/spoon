### Junit/CI Integration

To use the existing infrastructure as CI and a possibility for running the architecture tests local, junit integration helps.
The following test case is treated by junit as a normal test, but checks architecture. 
If you throw an `AssertionError` or any other Exception `IError<T>` junit marks the test case as failed and your CI fails.
Or you can use a class like `JunitError`for collecting all errors before failing if there is any.

```java
@Test
public void testArchitecture() {
  		SpoonArchitecturalChecker.createChecker().runChecks();
}
```  

```java
	public class JunitError<CtElement> implements IError<CtElement> {

		List<String> failedChecks = new ArrayList<>();
		@Override
		public void printError(CtElement element) {
			failedChecks.add(createViolationMessage(element));
		}
		
		public void checkError() {
			if(!failedChecks.isEmpty()) {
				// print messages to system.out or a logger
				fail(failedChecks.stream().collect(Collectors.joining("\n")));
			}
		}
	}
```