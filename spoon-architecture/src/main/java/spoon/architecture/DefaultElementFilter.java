package spoon.architecture;

import spoon.reflect.code.CtAnnotationFieldAccess;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtContinue;
import spoon.reflect.code.CtExecutableReferenceExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtOperatorAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSwitchExpression;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.CtYieldStatement;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationMethod;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtPackageDeclaration;
import spoon.reflect.declaration.CtPackageExport;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.CtUsedService;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.TypeFilter;

@SuppressWarnings("unchecked")
public enum DefaultElementFilter {
	// we skip CtUnboundVariableReference
	ANNOTATIONS() {
		@Override
		public Filter<CtAnnotation<?>> getFilter() {
			return new TypeFilter<CtAnnotation<?>>(CtAnnotation.class);
		}
	},
	ANNOTATIONS_DEFINITIONS() {
		@Override
		public Filter<CtAnnotationType<?>> getFilter() {
			return new TypeFilter<CtAnnotationType<?>>(CtAnnotationType.class);
		}
	},
	ANONYMOUS_EXECUTABLES() {
		@Override
		public Filter<CtAnonymousExecutable> getFilter() {
			return new TypeFilter<CtAnonymousExecutable>(CtAnonymousExecutable.class);
		}
	},
	ARRAY_READS() {
		@Override
		public Filter<CtArrayRead<?>> getFilter() {
			return new TypeFilter<CtArrayRead<?>>(CtArrayRead.class);
		}
	},
	ARRAY_WRITES() {
		@Override
		public Filter<CtArrayWrite<?>> getFilter() {
			return new TypeFilter<CtArrayWrite<?>>(CtArrayWrite.class);
		}
	},
	/**
	 * TODO: doc
	 * ArrayTypeReference is int[] in int[][]. Multidimensional arrays.
	 */
	ARRAY_TYPE_REFERENCES() {
		@Override
		public Filter<CtArrayTypeReference<?>> getFilter() {
			return new TypeFilter<CtArrayTypeReference<?>>(CtArrayTypeReference.class);
		}
	},
	ASSERTS() {
		@Override
		public Filter<CtAssert<?>> getFilter() {
			return new TypeFilter<CtAssert<?>>(CtAssert.class);
		}
	},
	ASSIGNMENTS() {
		@Override
		public Filter<CtAssignment<?, ?>> getFilter() {
			return new TypeFilter<CtAssignment<?, ?>>(CtAssignment.class);
		}
	},
	BINARY_OPERATORS() {
		@Override
		public Filter<CtBinaryOperator<?>> getFilter() {
			return new TypeFilter<CtBinaryOperator<?>>(CtBinaryOperator.class);
		}
	},
	BLOCKS() {
		@Override
		public Filter<CtBlock<?>> getFilter() {
			return new TypeFilter<CtBlock<?>>(CtBlock.class);
		}
	},
	BREAKS() {
		@Override
		public Filter<CtBreak> getFilter() {
			return new TypeFilter<CtBreak>(CtBreak.class);
		}
	},
	CASES() {
		@Override
		public Filter<CtCase<?>> getFilter() {
			return new TypeFilter<CtCase<?>>(CtCase.class);
		}
	},
	CATCHES() {
		@Override
		public Filter<CtCatch> getFilter() {
			return new TypeFilter<CtCatch>(CtCatch.class);
		}
	},
	TYPE_PARAMETERS() {
		@Override
		public Filter<CtTypeParameter> getFilter() {
			return new TypeFilter<CtTypeParameter>(CtTypeParameter.class);
		}
	},
	CONDITIONALS() {
		@Override
		public Filter<CtConditional<?>> getFilter() {
			return new TypeFilter<CtConditional<?>>(CtConditional.class);
		}
	},
	CONSTRUCTORS() {
		@Override
		public Filter<CtConstructor<?>> getFilter() {
			return new TypeFilter<CtConstructor<?>>(CtConstructor.class);
		}
	},
	CONTINUES() {
		@Override
		public Filter<CtContinue> getFilter() {
			return new TypeFilter<CtContinue>(CtContinue.class);
		}
	},
	ENUMS() {
		@Override
		public Filter<CtEnum<?>> getFilter() {
			return new TypeFilter<CtEnum<?>>(CtEnum.class);
		}
	},
	EXECUTABLE_REFERENCES() {
		@Override
		public Filter<CtExecutableReference<?>> getFilter() {
			return new TypeFilter<CtExecutableReference<?>>(CtExecutableReference.class);
		}
	},
	ENUM_VALUES() {
		@Override
		public Filter<CtEnumValue<?>> getFilter() {
			return new TypeFilter<CtEnumValue<?>>(CtEnumValue.class);
		}
	},
	THIS_ACCESSES() {
		@Override
		public Filter<CtThisAccess<?>> getFilter() {
			return new TypeFilter<CtThisAccess<?>>(CtThisAccess.class);
		}
	},
	FIELD_REFERENCES() {
		@Override
		public Filter<CtFieldReference<?>> getFilter() {
			return new TypeFilter<CtFieldReference<?>>(CtFieldReference.class);
		}
	},
	FOR_LOOPS() {
		@Override
		public Filter<CtFor> getFilter() {
			return new TypeFilter<CtFor>(CtFor.class);
		}
	},
	FOREACH_LOOPS() {
		@Override
		public Filter<CtForEach> getFilter() {
			return new TypeFilter<CtForEach>(CtForEach.class);
		}
	},
	IFS() {
		@Override
		public Filter<CtIf> getFilter() {
			return new TypeFilter<CtIf>(CtIf.class);
		}
	},
	INVOCATIONS() {
		@Override
		public Filter<CtInvocation<?>> getFilter() {
			return new TypeFilter<CtInvocation<?>>(CtInvocation.class);
		}
	},
	LITERALS() {
		@Override
		public Filter<CtLiteral<?>> getFilter() {
			return new TypeFilter<CtLiteral<?>>(CtLiteral.class);
		}
	},
	LOCAL_VARIABLES() {
		@Override
		public Filter<CtLocalVariable<?>> getFilter() {
			return new TypeFilter<CtLocalVariable<?>>(CtLocalVariable.class);
		}
	},
	LOCAL_VARIABLES_REFERENCES() {
		@Override
		public Filter<CtLocalVariableReference<?>> getFilter() {
			return new TypeFilter<CtLocalVariableReference<?>>(CtLocalVariableReference.class);
		}
	},
	CATCH_VARIABLES() {
		@Override
		public Filter<CtCatchVariable<?>> getFilter() {
			return new TypeFilter<CtCatchVariable<?>>(CtCatchVariable.class);
		}
	},
	CATCH_VARIABLES_REFERENCES() {
		@Override
		public Filter<CtCatchVariableReference<?>> getFilter() {
			return new TypeFilter<CtCatchVariableReference<?>>(CtCatchVariableReference.class);
		}
	},
	ANNOTATION_METHODS() {
		@Override
		public Filter<CtAnnotationMethod<?>> getFilter() {
			return new TypeFilter<CtAnnotationMethod<?>>(CtAnnotationMethod.class);
		}
	},
	NEW_ARRAYS() {
		@Override
		public Filter<CtNewArray<?>> getFilter() {
			return new TypeFilter<CtNewArray<?>>(CtNewArray.class);
		}
	},
	CONSTRUCTOR_CALLS() {
		@Override
		public Filter<CtConstructorCall<?>> getFilter() {
			return new TypeFilter<CtConstructorCall<?>>(CtConstructorCall.class);
		}
	},
	NEW_CLASSES() {
		@Override
		public Filter<CtNewClass<?>> getFilter() {
			return new TypeFilter<CtNewClass<?>>(CtNewClass.class);
		}
	},
	LAMBDAS() {
		@Override
		public Filter<CtLambda<?>> getFilter() {
			return new TypeFilter<CtLambda<?>>(CtLambda.class);
		}
	},
	EXECUTABLE_REFERENCE_EXPRESSIONS() {
		@Override
		public Filter<CtExecutableReferenceExpression<?, ?>> getFilter() {
			return new TypeFilter<CtExecutableReferenceExpression<?, ?>>(CtExecutableReferenceExpression.class);
		}
	},
	OPERATOR_ASSIGNMENT() {
		@Override
		public Filter<CtOperatorAssignment<?, ?>> getFilter() {
			return new TypeFilter<CtOperatorAssignment<?, ?>>(CtOperatorAssignment.class);
		}
	},
	PACKAGE_REFERENCES() {
		@Override
		public Filter<CtPackageReference> getFilter() {
			return new TypeFilter<CtPackageReference>(CtPackageReference.class);
		}
	},
	PARAMETERS() {
		@Override
		public Filter<CtParameter<?>> getFilter() {
			return new TypeFilter<CtParameter<?>>(CtParameter.class);
		}
	},
	PARAMETER_REFERENCES() {
		@Override
		public Filter<CtParameterReference<?>> getFilter() {
			return new TypeFilter<CtParameterReference<?>>(CtParameterReference.class);
		}
	},
	RETURNS() {
		@Override
		public Filter<CtReturn<?>> getFilter() {
			return new TypeFilter<CtReturn<?>>(CtReturn.class);
		}
	},
	SWITCHES() {
		@Override
		public Filter<CtSwitch<?>> getFilter() {
			return new TypeFilter<CtSwitch<?>>(CtSwitch.class);
		}
	},
	SWITCH_EXPRESSIONS() {
		@Override
		public Filter<CtSwitchExpression<?, ?>> getFilter() {
			return new TypeFilter<CtSwitchExpression<?, ?>>(CtSwitchExpression.class);
		}
	},
	SYNCHRONIZED() {
		@Override
		public Filter<CtSynchronized> getFilter() {
			return new TypeFilter<CtSynchronized>(CtSynchronized.class);
		}
	},
	THROWS() {
		@Override
		public Filter<CtThrow> getFilter() {
			return new TypeFilter<CtThrow>(CtThrow.class);
		}
	},
	TRIES() {
		@Override
		public Filter<CtTry> getFilter() {
			return new TypeFilter<CtTry>(CtTry.class);
		}
	},
	TRIES_WITH_RESOURCES() {
		@Override
		public Filter<CtTry> getFilter() {
			return new TypeFilter<CtTry>(CtTry.class);
		}
	},
	TYPE_PARAMETER_REFERENCES() {
		@Override
		public Filter<CtTypeParameterReference> getFilter() {
			return new TypeFilter<CtTypeParameterReference>(CtTypeParameterReference.class);
		}
	},
	WILDCARD_REFERENCES() {
		@Override
		public Filter<CtWildcardReference> getFilter() {
			return new TypeFilter<CtWildcardReference>(CtWildcardReference.class);
		}
	},
	INTERSECTION_TYPE_REFERENCES() {
		@Override
		public Filter<CtIntersectionTypeReference<?>> getFilter() {
			return new TypeFilter<CtIntersectionTypeReference<?>>(CtIntersectionTypeReference.class);
		}
	},
	TYPE_ACCESSES() {
		@Override
		public Filter<CtTypeAccess<?>> getFilter() {
			return new TypeFilter<CtTypeAccess<?>>(CtTypeAccess.class);
		}
	},
	UNARY_OPERATORS() {
		@Override
		public Filter<CtUnaryOperator<?>> getFilter() {
			return new TypeFilter<CtUnaryOperator<?>>(CtUnaryOperator.class);
		}
	},
	VARIABLE_READS() {
		@Override
		public Filter<CtVariableRead<?>> getFilter() {
			return new TypeFilter<CtVariableRead<?>>(CtVariableRead.class);
		}
	},
	VARIABLE_WRITES() {
		@Override
		public Filter<CtVariableWrite<?>> getFilter() {
			return new TypeFilter<CtVariableWrite<?>>(CtVariableWrite.class);
		}
	},
	WHILES() {
		@Override
		public Filter<CtWhile> getFilter() {
			return new TypeFilter<CtWhile>(CtWhile.class);
		}
	},
	ANNOTATION_FIELD_ACCESSES() {
		@Override
		public Filter<CtAnnotationFieldAccess<?>> getFilter() {
			return new TypeFilter<CtAnnotationFieldAccess<?>>(CtAnnotationFieldAccess.class);
		}
	},
	FIELD_READS() {
		@Override
		public Filter<CtFieldRead<?>> getFilter() {
			return new TypeFilter<CtFieldRead<?>>(CtFieldRead.class);
		}
	},
	FIELD_WRITES() {
		@Override
		public Filter<CtFieldWrite<?>> getFilter() {
			return new TypeFilter<CtFieldWrite<?>>(CtFieldWrite.class);
		}
	},
	SUPER_ACCESSES() {
		@Override
		public Filter<CtSuperAccess<?>> getFilter() {
			return new TypeFilter<CtSuperAccess<?>>(CtSuperAccess.class);
		}
	},
	COMMENTS() {
		@Override
		public Filter<CtComment> getFilter() {
			return new TypeFilter<CtComment>(CtComment.class);
		}
	},
	JAVA_DOCS() {
		@Override
		public Filter<CtJavaDoc> getFilter() {
			return new TypeFilter<CtJavaDoc>(CtJavaDoc.class);
		}
	},
	JAVA_DOC_TAGS() {
		@Override
		public Filter<CtJavaDocTag> getFilter() {
			return new TypeFilter<CtJavaDocTag>(CtJavaDocTag.class);
		}
	},
	IMPORTS() {
		@Override
		public Filter<CtImport> getFilter() {
			return new TypeFilter<CtImport>(CtImport.class);
		}
	},
	MODULES() {
		@Override
		public Filter<CtModule> getFilter() {
			return new TypeFilter<CtModule>(CtModule.class);
		}
	},
	PACKAGE_EXPORTS() {
		@Override
		public Filter<CtPackageExport> getFilter() {
			return new TypeFilter<CtPackageExport>(CtPackageExport.class);
		}
	},
	MODULE_REQUIREMENTS() {
		@Override
		public Filter<CtModuleRequirement> getFilter() {
			return new TypeFilter<CtModuleRequirement>(CtModuleRequirement.class);
		}
	},
	PROVIDED_SERVICES() {
		@Override
		public Filter<CtProvidedService> getFilter() {
			return new TypeFilter<CtProvidedService>(CtProvidedService.class);
		}
	},
	USED_SERVICES() {
		@Override
		public Filter<CtUsedService> getFilter() {
			return new TypeFilter<CtUsedService>(CtUsedService.class);
		}
	},
	COMPILATION_UNITS() {
		@Override
		public Filter<CtCompilationUnit> getFilter() {
			return new TypeFilter<CtCompilationUnit>(CtCompilationUnit.class);
		}
	},
	PACKAGE_DECLARATIONS() {
		@Override
		public Filter<CtPackageDeclaration> getFilter() {
			return new TypeFilter<CtPackageDeclaration>(CtPackageDeclaration.class);
		}
	},
	TYPE_MEMBER_WILDCARD_IMPORT_REFERENCES() {
		@Override
		public Filter<CtTypeMemberWildcardImportReference> getFilter() {
			return new TypeFilter<CtTypeMemberWildcardImportReference>(CtTypeMemberWildcardImportReference.class);
		}
	},
	YIELD_STATEMENTS() {
		@Override
		public Filter<CtYieldStatement> getFilter() {
			return new TypeFilter<CtYieldStatement>(CtYieldStatement.class);
		}
	},
	METHODS() {
		@Override
		public Filter<CtMethod<?>> getFilter() {
			return new TypeFilter<CtMethod<?>>(CtMethod.class);
		}
	},
	FIELDS() {
		@Override
		public Filter<CtField<?>> getFilter() {
			return new TypeFilter<CtField<?>>(CtField.class);
		}
	},
	CLASSES() {
		@Override
		public Filter<CtClass<?>> getFilter() {
			return new TypeFilter<CtClass<?>>(CtClass.class);

		}
	},
	INTERFACES() {
		@Override
		public Filter<CtInterface<?>> getFilter() {
			return new TypeFilter<CtInterface<?>>(CtInterface.class);

		}
	},
	TYPES() {
		@Override
		public AbstractFilter<CtElement> getFilter() {
			return new AbstractFilter<CtElement>() {
				private AbstractFilter<CtType<?>> filter = new TypeFilter<CtType<?>>(CtType.class);
				@Override
				public boolean matches(CtElement element) {
					if (element instanceof CtType) {
						return filter.matches((CtType<?>) element) && !(element instanceof CtTypeParameter);
					}
					return false;
				}

			};
		}
	},
	PACKAGES() {
		@Override
		public Filter<CtPackage> getFilter() {
			return new TypeFilter<CtPackage>(CtPackage.class);

		}
	},
	TYPE_REFERENCE() {
		@Override
		public Filter<CtTypeReference<?>> getFilter() {
			return new TypeFilter<CtTypeReference<?>>(CtTypeReference.class);

		}
};
	public abstract <T extends CtElement> Filter<T> getFilter();
}
