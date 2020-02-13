package spoon.visualisation.spoon;

import java.util.Optional;

/**
 * Defines the computed title of a Spoon element
 */
public class TreeNodeLabel {
	public final String className;
	public final String fullName;
	public final Optional<String> implicit;
	public final Optional<String> role;
	public final Optional<String> additionals;

	/**
	 * @param simpleName The simple class name of the Spoon element
	 * @param fullName The qualified name of the Spoon element
	 * @param implicit Text that detail whether the Spoon element is implicit
	 * @param role Text that detail the role of the Spoon element
	 * @param additionals Additional text
	 */
	public TreeNodeLabel(final String simpleName, final String fullName, final Optional<String> implicit, final Optional<String> role,
		final Optional<String> additionals) {
		super();
		this.className = simpleName;
		this.fullName = fullName;
		this.implicit = implicit;
		this.role = role;
		this.additionals = additionals;
	}

	@Override
	public String toString() {
		return className
			+ implicit.map(imp -> " " + imp).orElse("")
			+ role.map(imp -> " " + imp).orElse("")
			+ additionals.map(imp -> " " + imp).orElse("");
	}
}
