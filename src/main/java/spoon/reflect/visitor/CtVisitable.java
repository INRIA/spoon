package spoon.reflect.visitor;

/**
 * Created by nicolas on 27/02/2015.
 */
public interface CtVisitable {
	/**
	 * Accepts a visitor
	 */
	void accept(CtVisitor visitor);
}
