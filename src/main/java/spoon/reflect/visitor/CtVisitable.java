package spoon.reflect.visitor;

/**
 * Define a visitable element in spoon. You can read the page Wikipedia http://en.wikipedia.org/wiki/Visitor_pattern.
 */
public interface CtVisitable {
	/**
	 * Accepts a visitor
	 */
	void accept(CtVisitor visitor);
}
