/**
 * 
 */
package spoon.support;

/**
 *	Different types of serialization.
 */
public enum SerializationType {

	STANDARD("Java"),
	STANDARD_GZIP("Java, GZIP compression");
	
	private String description;
	
	private SerializationType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return this.description;
	}
}
