package spoon.reflect.ast;

import org.junit.Test;
import spoon.Launcher;
import spoon.experimental.modelobs.FineModelChangeListener;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtThrow;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.support.comparator.CtLineElementComparator;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AstCheckerTest {

	@Test
	public void testAvoidSetCollectionSavedOnAST() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
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
				if (!(element.getParent() instanceof CtInvocation)) {
					return false;
				}
				final CtInvocation<?> parent = (CtInvocation<?>) element.getParent();
				if (parent.getTarget() == null || !parent.getTarget().equals(element)) {
					return false;
				}
				if (!element.getExecutable().getDeclaringType().getSimpleName().startsWith("Ct")) {
					return false;
				}
				boolean isDataStructure = false;
				for (int i = 0; i < collectionsRef.size(); i++) {
					CtTypeReference<?> ctTypeReference = collectionsRef.get(i);
					if (element.getType().isSubtypeOf(ctTypeReference)) {
						isDataStructure = true;
						break;
					}
				}
				if (!isDataStructure) {
					return false;
				}

				final String simpleName = parent.getExecutable().getSimpleName();
				return simpleName.startsWith("add") || simpleName.startsWith("remove") || simpleName.startsWith("put");
			}
		});
		if (invocations.size() > 0) {
			final String error = invocations.stream() //
					.sorted(new CtLineElementComparator()) //
					.map(i -> "see " + i.getPosition().getFile().getAbsoluteFile() + " at " + i.getPosition().getLine()) //
					.collect(Collectors.joining(",\n"));
			throw new AssertionError(error);
		}
	}

	@Test
	public void testPushToStackChanges() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		// Implementations.
		launcher.addInputResource("./src/main/java/spoon/support/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/reference");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/internal");
		// Utils.
		launcher.addInputResource("./src/test/java/spoon/reflect/ast/AstCheckerTest.java");
		launcher.buildModel();

		final PushStackInIntercessionChecker checker = new PushStackInIntercessionChecker();
		checker.scan(launcher.getModel().getRootPackage());
		if (!checker.result.isEmpty()) {
			System.err.println(checker.count);
			throw new AssertionError(checker.result);
		}
	}

	private class PushStackInIntercessionChecker extends CtScanner {
		private final List<String> notCandidates;
		private String result = "";
		private int count;

		PushStackInIntercessionChecker() {
			notCandidates = Arrays.asList( //
					"CtTypeImpl#setTypeMembers", //
					"CtStatementListImpl#setPosition", //
					"CtElementImpl#setFactory", //
					"CtElementImpl#setPositions", //
					"CtElementImpl#setDocComment", //
					"CtElementImpl#setParent", //
					"CtTypeParameterReferenceImpl#addBound", //
					"CtTypeParameterReferenceImpl#removeBound", //
					"CtTypeParameterReferenceImpl#setBounds" //
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
					&& !isSurcharged(candidate) //
					&& !isDelegateMethod(candidate) //
					&& !isUnsupported(candidate.getBody()) //
					&& !hasPushToStackInvocation(candidate.getBody());
		}

		private boolean isNotCandidate(CtMethod<?> candidate) {
			return "setVisibility".equals(candidate.getSimpleName()) || notCandidates.contains(candidate.getDeclaringType().getSimpleName() + "#" + candidate.getSimpleName());
		}

		private boolean isSurcharged(CtMethod<?> candidate) {
			CtBlock<?> block = candidate.getBody();
			if (block.getStatements().size() == 0) {
				return false;
			}
			CtInvocation potentialDelegate;
			if (block.getLastStatement() instanceof CtReturn) {
				if (!(((CtReturn) block.getLastStatement()).getReturnedExpression() instanceof CtInvocation)) {
					if (block.getStatement(0) instanceof CtInvocation) {
						potentialDelegate = block.getStatement(0);
					} else {
						return false;
					}
				} else {
					potentialDelegate = (CtInvocation) ((CtReturn) block.getLastStatement()).getReturnedExpression();
				}
			} else if (block.getStatement(0) instanceof CtInvocation && block.getStatements().size() == 1) {
				potentialDelegate = block.getStatement(0);
			} else {
				return false;
			}
			CtExecutable declaration = potentialDelegate.getExecutable().getDeclaration();
			if (declaration == null || !(declaration instanceof CtMethod)) {
				return false;
			}
			// check if the invocation has a model change listener
			return !isToBeProcessed((CtMethod<?>) declaration);
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
					return FineModelChangeListener.class.getSimpleName().equals(element.getExecutable().getDeclaringType().getSimpleName()) && super.matches(element);
				}
			}).size() > 0;
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
}
