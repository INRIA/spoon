package spoon.test.template.testclasses.match;

import spoon.pattern.ConflictResolutionMode;
import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.pattern.TemplateModelBuilder;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.filter.TypeFilter;

public class MatchMap {

	@Check()
	void matcher1() {
	}
	
	@Check(value = "xyz")
	void m1() {
	}
	
	@Check(timeout = 123, value = "abc")
	void m2() {
	}

	@Deprecated
	void notATestAnnotation() {
	}

	@Deprecated
	@Check(timeout = 456)
	void deprecatedTestAnnotation1() {
	}

	@Check(timeout = 4567)
	@Deprecated
	void deprecatedTestAnnotation2() {
	}
}
