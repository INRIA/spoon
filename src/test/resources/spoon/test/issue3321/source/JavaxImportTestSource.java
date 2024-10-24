package spoon.test.issue3321.source;

import javax.validation.constraints.NotNull;
import spoon.test.issue3321.SomeObjectDto;

/**
 * @author Gibah Joseph
 * Email: gibahjoe@gmail.com
 * Apr, 2020
 **/
public class JavaxImportTestSource {
	public JavaxImportTestSource setObject(@NotNull SomeObjectDto someObjectDto) {
		return this;
	}
}
