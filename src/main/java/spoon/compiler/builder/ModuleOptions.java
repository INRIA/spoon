package spoon.compiler.builder;

public class ModuleOptions<T extends ModuleOptions<T>> extends Options<T> {
	public ModuleOptions() {
		super(ModuleOptions.class);
	}

	public T modules(String modulePath) {
		args.add("--module-source-path");
		args.add(modulePath);
		return myself;
	}
}
