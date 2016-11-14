package spoon.reflect.ast;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

import static org.junit.Assert.fail;

public class ParentTest {
	@Test
	public void testParentSetInSetter() throws Exception {
		// contract: Check that all setters protect their parameter.
		final Launcher launcher = new Launcher();
		final Factory factory = launcher.getFactory();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.getEnvironment().setNoClasspath(true);
		// interfaces.
		launcher.addInputResource("./src/main/java/spoon/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/reflect/reference");
		// implementations.
		launcher.addInputResource("./src/main/java/spoon/support/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/reference");
		// Utils.
		launcher.addInputResource("./src/test/java/spoon/reflect/ast/");
		launcher.buildModel();

		// Asserts.
		new IntercessionScanner(launcher.getFactory()) {

			@Override
			protected boolean isToBeProcessed(CtMethod<?> candidate) {
				return (candidate.getSimpleName().startsWith("set") //
						|| candidate.getSimpleName().startsWith("add")) //
						&& candidate.hasModifier(ModifierKind.PUBLIC) //
						&& takeSetterForCtElement(candidate) //
						&& avoidInterfaces(candidate) //
						&& avoidSpecificMethods(candidate) //
						&& avoidThrowUnsupportedOperationException(candidate);
			}

			@Override
			public void process(CtMethod<?> element) {
				if (element.getSimpleName().startsWith("add")) {
					checkAddStrategy(element);
				} else {
					checkSetStrategy(element);
				}
			}

			private void checkAddStrategy(CtMethod<?> element) {
				final CtStatement statement = element.getBody().getStatement(0);
				if (!(statement instanceof CtIf)) {
					fail("First statement should be an if to check the parameter of the setter." + element.getSignature() + " declared in " + element.getDeclaringType().getQualifiedName());
				}
				if (!createCheckNull(element.getParameters().get(0)).equals(((CtIf) statement).getCondition())) {
					fail("Condition should test if the parameter is null. The condition was " + ((CtIf) statement).getCondition() + "in " + element.getSignature() + " declared in " + element
							.getDeclaringType().getQualifiedName());
				}
			}

			private void checkSetStrategy(CtMethod<?> element) {
				final CtTypeReference<?> type = element.getParameters().get(0).getType();
				if (!COLLECTIONS.contains(type) && !(type instanceof CtArrayTypeReference)) {
					CtInvocation<?> setParent = searchSetParent(element.getBody());
					if (setParent == null) {
						fail("Missing set parent in " + element.getSignature());
					}
					try {
						if (setParent.getParent(CtIf.class) == null) {
							fail("Missing condition in " + element.getSignature() + " declared in the class " + element.getDeclaringType().getQualifiedName());
						}
					} catch (ParentNotInitializedException e) {
						fail("Missing parent condition in " + element.getSignature() + " declared in the class " + element.getDeclaringType().getQualifiedName());
					}
				}
			}

			/**
			 * Creates <code>parameter == null</code>.
			 *
			 * @param ctParameter <code>parameter</code>
			 */
			private CtBinaryOperator<Boolean> createCheckNull(CtParameter<?> ctParameter) {
				final CtLiteral nullLiteral = factory.Code().createLiteral(null);
				nullLiteral.setType(factory.Type().NULL_TYPE.clone());
				final CtBinaryOperator<Boolean> operator = factory.Code().createBinaryOperator( //
						factory.Code().createVariableRead(ctParameter.getReference(), true), //
						nullLiteral, BinaryOperatorKind.EQ);
				operator.setType(factory.Type().BOOLEAN_PRIMITIVE);
				return operator;
			}

			private CtInvocation<?> searchSetParent(CtBlock<?> body) {
				final List<CtInvocation<?>> ctInvocations = body.getElements(new TypeFilter<CtInvocation<?>>(CtInvocation.class) {
					@Override
					public boolean matches(CtInvocation<?> element) {
						return "setParent".equals(element.getExecutable().getSimpleName()) && super.matches(element);
					}
				});
				if (ctInvocations.size() != 1) {
					final CtMethod parent = (CtMethod) body.getParent();
					fail("Have " + ctInvocations.size() + " setParent() in " + parent.getSignature() + " declared in the class " + parent.getDeclaringType().getQualifiedName());
				}
				return ctInvocations.get(0);
			}
		}.scan(launcher.getModel().getRootPackage());
	}
}
