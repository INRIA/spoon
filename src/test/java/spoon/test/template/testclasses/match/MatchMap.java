package spoon.test.template.testclasses.match;

import spoon.pattern.ConflictResolutionMode;
import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.filter.TypeFilter;

public class MatchMap {

	public static Pattern createPattern(Factory factory, boolean acceptOtherAnnotations) {
		return PatternBuilder.create(factory, MatchMap.class, tmb -> tmb.setTypeMember("matcher1"))
			.configureParameters(pb -> {
				//match any value of @Check annotation to parameter `testAnnotations`
				pb.parameter("CheckAnnotationValues").attributeOfElementByFilter(CtRole.VALUE, new TypeFilter(CtAnnotation.class)).setContainerKind(ContainerKind.MAP);
//				pb.parameter("testAnnotations").attributeOfElementByFilter(CtRole.VALUE, new TypeFilter(CtAnnotation.class));
				//match any method name
				pb.parameter("methodName").byString("matcher1");
				if (acceptOtherAnnotations) {
					//match on all annotations of method
					pb.parameter("allAnnotations")
						.setConflictResolutionMode(ConflictResolutionMode.APPEND)
						.attributeOfElementByFilter(CtRole.ANNOTATION, new TypeFilter<>(CtMethod.class));
				}
			})
			.build();
	}

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
