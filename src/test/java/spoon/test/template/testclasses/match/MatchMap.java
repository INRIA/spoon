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

	public static Pattern createPattern(Factory factory, boolean acceptOtherAnnotations) {
		CtType<?> type = factory.Type().get(MatchMap.class);
		return PatternBuilder.create(new TemplateModelBuilder(type).setTypeMember("matcher1").getTemplateModels())
			.configureParameters(pb -> {
				//match any value of @Check annotation to parameter `testAnnotations`
				pb.parameter("CheckAnnotationValues").attributeOfElementByFilter(CtRole.VALUE, new TypeFilter(CtAnnotation.class)).setContainerKind(ContainerKind.MAP);
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
	public static Pattern createMatchKeyPattern(Factory factory) {
		CtType<?> type = factory.Type().get(MatchMap.class);
		return PatternBuilder.create(new TemplateModelBuilder(type).setTypeMember("m1").getTemplateModels())
			.configureParameters(pb -> {
				//match any value of @Check annotation to parameter `testAnnotations`
				pb.parameter("CheckKey").bySubstring("value");
				pb.parameter("CheckValue").byFilter((CtLiteral lit) -> true);
				//match any method name
				pb.parameter("methodName").byString("m1");
				//match on all annotations of method
				pb.parameter("allAnnotations")
					.setConflictResolutionMode(ConflictResolutionMode.APPEND)
					.attributeOfElementByFilter(CtRole.ANNOTATION, new TypeFilter<>(CtMethod.class));
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
