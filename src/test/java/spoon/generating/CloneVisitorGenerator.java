/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.generating;

import org.apache.commons.lang3.StringUtils;
import spoon.SpoonException;
import spoon.processing.AbstractManualProcessor;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtInheritanceScanner;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.OverridingMethodFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.CtModifierHandler;
import spoon.support.util.ModelList;
import spoon.support.util.ModelSet;
import spoon.support.visitor.clone.CloneBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CloneVisitorGenerator extends AbstractManualProcessor {
	private static final String TARGET_CLONE_PACKAGE = "spoon.support.visitor.clone";
	private static final String TARGET_CLONE_TYPE = "CloneVisitor";
	private static final String TARGET_BUILDER_CLONE_TYPE = "CloneBuilder";
	private static final String GENERATING_CLONE_PACKAGE = "spoon.generating.clone";
	private static final String GENERATING_CLONE = GENERATING_CLONE_PACKAGE + ".CloneVisitorTemplate";
	private static final String GENERATING_BUILDER_CLONE = GENERATING_CLONE_PACKAGE + ".CloneBuilderTemplate";

	@Override
	public void process() {
		final CtClass<Object> target = createCloneVisitor();
		final CtClass<Object> targetBuilder = createCloneBuilder();

		final Factory factory = target.getFactory();
		final CtTypeReference<Object> cloneBuilder = factory.Type().createReference("spoon.support.visitor.clone.CloneBuilder");
		final CtTypeAccess<Object> cloneBuilderType = factory.Code().createTypeAccess(cloneBuilder);
		final CtVariableAccess<Object> builderFieldAccess = factory.Code().createVariableRead(factory.Field().createReference(target.getReference(), cloneBuilder, "builder"), false);
		final CtVariableAccess<Object> tailorerFieldAccess = factory.Code().createVariableRead(factory.Field().createReference(target.getReference(), cloneBuilder, "tailorer"), false);
		final CtVariableAccess<Object> cloneHelperFieldAccess = factory.Code().createVariableRead(factory.Field().createReference(target.getReference(), cloneBuilder, "cloneHelper"), false);
		final CtFieldReference<Object> other = factory.Field().createReference((CtField) target.getField("other"));
		final CtVariableAccess otherRead = factory.Code().createVariableRead(other, true);

		new CtScanner() {
			private final List<String> internals = Collections.singletonList("CtCircularTypeReference");

			@Override
			public <T> void visitCtMethod(CtMethod<T> element) {
				if (!element.getSimpleName().startsWith("visitCt")) {
					return;
				}
				CtMethod<T> clone = element.clone();

				// Variables used by the visit method.
				final CtParameter<CtElement> ctParameter = (CtParameter<CtElement>) element.getParameters().get(0);
				final CtVariableAccess<CtElement> elementVarRead = factory.Code().createVariableRead(ctParameter.getReference(), false);
				final CtInvocation cloneBuilderInvocation = createCloneBuilderBuildInvocation(elementVarRead);
				final CtLocalVariable localCloningElement = createLocalCloningElement(ctParameter.getType(), createFactoryInvocation(elementVarRead));
				final CtVariableAccess localVarRead = factory.Code().createVariableRead(localCloningElement.getReference(), false);

				// Changes body of the cloned method.
				for (int i = 1; i < clone.getBody().getStatements().size() - 1; i++) {
					List<CtExpression> invArgs = ((CtInvocation) clone.getBody().getStatement(i)).getArguments();
					if (invArgs.size() <= 1) {
						throw new RuntimeException("You forget the role argument in line " + i + " of method " + element.getSimpleName() + " from " + element.getDeclaringType().getQualifiedName());
					}
					final CtInvocation targetInvocation = (CtInvocation) invArgs.get(1);
					if ("getValue".equals(targetInvocation.getExecutable().getSimpleName()) && "CtLiteral".equals(targetInvocation.getExecutable().getDeclaringType().getSimpleName())) {
						clone.getBody().getStatement(i--).delete();
						continue;
					}
					clone.getBody().getStatement(i) //
							.replace(createSetter((CtInvocation) clone.getBody().getStatement(i), localVarRead));
				}

				// Delete enter and exit methods.
				clone.getBody().getStatement(0).delete();
				clone.getBody().getStatement(clone.getBody().getStatements().size() - 1).delete();

				clone.getBody().insertBegin(createCloneBuilderCopyInvocation(elementVarRead, localVarRead)); // call to copy
				clone.getBody().insertBegin(localCloningElement); // declaration of local variable
				clone.getBody().insertEnd(createTailorerScanInvocation(elementVarRead, localVarRead)); // call to tailor
				clone.getBody().insertEnd(factory.createVariableAssignment(other, false, localVarRead)); // final assignment


				// Add auto-generated comment.
				final CtComment comment = factory.Core().createComment();
				comment.setCommentType(CtComment.CommentType.INLINE);
				comment.setContent("auto-generated, see " + CloneVisitorGenerator.class.getName());
				clone.addComment(comment);

				target.addMethod(clone);
			}

			/**
			 * Creates <code>anElement.setX(clone(anElement.getX()))</code>.
			 *
			 * @param scanInvocation <code>scan(anElement.getX())</code>.
			 * @param elementVarRead <code>anElement</code>.
			 */
			private CtInvocation<?> createSetter(CtInvocation scanInvocation, CtVariableAccess<CtElement> elementVarRead) {
				final CtInvocation<?> getter = (CtInvocation<?>) scanInvocation.getArguments().get(1);
				final String getterName = getter.getExecutable().getSimpleName();
				final CtExecutableReference<Object> setterRef = factory.Executable().createReference("void CtElement#set" + getterName.substring(3, getterName.length()) + "()");
				final CtExecutableReference<Object> cloneRef = factory.Executable().createReference("CtElement spoon.support.visitor.equals.CloneHelper#clone()");
				final CtInvocation<Object> cloneInv = factory.Code().createInvocation(null, cloneRef, getter);
				cloneInv.setTarget(cloneHelperFieldAccess);
				return factory.Code().createInvocation(elementVarRead, setterRef, cloneInv);
			}

			/**
			 * Creates <code>CtElement anElement = CloneBuilder.build(builder, element, element.getFactory().Core().createElement())</code>.
			 *
			 * @param typeReference <code>CtElement</code>.
			 * @param ctInvocation <code>CloneBuilder.build(builder, element, element.getFactory().Core().createElement())</code>.
			 */
			private <T> CtLocalVariable<T> createLocalCloningElement(CtTypeReference<T> typeReference, CtInvocation<T> ctInvocation) {
				return factory.Code().createLocalVariable(typeReference, "a" + typeReference.getSimpleName(), ctInvocation);
			}

			/**
			 * Creates <code>CloneBuilder.build(builder, element, element.getFactory().Core().createElement())</code>.
			 *
			 * @param elementAccess <code>element</code>.
			 */
			private CtInvocation<CloneBuilder> createCloneBuilderBuildInvocation(CtVariableAccess<CtElement> elementAccess) {
				final CtExecutableReference<CloneBuilder> buildExecRef = factory.Executable().createReference("CloneBuilder CtElement#build()");
				return factory.Code().createInvocation(cloneBuilderType, buildExecRef, builderFieldAccess, elementAccess, createFactoryInvocation(elementAccess.clone()));
			}

			private CtInvocation<CloneBuilder> createCloneBuilderCopyInvocation(CtVariableAccess<CtElement> elementVarRead, CtVariableAccess<CtElement> elementVarRead2) {
				final CtExecutableReference<CloneBuilder> buildExecRef = factory.Executable().createReference("CloneBuilder #copy()");
				return factory.Code().createInvocation(builderFieldAccess, buildExecRef, elementVarRead, elementVarRead2);
			}

			private CtInvocation<CloneBuilder> createTailorerScanInvocation(CtVariableAccess elementVarRead, CtVariableAccess localVarRead) {
				final CtExecutableReference<CloneBuilder> buildExecRef = factory.Executable().createReference("CloneHelper #tailor()");
				return factory.Code().createInvocation(cloneHelperFieldAccess, buildExecRef, elementVarRead, localVarRead);
			}

			/**
			 * Creates <code>element.getFactory().Core().createElement()</code>.
			 *
			 * @param elementAccess <code>element</code>.
			 */
			private CtInvocation createFactoryInvocation(CtVariableAccess<CtElement> elementAccess) {
				final String typeName = elementAccess.getType().getSimpleName();
				// #getFactory()
				final CtInvocation<Object> getFactory = factory.Code().createInvocation(null, factory.Executable().createReference("Factory CtElement#getFactory()"));
				getFactory.setTarget(elementAccess);
				// Factory#Core() or Factory#Internal()
				final String factoryName = internals.contains(typeName) ? "Internal" : "Core";
				final CtInvocation<Object> coreFactory = factory.Code().createInvocation(getFactory, factory.Executable().createReference("CoreFactory Factory#" + factoryName + "()"));
				// CoreFactory#createElement()
				return factory.Code().createInvocation(coreFactory, factory.Executable().createReference("CoreFactory CtElement#create" + typeName.substring(2, typeName.length()) + "()"));
			}
		}.scan(getFactory().Class().get(CtScanner.class));

		new CtScanner() {
			private final List<String> excludesAST = Arrays.asList(//
					"spoon.support.reflect.declaration.CtTypeInformationImpl", "spoon.support.reflect.code.CtAbstractInvocationImpl", //
					"spoon.support.reflect.declaration.CtTypeInformationImpl", "spoon.support.reflect.code.CtAbstractSwitchImpl", //
					"spoon.support.reflect.declaration.CtTypedElementImpl", "spoon.support.reflect.declaration.CtVariableImpl", //
					"spoon.support.reflect.reference.CtActualTypeContainerImpl", "spoon.support.reflect.code.CtCFlowBreakImpl", "spoon.support.reflect.code.CtLabelledFlowBreakImpl", //
					"spoon.support.reflect.declaration.CtCodeSnippetImpl", "spoon.support.reflect.declaration.CtFormalTypeDeclarerImpl", //
					"spoon.support.reflect.declaration.CtGenericElementImpl", "spoon.support.reflect.reference.CtGenericElementReferenceImpl", //
					"spoon.support.reflect.declaration.CtModifiableImpl", "spoon.support.reflect.declaration.CtMultiTypedElementImpl", //
					"spoon.support.reflect.declaration.CtTypeMemberImpl", "spoon.support.reflect.code.CtRHSReceiverImpl",
					"spoon.support.reflect.declaration.CtShadowableImpl", "spoon.support.reflect.code.CtBodyHolderImpl", "spoon.support.reflect.declaration.CtModuleDirectiveImpl");
			private final List<String> excludesFields = Arrays.asList("factory", "elementValues", "target", "rootFragment", "originalSourceCode", "myPartialSourcePosition");
			private final Set<String> collectionClasses = new HashSet<>(Arrays.asList(
					List.class.getName(), Collection.class.getName(), Set.class.getName(),
					ModelList.class.getName(), ModelSet.class.getName()));
			private final CtTypeReference<CtElement> CTELEMENT_REFERENCE = factory.Type().createReference(CtElement.class);
			private final CtClass<?> GETTER_TEMPLATE_MATCHER_CLASS = factory.Class().get(GENERATING_CLONE_PACKAGE + ".GetterTemplateMatcher");
			private final CtClass<?> SETTER_TEMPLATE_MATCHER_CLASS = factory.Class().get(GENERATING_CLONE_PACKAGE + ".SetterTemplateMatcher");

			@Override
			public <T> void visitCtMethod(CtMethod<T> element) {
				if (!element.getSimpleName().startsWith("visitCt") && !element.getSimpleName().startsWith("scanCt")) {
					return;
				}
				if ("scanCtVisitable".equals(element.getSimpleName())) {
					return;
				}
				final String qualifiedNameOfImplClass = "spoon.support" + element.getParameters().get(0).getType().getQualifiedName().substring(5) + "Impl";
				if (excludesAST.contains(qualifiedNameOfImplClass)) {
					return;
				}
				final CtType<?> declaration = factory.Class().get(qualifiedNameOfImplClass);
				if (declaration == null) {
					throw new SpoonException(qualifiedNameOfImplClass + " doesn't have declaration in the source path for " + element.getSignature());
				}

				CtMethod<T> clone = element.clone();
				clone.getBody().getStatements().clear();
				for (CtField<?> ctField : declaration.getFields()) {
					if (excludesFields.contains(ctField.getSimpleName())) {
						continue;
					}
					if (isConstantOrStaticOrTransient(ctField)) {
						continue;
					}
					if (isSubTypeOfCtElement(ctField.getType())) {
						continue;
					}
					final CtMethod<?> setterOfField = getSetterOf(ctField);
					final CtInvocation<?> setterInvocation = createSetterInvocation(//
							element.getParameters().get(0).getType(), setterOfField, //
							createGetterInvocation(element.getParameters().get(0), getGetterOf(ctField)));
					final List<CtMethod<?>> methodsToAvoid = getCtMethodThrowUnsupportedOperation(setterOfField);
					if (!methodsToAvoid.isEmpty()) {
						clone.getBody().addStatement(createProtectionToException(setterInvocation, methodsToAvoid));
					} else {
						clone.getBody().addStatement(setterInvocation);
					}
				}
				if (!clone.getBody().getStatements().isEmpty()) {
					clone.getBody().insertEnd(createSuperInvocation(element));

					// Add auto-generated comment.
					final CtComment comment = factory.Core().createComment();
					comment.setCommentType(CtComment.CommentType.INLINE);
					comment.setContent("auto-generated, see " + CloneVisitorGenerator.class.getName());
					clone.addComment(comment);

					targetBuilder.addMethod(clone);
				}
			}

			/**
			 * Creates <code>if (!(other instanceof CtX && other instanceof CtY && ..)) {}</code>.
			 */
			private CtIf createProtectionToException(CtInvocation<?> setterInvocation, List<CtMethod<?>> methodsAvoid) {
				final CtIf anIf = factory.Core().createIf();
				anIf.setCondition(factory.Core().createUnaryOperator().setOperand(createBinaryConditions(methodsAvoid)).setKind(UnaryOperatorKind.NOT));
				anIf.setThenStatement(factory.Code().createCtBlock(setterInvocation));
				return anIf;
			}

			/**
			 * Creates <code>condition && condition && ...</code>.
			 *
			 * @param methodsAvoid Methods to avoid.
			 */
			private CtExpression<Object> createBinaryConditions(List<CtMethod<?>> methodsAvoid) {
				CtExpression<Object> left = null;
				CtExpression<Object> right;
				for (int i = 0; i < methodsAvoid.size(); i++) {
					final CtInterface<?> ctInterface = getInterfaceOf(methodsAvoid.get(i).getDeclaringType());
					if (i == 0) {
						left = factory.Code().createBinaryOperator(otherRead, //
								factory.Code().createTypeAccess(ctInterface.getReference()), //
								BinaryOperatorKind.INSTANCEOF);
					} else {
						right = factory.Code().createBinaryOperator(otherRead, //
								factory.Code().createTypeAccess(ctInterface.getReference()), //
								BinaryOperatorKind.INSTANCEOF);

						left = factory.Code().createBinaryOperator(left, right, BinaryOperatorKind.OR);
					}
				}
				return left;
			}

			/**
			 * Query to get all methods which throw an UnsupportedOperationException. We must avoid to call these methods during a clone process.
			 */
			private List<CtMethod<?>> getCtMethodThrowUnsupportedOperation(CtMethod<?> method) {
				final List<CtMethod<?>> avoid = new ArrayList<>();
				final CtInterface<?> ctInterface = getInterfaceOf(method.getDeclaringType());
				if (ctInterface == null) {
					return avoid;
				}
				final CtMethod<?> declarationMethod = getMethodByCtMethod(ctInterface, method);
				for (CtMethod<?> ctMethod : Query.getElements(factory, new OverridingMethodFilter(declarationMethod))) {
					if (!avoidThrowUnsupportedOperationException(ctMethod)) {
						avoid.add(ctMethod);
					}
				}
				return avoid;
			}

			/**
			 * Check if the candidate method throw an UnsupportedOperationException.
			 */
			private boolean avoidThrowUnsupportedOperationException(CtMethod<?> candidate) {
				if (candidate.getBody() == null) {
					//abstract method of interface
					return true;
				}
				if (candidate.getBody().getStatements().size() != 1) {
					return true;
				}
				if (!(candidate.getBody().getStatement(0) instanceof CtThrow)) {
					return true;
				}
				CtThrow ctThrow = candidate.getBody().getStatement(0);
				if (!(ctThrow.getThrownExpression() instanceof CtConstructorCall)) {
					return true;
				}
				final CtConstructorCall<? extends Throwable> thrownExpression = (CtConstructorCall<? extends Throwable>) ctThrow.getThrownExpression();
				if (!thrownExpression.getType().equals(factory.Type().createReference(UnsupportedOperationException.class))) {
					return true;
				}
				return false;
			}

			/**
			 * Query to get a method from a CtMethod.
			 */
			private CtMethod<?> getMethodByCtMethod(CtType<?> ctType, CtMethod<?> method) {
				for (CtMethod<?> ctMethod : ctType.getAllMethods()) {
					if (!method.getSimpleName().equals(ctMethod.getSimpleName())) {
						continue;
					}
					boolean cont = method.getParameters().size() == ctMethod.getParameters().size();
					for (int i = 0; cont && i < method.getParameters().size(); i++) {
						if (!method.getParameters().get(i).getType().equals(ctMethod.getParameters().get(i).getType())) {
							cont = false;
						}
					}
					if (cont) {
						return ctMethod;
					}
				}
				throw new AssertionError("Can't find method " + method.getSignature() + " in the given interface " + ctType.getQualifiedName());
			}

			/**
			 * Query to get the interface of the class.
			 */
			private CtInterface<?> getInterfaceOf(CtType<?> declaringType) {
				final CtTypeReference<?>[] interfaces = declaringType.getSuperInterfaces().toArray(new CtTypeReference[declaringType.getSuperInterfaces().size()]);
				for (CtTypeReference<?> anInterface : interfaces) {
					if (anInterface.getSimpleName().equals(declaringType.getSimpleName().substring(0, declaringType.getSimpleName().length() - 4))) {
						return (CtInterface<?>) anInterface.getDeclaration();
					}
				}
				throw new AssertionError("You should have the interface for the implementation " + declaringType.getQualifiedName());
			}

			/**
			 * Creates <code>super.visitMethod(element)</code>.
			 *
			 * @param element <code>visitMethod</code>.
			 */
			private <T> CtInvocation<T> createSuperInvocation(CtMethod<T> element) {
				return factory.Code()
						.createInvocation(factory.Core().createSuperAccess(), element.getReference(), factory.Code().createVariableRead(element.getParameters().get(0).getReference(), false));
			}

			/**
			 * Creates <code>((CtElement) other).setX(element.getX())</code>
			 * or <code>((CtElement) other).setX(new Collection(element.getX()))</code>
			 * if the field is a collection.
			 *
			 * @param type <code>CtElement</code>
			 * @param setter <code>setX</code>.
			 * @param getter <code>getX</code>.
			 */
			private CtInvocation<?> createSetterInvocation(CtTypeReference<?> type, CtMethod<?> setter, CtInvocation<?> getter) {
				return factory.Code().createInvocation(otherRead.clone().addTypeCast(type), setter.getReference(), getter);
			}

			/**
			 * Creates <code>element.getX()</code>.
			 *
			 * @param element <code>element</code>.
			 * @param getter <code>getX</code>.
			 */
			private CtInvocation<?> createGetterInvocation(CtParameter<?> element, CtMethod<?> getter) {
				return factory.Code().createInvocation(factory.Code().createVariableRead(element.getReference(), false), getter.getReference());
			}

			/**
			 * Query to get the setter of given field.
			 */
			private <T> CtMethod<?> getSetterOf(final CtField<T> ctField) {
				if (ctField.getType().equals(getFactory().createCtTypeReference(CtModifierHandler.class))) {
					return ctField.getDeclaringType().getMethodsByName("setModifiers").get(0);
				}
				// Search by name convention.
				for (CtMethod<?> ctMethod : ctField.getDeclaringType().getMethods()) {
					if (ctMethod.getSimpleName().startsWith("set") && ctMethod.getSimpleName().toLowerCase().contains(ctField.getSimpleName().toLowerCase())) {
						if (ctMethod.getParameters().size() != 1) {
							continue;
						}
						if (!ctMethod.getParameters().get(0).getType().equals(ctField.getType())) {
							continue;
						}
						return ctMethod;
					}
				}
				final List<CtMethod> matchers = ctField.getDeclaringType().getElements(new TypeFilter<CtMethod>(CtMethod.class) {
					@Override
					public boolean matches(CtMethod element) {
						final CtBlock body = element.getBody();
						if (body == null || body.getStatements().size() != 3) {
							return false;
						}
						if (body.getStatement(1) instanceof CtAssignment) {
							final CtExpression assigned = ((CtAssignment) body.getStatement(1)).getAssigned();
							if (!(assigned instanceof CtFieldAccess)) {
								return false;
							}
							if (!((CtFieldAccess) assigned).getVariable().getSimpleName().equals(ctField.getSimpleName())) {
								return false;
							}
						} else {
							return false;
						}
						return true;
					}
				});
				if (matchers.size() != 1) {
					throw new SpoonException("Get more than one setter. Please make an more ingenious method to get setter method. " + matchers.size() + " " + ctField);
				}
				return matchers.get(0);
			}

			/**
			 * Query to get the getter of the given field.
			 */
			private <T> CtMethod<?> getGetterOf(CtField<T> ctField) {
				if (ctField.getType().equals(getFactory().createCtTypeReference(CtModifierHandler.class))) {
					return ctField.getDeclaringType().getMethod("getModifiers");
				}
				// Search by name convention.
				for (CtMethod<?> ctMethod : ctField.getDeclaringType().getMethods()) {
					if ((ctMethod.getSimpleName().startsWith("get") || ctMethod.getSimpleName().startsWith("is")) //
							&& ctMethod.getSimpleName().toLowerCase().contains(ctField.getSimpleName().toLowerCase())) {
						if (!ctMethod.getType().equals(ctField.getType())) {
							continue;
						}
						if (!ctMethod.getParameters().isEmpty()) {
							continue;
						}
						return ctMethod;
					}
				}
				// Search with template.
				final CtBlock<?> templateRoot = GETTER_TEMPLATE_MATCHER_CLASS.getMethod("getElement").getBody();
				((CtReturn) templateRoot.getStatement(0)).setReturnedExpression(factory.Code().createVariableRead(ctField.getReference(), true));
				List<CtMethod> matchers = ctField.getDeclaringType().getElements(new TypeFilter<CtMethod>(CtMethod.class) {
					@Override
					public boolean matches(CtMethod element) {
						return element.getBody().toString().equals(templateRoot.toString());
					}
				});
				if (matchers.isEmpty()) {
					throw new SpoonException("No getter found for field " + ctField);
				}

				if (matchers.size() > 1) {
					throw new SpoonException("Get more than one getter (" + StringUtils.join(matchers, ";") + "). Please make an more ingenious method to get getter method.");
				}
				return matchers.get(0);
			}

			/**
			 * Check if the type is a subtype of CtElement.
			 */
			private boolean isSubTypeOfCtElement(CtTypeReference<?> type) {
				if (!type.isPrimitive() && !type.equals(factory.Type().STRING)) {
					if (type.isSubtypeOf(factory.Type().createReference(CtElement.class))) {
						return true;
					}
					if (collectionClasses.contains(type.getQualifiedName())) {
						if (type.getActualTypeArguments().get(0).isSubtypeOf(CTELEMENT_REFERENCE)) {
							return true;
						}
					}
				}
				return false;
			}

			private boolean isConstantOrStaticOrTransient(CtField<?> ctField) {
				return ctField.getModifiers().contains(ModifierKind.FINAL) || ctField.getModifiers().contains(ModifierKind.STATIC)  || ctField.getModifiers().contains(ModifierKind.TRANSIENT) ;
			}
		}.scan(getFactory().Class().get(CtInheritanceScanner.class));
	}

	private CtClass<Object> createCloneVisitor() {
		final CtPackage aPackage = getFactory().Package().getOrCreate(TARGET_CLONE_PACKAGE);
		final CtClass<Object> target = getFactory().Class().get(GENERATING_CLONE);
		//remove type from old package so it can be added to new package
		target.delete();
		target.setSimpleName(TARGET_CLONE_TYPE);
		target.addModifier(ModifierKind.PUBLIC);
		aPackage.addType(target);
		final List<CtTypeReference> references = target.getElements(new TypeFilter<CtTypeReference>(CtTypeReference.class) {
			@Override
			public boolean matches(CtTypeReference reference) {
				return GENERATING_CLONE.equals(reference.getQualifiedName());
			}
		});
		for (CtTypeReference reference : references) {
			reference.setSimpleName(TARGET_CLONE_TYPE);
			reference.setPackage(aPackage.getReference());
		}
		target.getConstructors().forEach(c -> c.addModifier(ModifierKind.PUBLIC));
		return target;
	}

	private CtClass<Object> createCloneBuilder() {
		final CtPackage aPackage = getFactory().Package().getOrCreate(TARGET_CLONE_PACKAGE);
		final CtClass<Object> target = getFactory().Class().get(GENERATING_BUILDER_CLONE);
		//remove target from old package so it can be added to new package
		target.delete();
		target.setSimpleName(TARGET_BUILDER_CLONE_TYPE);
		target.addModifier(ModifierKind.PUBLIC);
		aPackage.addType(target);
		final List<CtTypeReference> references = target.getElements(new TypeFilter<CtTypeReference>(CtTypeReference.class) {
			@Override
			public boolean matches(CtTypeReference reference) {
				return GENERATING_BUILDER_CLONE.equals(reference.getQualifiedName());
			}
		});
		for (CtTypeReference reference : references) {
			reference.setSimpleName(TARGET_BUILDER_CLONE_TYPE);
			reference.setPackage(aPackage.getReference());
		}
		return target;
	}
}
