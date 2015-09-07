package spoon.reflect.path;

/**
 * Created by nicolas on 27/08/2015.
 */
public enum CtPathRole {
	/**
	 * Default value for a field
	 */
	DEFAULT_VALUE("defaultValue"),
	/**
	 * Then part of a CtIf
	 */
	THEN("then"),
	/**
	 * Else part of a CtIf
	 */
	ELSE("else"),
	/**
	 * Body of CtExecutable.
	 */
	BODY("body");

	private final String[] names;

	CtPathRole(String... names) {
		this.names = names;
	}

	public static CtPathRole fromName(String name) {
		for (CtPathRole role : values()) {
			for (String roleName : role.names) {
				if (roleName.equals(name)) {
					return role;
				}
			}
		}
		throw new IllegalArgumentException("no role found with name :" + name);
	}

	@Override
	public String toString() {
		return names[0];
	}
}
