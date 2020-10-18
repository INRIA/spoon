package fr.inria.gforge.spoon.architecture.runner;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Architecture {
	String[] modelNames() default {"srcModel","testModel"};
		// marker annotation
}
