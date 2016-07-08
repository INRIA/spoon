package spoon.reflect.ast;

import org.junit.Test;
import spoon.Launcher;
import spoon.diff.Action;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThrow;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.support.comparator.CtLineElementComparator;
import spoon.template.TemplateMatcher;
import spoon.template.TemplateParameter;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AstCheckerTest {
	@Test
	public void testStackChanges() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.getModelBuilder().setSourceClasspath(System.getProperty("java.class.path").split(File.pathSeparator));
		// interfaces.
		launcher.addInputResource("./src/main/java/spoon/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/reflect/reference");
		launcher.addInputResource("./src/main/java/spoon/reflect/internal");
		// Implementations.
		launcher.addInputResource("./src/main/java/spoon/support/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/reference");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/internal");
		// Utils.
		launcher.addInputResource("./src/test/java/spoon/reflect/ast/AstCheckerTest.java");
		launcher.buildModel();

		final GetterListChecker getterListChecker = new GetterListChecker(launcher.getFactory());
		getterListChecker.scan(launcher.getModel().getRootPackage());
		if (getterListChecker.result != null) {
			throw new AssertionError(getterListChecker.result);
		}
	}

	@Test
	public void testAvoidSetCollectionSavedOnAST() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setBuildStackChanges(true);
		launcher.addInputResource("src/main/java");
		launcher.buildModel();

		final Factory factory = launcher.getFactory();
		final List<CtTypeReference<?>> collectionsRef = Arrays.asList( //
				factory.Type().createReference(Collection.class), //
				factory.Type().createReference(List.class), //
				factory.Type().createReference(Set.class), //
				factory.Type().createReference(Map.class));

		final List<CtInvocation<?>> invocations = Query.getElements(factory, new TypeFilter<CtInvocation<?>>(CtInvocation.class) {
			@Override
			public boolean matches(CtInvocation<?> element) {
				if (!element.getExecutable().getSimpleName().startsWith("get")) {
					return false;
				}
				if (!collectionsRef.contains(element.getType())) {
					return false;
				}
				if (!element.getExecutable().getDeclaringType().getSimpleName().startsWith("Ct")) {
					return false;
				}
				if (!(element.getParent() instanceof CtInvocation)) {
					return false;
				}
				final CtInvocation<?> parent = (CtInvocation<?>) element.getParent();
				if (!parent.getTarget().equals(element)) {
					return false;
				}
				final String simpleName = parent.getExecutable().getSimpleName();
				return simpleName.startsWith("add") || simpleName.startsWith("remove");
			}
		});
		if (invocations.size() > 0) {
			final String error = invocations.stream() //
					.sorted(new CtLineElementComparator()) //
					.map(i -> "see " + i.getPosition().getFile().getName() + " at " + i.getPosition().getLine()) //
					.collect(Collectors.joining(",\n"));
			throw new AssertionError(error);
		}
	}

	@Test
	public void testPushToStackChanges() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setBuildStackChanges(true);
		// Implementations.
		launcher.addInputResource("./src/main/java/spoon/support/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/reference");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/internal");
		// Utils.
		launcher.addInputResource("./src/test/java/spoon/reflect/ast/AstCheckerTest.java");
		launcher.buildModel();

		final PushStackInIntercessionChecker checker = new PushStackInIntercessionChecker(launcher.getFactory());
		checker.scan(launcher.getModel().getRootPackage());
		if (!checker.result.isEmpty()) {
			System.err.println(checker.count);
			throw new AssertionError(checker.result);
		}
	}

	private class PushStackInIntercessionChecker extends CtScanner {
		private final CtInvocation<?> template;
		private final List<String> notCandidates;
		private String result = "";
		private int count;

		PushStackInIntercessionChecker(Factory factory) {
			final CtType<Object> templateClass = factory.Type().get(Template.class);
			template = templateClass.getMethod("templatePush").getBody().getStatement(0);
			notCandidates = Arrays.asList( //
					"CtTypeImpl#addTypeMember", //
					"CtTypeImpl#removeTypeMember", //
					"CtTypeImpl#addFieldAtTop", //
					"CtTypeImpl#addField", //
					"CtTypeImpl#removeField", //
					"CtTypeImpl#addBound", //
					"CtTypeImpl#addNestedType", //
					"CtTypeImpl#removeNestedType", //
					"CtTypeImpl#addMethod", //
					"CtTypeImpl#removeMethod", //
					"CtBlockImpl#removeStatement", //
					"CtAnnotationTypeImpl#addMethod", //
					"CtClassImpl#removeConstructor", //
					"CtConstructorCallImpl#addArgument", //
					"CtInvocationImpl#addArgument", //
					"CtTypeParameterReferenceImpl#addBound", //
					"CtTypeParameterReferenceImpl#removeBound", //
					"CtTypeParameterReferenceImpl#setBounds", //
					"CtElementImpl#setFactory", //
					"CtElementImpl#setPositions", //
					"CtElementImpl#setDocComment", //
					"CtStatementListImpl#setPosition", //
					"CtAnnotationImpl#addValue", //
					"CtAnnotationTypeImpl#setFields" //
			);
		}

		private boolean isToBeProcessed(CtMethod<?> candidate) {
			if (candidate.getAnnotation(UnsettableProperty.class) != null) {
				return false;
			}
			if (candidate.getAnnotation(DerivedProperty.class) != null) {
				return false;
			}
			return candidate.getBody() != null //
					&& candidate.getParameters().size() != 0 //
					&& candidate.hasModifier(ModifierKind.PUBLIC) //
					&& (candidate.getSimpleName().startsWith("add") || candidate.getSimpleName().startsWith("set") || candidate.getSimpleName().startsWith("remove")) //
					&& candidate.getDeclaringType().getSimpleName().startsWith("Ct") //
					&& !isNotCandidate(candidate) //
					&& !isDelegateMethod(candidate) //
					&& !isUnsupported(candidate.getBody()) //
					&& !hasPushToStackInvocation(candidate.getBody())
					&& isSettable(candidate);
		}

		private boolean isSettable(CtMethod<?> candidate) {
			return candidate.getAnnotation(UnsettableProperty.class) == null;
		}
		private boolean isNotCandidate(CtMethod<?> candidate) {
			return "setVisibility".equals(candidate.getSimpleName()) || notCandidates.contains(candidate.getDeclaringType().getSimpleName() + "#" + candidate.getSimpleName());
		}

		private boolean isDelegateMethod(CtMethod<?> candidate) {
			if (candidate.getBody().getStatements().size() == 0) {
				return false;
			}
			if (!(candidate.getBody().getStatement(0) instanceof CtIf)) {
				return false;
			}
			if (!(((CtIf) candidate.getBody().getStatement(0)).getThenStatement() instanceof CtBlock)) {
				return false;
			}
			final CtBlock block = ((CtIf) candidate.getBody().getStatement(0)).getThenStatement();
			if (!(block.getStatement(0) instanceof CtInvocation || block.getStatement(0) instanceof CtReturn)) {
				return false;
			}
			CtInvocation potentialDelegate;
			if (block.getStatement(0) instanceof CtReturn) {
				if (!(((CtReturn) block.getStatement(0)).getReturnedExpression() instanceof CtInvocation)) {
					return false;
				}
				potentialDelegate = (CtInvocation) ((CtReturn) block.getStatement(0)).getReturnedExpression();
			} else {
				potentialDelegate = (CtInvocation) block.getStatement(0);
			}
			return potentialDelegate.getExecutable().getSimpleName().equals(candidate.getSimpleName());
		}

		private boolean isUnsupported(CtBlock<?> body) {
			return body.getStatements().size() != 0 //
					&& body.getStatements().get(0) instanceof CtThrow //
					&& "UnsupportedOperationException".equals(((CtThrow) body.getStatements().get(0)).getThrownExpression().getType().getSimpleName());
		}

		private boolean hasPushToStackInvocation(CtBlock<?> body) {
			return body.getElements(new TypeFilter<CtInvocation<?>>(CtInvocation.class) {
				@Override
				public boolean matches(CtInvocation<?> element) {
					return element.getExecutable().getSimpleName().equals(template.getExecutable().getSimpleName()) && super.matches(element);
				}
			}).size() == 1;
		}

		private void process(CtMethod<?> element) {
			count++;
			result += element.getSignature() + " on " + element.getDeclaringType().getQualifiedName() + "\n";
		}

		@Override
		public <T> void visitCtMethod(CtMethod<T> m) {
			if (isToBeProcessed(m)) {
				process(m);
			}
			super.visitCtMethod(m);
		}
	}

	private class GetterListChecker extends CtScanner {
		private final List<CtTypeReference<?>> COLLECTIONS;
		private final CtExpression<Boolean> conditionExpected;
		private String result;

		GetterListChecker(Factory factory) {
			COLLECTIONS = Arrays.asList(factory.Type().createReference(Collection.class), factory.Type().createReference(List.class), factory.Type().createReference(Set.class));
			final CtType<Object> templateClass = factory.Type().get(Template.class);
			conditionExpected = ((CtIf) templateClass.getMethod("template").getBody().getStatement(0)).getCondition();
		}

		private boolean isToBeProcessed(CtMethod<?> candidate) {
			return candidate.getBody() != null //
					&& candidate.getParameters().size() == 0 //
					&& candidate.getDeclaringType().getSimpleName().startsWith("Ct") //
					&& COLLECTIONS.contains(candidate.getType()) //
					&& isConditionExpected(candidate.getBody().getStatement(0)) //
					&& isReturnCollection(candidate.getBody().getLastStatement());
		}

		private boolean isConditionExpected(CtStatement statement) {
			final TemplateMatcher matcher = new TemplateMatcher(conditionExpected);
			return matcher.find(statement).size() == 0;
		}

		private boolean isReturnCollection(CtStatement statement) {
			return statement instanceof CtReturn //
					&& ((CtReturn) statement).getReturnedExpression() instanceof CtFieldRead<?> //
					&& COLLECTIONS.contains(((CtFieldRead) ((CtReturn) statement).getReturnedExpression()).getVariable().getType());
		}

		private void process(CtMethod<?> element) {
			result += element.getSignature() + " on " + element.getDeclaringType().getQualifiedName() + "\n";
		}

		@Override
		public <T> void visitCtMethod(CtMethod<T> m) {
			if (isToBeProcessed(m)) {
				process(m);
			}
			super.visitCtMethod(m);
		}
	}

	class Template {
		TemplateParameter<Action> _action_;

		public void template() {
			if (getFactory().getEnvironment().buildStackChanges()) {
			}
		}

		public void templatePush() {
			getFactory().getEnvironment().pushToStack(_action_.S());
		}

		public Factory getFactory() {
			return null;
		}
	}
}
