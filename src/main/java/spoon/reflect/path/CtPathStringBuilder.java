package spoon.reflect.path;

import spoon.reflect.path.impl.CtNamedPathElement;
import spoon.reflect.path.impl.CtPathElement;
import spoon.reflect.path.impl.CtPathImpl;
import spoon.reflect.path.impl.CtRolePathElement;
import spoon.reflect.path.impl.CtTypedNameElement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nicolas on 27/08/2015.
 */
public class CtPathStringBuilder {

	private final Pattern PATH_PATTERN = Pattern.compile("([/.#])([^/.#\\[]+)(\\[([^/.#]*)\\])?");
	private final Pattern ARGUMENT_PATTERN = Pattern.compile("(\\w+)=([^=\\]]+)");


	private Class load(String name) throws CtPathException {
		// try without name
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException ex) {
		}

		// search in spoon.reflect.declaration
		try {
			return Class.forName("spoon.reflect.declaration." + name);
		} catch (ClassNotFoundException ex) {
		}
		// search in  
		try {
			return Class.forName("spoon.reflect.code." + name);
		} catch (ClassNotFoundException ex) {
			throw new CtPathException(
					String.format("Unable to locate element with name $s in Spoon model", name));
		}
	}

	/**
	 * Build path from a string representation.
	 *
	 * for example: 
	 * new CtPathBuilder().fromString(".spoon.test.path.Foo.foo#statement[index=0]")
	 * Match the first statement of method foo from class spoon.test.path.Foo.
	 *
	 * Some specials characters 
	 * . :  match with the given name
	 * # : match with a CtPathRole
	 * / : match with a element type (for example, to match all classes, use /CtClass
	 */
	public CtPath fromString(String pathStr) throws CtPathException {
		Matcher matcher = PATH_PATTERN.matcher(pathStr);

		CtPathImpl path = new CtPathImpl();
		while (matcher.find()) {
			String kind = matcher.group(1);

			CtPathElement pathElement = null;
			if (CtNamedPathElement.STRING.equals(kind)) {
				pathElement = new CtNamedPathElement(matcher.group(2));
			} else if (CtTypedNameElement.STRING.equals(kind)) {
				pathElement = new CtTypedNameElement(load(matcher.group(2)));
			} else if (CtRolePathElement.STRING.equals(kind)) {
				pathElement = new CtRolePathElement(CtPathRole.fromName(matcher.group(2)));
			}

			String args = matcher.group(4);
			if (args != null) {
				for (String arg : args.split(";")) {
					Matcher argmatcher = ARGUMENT_PATTERN.matcher(arg);
					if (argmatcher.matches()) {
						pathElement.addArgument(argmatcher.group(1), argmatcher.group(2));
					}
				}
			}

			path.addLast(pathElement);
		}
		return path;
	}

}
