import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class SignaturePolymorphicMethods {
	private static final MethodHandle STRING_IDENTITY = MethodHandles.identity(String.class);
	private static final MethodHandle HELLO_WORLD = MethodHandles.constant(String.class, "Hello World");
	private static final MethodHandle NOP = MethodHandles.empty(MethodType.methodType(void.class, CharSequence.class, int.class));
	public String a() throws Throwable {
		return (String) STRING_IDENTITY.invokeExact("Hello World");
	}
	public String b() throws Throwable {
		return (String) HELLO_WORLD.invokeExact();
	}
	public Object c() throws Throwable {
		return HELLO_WORLD.invoke();
	}
	public void d() throws Throwable {
		NOP.invokeExact((CharSequence) "Hello World", 123);
	}
}
