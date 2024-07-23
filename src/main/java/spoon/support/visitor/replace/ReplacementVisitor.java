/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.visitor.replace;
/**
 * Used to replace an element by another one.
 *
 * This class is generated automatically by the processor spoon.generating.ReplacementVisitorGenerator.
 */
public class ReplacementVisitor extends spoon.reflect.visitor.CtScanner {
	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtRecordPatternPatternListReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtRecordPattern element;

		CtRecordPatternPatternListReplaceListener(spoon.reflect.code.CtRecordPattern element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setPatternList(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtRecordPatternRecordTypeReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.code.CtRecordPattern element;

		CtRecordPatternRecordTypeReplaceListener(spoon.reflect.code.CtRecordPattern element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setRecordType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtCasePatternPatternReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtPattern> {
		private final spoon.reflect.code.CtCasePattern element;

		CtCasePatternPatternReplaceListener(spoon.reflect.code.CtCasePattern element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtPattern replace) {
			this.element.setPattern(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtReceiverParameterTypeReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.declaration.CtTypedElement element;

		CtReceiverParameterTypeReplaceListener(spoon.reflect.declaration.CtTypedElement element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtRecordRecordComponentsReplaceListener implements spoon.support.visitor.replace.ReplaceSetListener<java.util.Set> {
		private final spoon.reflect.declaration.CtRecord element;

		CtRecordRecordComponentsReplaceListener(spoon.reflect.declaration.CtRecord element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Set replace) {
			this.element.setRecordComponents(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtTypePatternVariableReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtLocalVariable> {
		private final spoon.reflect.code.CtTypePattern element;

		CtTypePatternVariableReplaceListener(spoon.reflect.code.CtTypePattern element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtLocalVariable replace) {
			this.element.setVariable(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtYieldStatementExpressionReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtYieldStatement element;

		CtYieldStatementExpressionReplaceListener(spoon.reflect.code.CtYieldStatement element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtTypeMemberWildcardImportReferenceTypeReferenceReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.reference.CtTypeMemberWildcardImportReference element;

		CtTypeMemberWildcardImportReferenceTypeReferenceReplaceListener(spoon.reflect.reference.CtTypeMemberWildcardImportReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setTypeReference(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtPackageDeclarationReferenceReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtPackageReference> {
		private final spoon.reflect.declaration.CtPackageDeclaration element;

		CtPackageDeclarationReferenceReplaceListener(spoon.reflect.declaration.CtPackageDeclaration element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtPackageReference replace) {
			this.element.setReference(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtCompilationUnitDeclaredTypeReferencesReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.declaration.CtCompilationUnit element;

		CtCompilationUnitDeclaredTypeReferencesReplaceListener(spoon.reflect.declaration.CtCompilationUnit element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setDeclaredTypeReferences(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtCompilationUnitDeclaredModuleReferenceReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtModuleReference> {
		private final spoon.reflect.declaration.CtCompilationUnit element;

		CtCompilationUnitDeclaredModuleReferenceReplaceListener(spoon.reflect.declaration.CtCompilationUnit element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtModuleReference replace) {
			this.element.setDeclaredModuleReference(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtCompilationUnitImportsReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.Collection> {
		private final spoon.reflect.declaration.CtCompilationUnit element;

		CtCompilationUnitImportsReplaceListener(spoon.reflect.declaration.CtCompilationUnit element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Collection replace) {
			this.element.setImports(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtCompilationUnitPackageDeclarationReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.declaration.CtPackageDeclaration> {
		private final spoon.reflect.declaration.CtCompilationUnit element;

		CtCompilationUnitPackageDeclarationReplaceListener(spoon.reflect.declaration.CtCompilationUnit element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.declaration.CtPackageDeclaration replace) {
			this.element.setPackageDeclaration(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtUsedServiceServiceTypeReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.declaration.CtUsedService element;

		CtUsedServiceServiceTypeReplaceListener(spoon.reflect.declaration.CtUsedService element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setServiceType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtProvidedServiceImplementationTypesReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.declaration.CtProvidedService element;

		CtProvidedServiceImplementationTypesReplaceListener(spoon.reflect.declaration.CtProvidedService element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setImplementationTypes(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtProvidedServiceServiceTypeReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.declaration.CtProvidedService element;

		CtProvidedServiceServiceTypeReplaceListener(spoon.reflect.declaration.CtProvidedService element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setServiceType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtModuleRequirementModuleReferenceReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtModuleReference> {
		private final spoon.reflect.declaration.CtModuleRequirement element;

		CtModuleRequirementModuleReferenceReplaceListener(spoon.reflect.declaration.CtModuleRequirement element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtModuleReference replace) {
			this.element.setModuleReference(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtPackageExportTargetExportReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.declaration.CtPackageExport element;

		CtPackageExportTargetExportReplaceListener(spoon.reflect.declaration.CtPackageExport element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setTargetExport(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtPackageExportPackageReferenceReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtPackageReference> {
		private final spoon.reflect.declaration.CtPackageExport element;

		CtPackageExportPackageReferenceReplaceListener(spoon.reflect.declaration.CtPackageExport element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtPackageReference replace) {
			this.element.setPackageReference(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtModuleRootPackageReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.declaration.CtPackage> {
		private final spoon.reflect.declaration.CtModule element;

		CtModuleRootPackageReplaceListener(spoon.reflect.declaration.CtModule element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.declaration.CtPackage replace) {
			this.element.setRootPackage(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtModuleModuleDirectivesReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.declaration.CtModule element;

		CtModuleModuleDirectivesReplaceListener(spoon.reflect.declaration.CtModule element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setModuleDirectives(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtImportReferenceReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtReference> {
		private final spoon.reflect.declaration.CtImport element;

		CtImportReferenceReplaceListener(spoon.reflect.declaration.CtImport element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtReference replace) {
			this.element.setReference(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtJavaDocTagsReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtJavaDoc element;

		CtJavaDocTagsReplaceListener(spoon.reflect.code.CtJavaDoc element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setTags(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtFieldAccessVariableReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtFieldReference> {
		private final spoon.reflect.code.CtVariableAccess element;

		CtFieldAccessVariableReplaceListener(spoon.reflect.code.CtVariableAccess element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtFieldReference replace) {
			this.element.setVariable(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtWhileLoopingExpressionReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtWhile element;

		CtWhileLoopingExpressionReplaceListener(spoon.reflect.code.CtWhile element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setLoopingExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtVariableAccessVariableReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtVariableReference> {
		private final spoon.reflect.code.CtVariableAccess element;

		CtVariableAccessVariableReplaceListener(spoon.reflect.code.CtVariableAccess element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtVariableReference replace) {
			this.element.setVariable(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtUnaryOperatorOperandReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtUnaryOperator element;

		CtUnaryOperatorOperandReplaceListener(spoon.reflect.code.CtUnaryOperator element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setOperand(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtTypeAccessAccessedTypeReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.code.CtTypeAccess element;

		CtTypeAccessAccessedTypeReplaceListener(spoon.reflect.code.CtTypeAccess element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setAccessedType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtIntersectionTypeReferenceBoundsReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.reference.CtIntersectionTypeReference element;

		CtIntersectionTypeReferenceBoundsReplaceListener(spoon.reflect.reference.CtIntersectionTypeReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setBounds(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtWildcardReferenceBoundingTypeReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.reference.CtWildcardReference element;

		CtWildcardReferenceBoundingTypeReplaceListener(spoon.reflect.reference.CtWildcardReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setBoundingType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtTryWithResourceResourcesReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtTryWithResource element;

		CtTryWithResourceResourcesReplaceListener(spoon.reflect.code.CtTryWithResource element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setResources(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtTryFinalizerReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtBlock> {
		private final spoon.reflect.code.CtTry element;

		CtTryFinalizerReplaceListener(spoon.reflect.code.CtTry element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtBlock replace) {
			this.element.setFinalizer(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtTryCatchersReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtTry element;

		CtTryCatchersReplaceListener(spoon.reflect.code.CtTry element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setCatchers(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtTryBodyReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtBlock> {
		private final spoon.reflect.code.CtBodyHolder element;

		CtTryBodyReplaceListener(spoon.reflect.code.CtBodyHolder element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtBlock replace) {
			this.element.setBody(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtThrowThrownExpressionReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtThrow element;

		CtThrowThrownExpressionReplaceListener(spoon.reflect.code.CtThrow element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setThrownExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtSynchronizedBlockReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtBlock> {
		private final spoon.reflect.code.CtSynchronized element;

		CtSynchronizedBlockReplaceListener(spoon.reflect.code.CtSynchronized element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtBlock replace) {
			this.element.setBlock(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtSynchronizedExpressionReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtSynchronized element;

		CtSynchronizedExpressionReplaceListener(spoon.reflect.code.CtSynchronized element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtAbstractSwitchCasesReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtAbstractSwitch element;

		CtAbstractSwitchCasesReplaceListener(spoon.reflect.code.CtAbstractSwitch element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setCases(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtAbstractSwitchSelectorReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtAbstractSwitch element;

		CtAbstractSwitchSelectorReplaceListener(spoon.reflect.code.CtAbstractSwitch element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setSelector(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtReturnReturnedExpressionReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtReturn element;

		CtReturnReturnedExpressionReplaceListener(spoon.reflect.code.CtReturn element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setReturnedExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtPackageTypesReplaceListener implements spoon.support.visitor.replace.ReplaceSetListener<java.util.Set> {
		private final spoon.reflect.declaration.CtPackage element;

		CtPackageTypesReplaceListener(spoon.reflect.declaration.CtPackage element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Set replace) {
			this.element.setTypes(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtPackagePackagesReplaceListener implements spoon.support.visitor.replace.ReplaceSetListener<java.util.Set> {
		private final spoon.reflect.declaration.CtPackage element;

		CtPackagePackagesReplaceListener(spoon.reflect.declaration.CtPackage element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Set replace) {
			this.element.setPackages(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtExecutableReferenceExpressionExecutableReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtExecutableReference> {
		private final spoon.reflect.code.CtExecutableReferenceExpression element;

		CtExecutableReferenceExpressionExecutableReplaceListener(spoon.reflect.code.CtExecutableReferenceExpression element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtExecutableReference replace) {
			this.element.setExecutable(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtLambdaExpressionReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtLambda element;

		CtLambdaExpressionReplaceListener(spoon.reflect.code.CtLambda element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtNewClassAnonymousClassReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.declaration.CtClass> {
		private final spoon.reflect.code.CtNewClass element;

		CtNewClassAnonymousClassReplaceListener(spoon.reflect.code.CtNewClass element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.declaration.CtClass replace) {
			this.element.setAnonymousClass(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtNewArrayDimensionExpressionsReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtNewArray element;

		CtNewArrayDimensionExpressionsReplaceListener(spoon.reflect.code.CtNewArray element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setDimensionExpressions(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtNewArrayElementsReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtNewArray element;

		CtNewArrayElementsReplaceListener(spoon.reflect.code.CtNewArray element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setElements(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtAnnotationMethodDefaultExpressionReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.declaration.CtAnnotationMethod element;

		CtAnnotationMethodDefaultExpressionReplaceListener(spoon.reflect.declaration.CtAnnotationMethod element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setDefaultExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtMultiTypedElementMultiTypesReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.declaration.CtMultiTypedElement element;

		CtMultiTypedElementMultiTypesReplaceListener(spoon.reflect.declaration.CtMultiTypedElement element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setMultiTypes(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtAbstractInvocationArgumentsReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtAbstractInvocation element;

		CtAbstractInvocationArgumentsReplaceListener(spoon.reflect.code.CtAbstractInvocation element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setArguments(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtAbstractInvocationExecutableReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtExecutableReference> {
		private final spoon.reflect.code.CtAbstractInvocation element;

		CtAbstractInvocationExecutableReplaceListener(spoon.reflect.code.CtAbstractInvocation element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtExecutableReference replace) {
			this.element.setExecutable(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtIfElseStatementReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtStatement> {
		private final spoon.reflect.code.CtIf element;

		CtIfElseStatementReplaceListener(spoon.reflect.code.CtIf element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtStatement replace) {
			this.element.setElseStatement(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtIfThenStatementReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtStatement> {
		private final spoon.reflect.code.CtIf element;

		CtIfThenStatementReplaceListener(spoon.reflect.code.CtIf element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtStatement replace) {
			this.element.setThenStatement(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtIfConditionReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtIf element;

		CtIfConditionReplaceListener(spoon.reflect.code.CtIf element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setCondition(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtForEachExpressionReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtForEach element;

		CtForEachExpressionReplaceListener(spoon.reflect.code.CtForEach element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtForEachVariableReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtLocalVariable> {
		private final spoon.reflect.code.CtForEach element;

		CtForEachVariableReplaceListener(spoon.reflect.code.CtForEach element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtLocalVariable replace) {
			this.element.setVariable(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtForForUpdateReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtFor element;

		CtForForUpdateReplaceListener(spoon.reflect.code.CtFor element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setForUpdate(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtForExpressionReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtFor element;

		CtForExpressionReplaceListener(spoon.reflect.code.CtFor element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtForForInitReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtFor element;

		CtForForInitReplaceListener(spoon.reflect.code.CtFor element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setForInit(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtVariableReferenceTypeReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.reference.CtVariableReference element;

		CtVariableReferenceTypeReplaceListener(spoon.reflect.reference.CtVariableReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtFieldReferenceDeclaringTypeReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.reference.CtFieldReference element;

		CtFieldReferenceDeclaringTypeReplaceListener(spoon.reflect.reference.CtFieldReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setDeclaringType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtAnnotationFieldAccessVariableReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtFieldReference> {
		private final spoon.reflect.code.CtVariableAccess element;

		CtAnnotationFieldAccessVariableReplaceListener(spoon.reflect.code.CtVariableAccess element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtFieldReference replace) {
			this.element.setVariable(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtVariableDefaultExpressionReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.declaration.CtVariable element;

		CtVariableDefaultExpressionReplaceListener(spoon.reflect.declaration.CtVariable element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setDefaultExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtExecutableReferenceParametersReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.reference.CtExecutableReference element;

		CtExecutableReferenceParametersReplaceListener(spoon.reflect.reference.CtExecutableReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setParameters(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtExecutableReferenceTypeReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.reference.CtExecutableReference element;

		CtExecutableReferenceTypeReplaceListener(spoon.reflect.reference.CtExecutableReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtExecutableReferenceDeclaringTypeReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.reference.CtExecutableReference element;

		CtExecutableReferenceDeclaringTypeReplaceListener(spoon.reflect.reference.CtExecutableReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setDeclaringType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtEnumEnumValuesReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.declaration.CtEnum element;

		CtEnumEnumValuesReplaceListener(spoon.reflect.declaration.CtEnum element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setEnumValues(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtLoopBodyReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtStatement> {
		private final spoon.reflect.code.CtBodyHolder element;

		CtLoopBodyReplaceListener(spoon.reflect.code.CtBodyHolder element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtStatement replace) {
			this.element.setBody(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtDoLoopingExpressionReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtDo element;

		CtDoLoopingExpressionReplaceListener(spoon.reflect.code.CtDo element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setLoopingExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtExecutableThrownTypesReplaceListener implements spoon.support.visitor.replace.ReplaceSetListener<java.util.Set> {
		private final spoon.reflect.declaration.CtExecutable element;

		CtExecutableThrownTypesReplaceListener(spoon.reflect.declaration.CtExecutable element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Set replace) {
			this.element.setThrownTypes(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtExecutableParametersReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.declaration.CtExecutable element;

		CtExecutableParametersReplaceListener(spoon.reflect.declaration.CtExecutable element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setParameters(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtExecutableReceiverParameterReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.declaration.CtReceiverParameter> {
		private final spoon.reflect.declaration.CtExecutable element;

		CtExecutableReceiverParameterReplaceListener(spoon.reflect.declaration.CtExecutable element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.declaration.CtReceiverParameter replace) {
			this.element.setReceiverParameter(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtConditionalElseExpressionReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtConditional element;

		CtConditionalElseExpressionReplaceListener(spoon.reflect.code.CtConditional element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setElseExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtConditionalThenExpressionReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtConditional element;

		CtConditionalThenExpressionReplaceListener(spoon.reflect.code.CtConditional element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setThenExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtConditionalConditionReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtConditional element;

		CtConditionalConditionReplaceListener(spoon.reflect.code.CtConditional element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setCondition(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtSealablePermittedTypesReplaceListener implements spoon.support.visitor.replace.ReplaceSetListener<java.util.Set> {
		private final spoon.reflect.declaration.CtSealable element;

		CtSealablePermittedTypesReplaceListener(spoon.reflect.declaration.CtSealable element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Set replace) {
			this.element.setPermittedTypes(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtFormalTypeDeclarerFormalCtTypeParametersReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.declaration.CtFormalTypeDeclarer element;

		CtFormalTypeDeclarerFormalCtTypeParametersReplaceListener(spoon.reflect.declaration.CtFormalTypeDeclarer element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setFormalCtTypeParameters(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtTypeInformationSuperInterfacesReplaceListener implements spoon.support.visitor.replace.ReplaceSetListener<java.util.Set> {
		private final spoon.reflect.declaration.CtType element;

		CtTypeInformationSuperInterfacesReplaceListener(spoon.reflect.declaration.CtType element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Set replace) {
			this.element.setSuperInterfaces(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtTypeInformationSuperclassReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.declaration.CtType element;

		CtTypeInformationSuperclassReplaceListener(spoon.reflect.declaration.CtType element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setSuperclass(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtCatchBodyReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtBlock> {
		private final spoon.reflect.code.CtBodyHolder element;

		CtCatchBodyReplaceListener(spoon.reflect.code.CtBodyHolder element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtBlock replace) {
			this.element.setBody(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtCatchParameterReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtCatchVariable> {
		private final spoon.reflect.code.CtCatch element;

		CtCatchParameterReplaceListener(spoon.reflect.code.CtCatch element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtCatchVariable replace) {
			this.element.setParameter(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtCaseGuardReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtCase element;

		CtCaseGuardReplaceListener(spoon.reflect.code.CtCase element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setGuard(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtCaseCaseExpressionsReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtCase element;

		CtCaseCaseExpressionsReplaceListener(spoon.reflect.code.CtCase element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setCaseExpressions(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtStatementListStatementsReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtStatementList element;

		CtStatementListStatementsReplaceListener(spoon.reflect.code.CtStatementList element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setStatements(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtBinaryOperatorRightHandOperandReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtBinaryOperator element;

		CtBinaryOperatorRightHandOperandReplaceListener(spoon.reflect.code.CtBinaryOperator element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setRightHandOperand(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtBinaryOperatorLeftHandOperandReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtBinaryOperator element;

		CtBinaryOperatorLeftHandOperandReplaceListener(spoon.reflect.code.CtBinaryOperator element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setLeftHandOperand(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtRHSReceiverAssignmentReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtRHSReceiver element;

		CtRHSReceiverAssignmentReplaceListener(spoon.reflect.code.CtRHSReceiver element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setAssignment(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtAssignmentAssignedReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtAssignment element;

		CtAssignmentAssignedReplaceListener(spoon.reflect.code.CtAssignment element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setAssigned(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtAssertExpressionReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtAssert element;

		CtAssertExpressionReplaceListener(spoon.reflect.code.CtAssert element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtAssertAssertExpressionReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtAssert element;

		CtAssertAssertExpressionReplaceListener(spoon.reflect.code.CtAssert element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setAssertExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtActualTypeContainerActualTypeArgumentsReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.reference.CtActualTypeContainer element;

		CtActualTypeContainerActualTypeArgumentsReplaceListener(spoon.reflect.reference.CtActualTypeContainer element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setActualTypeArguments(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtArrayTypeReferenceComponentTypeReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.reference.CtArrayTypeReference element;

		CtArrayTypeReferenceComponentTypeReplaceListener(spoon.reflect.reference.CtArrayTypeReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setComponentType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtTypeReferenceDeclaringTypeReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.reference.CtTypeReference element;

		CtTypeReferenceDeclaringTypeReplaceListener(spoon.reflect.reference.CtTypeReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setDeclaringType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtTypeReferencePackageReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtPackageReference> {
		private final spoon.reflect.reference.CtTypeReference element;

		CtTypeReferencePackageReplaceListener(spoon.reflect.reference.CtTypeReference element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtPackageReference replace) {
			this.element.setPackage(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtArrayAccessIndexExpressionReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtArrayAccess element;

		CtArrayAccessIndexExpressionReplaceListener(spoon.reflect.code.CtArrayAccess element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setIndexExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtTargetedExpressionTargetReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtTargetedExpression element;

		CtTargetedExpressionTargetReplaceListener(spoon.reflect.code.CtTargetedExpression element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setTarget(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtExpressionTypeCastsReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtExpression element;

		CtExpressionTypeCastsReplaceListener(spoon.reflect.code.CtExpression element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setTypeCasts(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtExecutableBodyReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.code.CtBlock> {
		private final spoon.reflect.code.CtBodyHolder element;

		CtExecutableBodyReplaceListener(spoon.reflect.code.CtBodyHolder element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtBlock replace) {
			this.element.setBody(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtTypeTypeMembersReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.declaration.CtType element;

		CtTypeTypeMembersReplaceListener(spoon.reflect.declaration.CtType element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setTypeMembers(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtAnnotationValuesReplaceListener implements spoon.support.visitor.replace.ReplaceMapListener<java.util.Map> {
		private final spoon.reflect.declaration.CtAnnotation element;

		CtAnnotationValuesReplaceListener(spoon.reflect.declaration.CtAnnotation element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Map replace) {
			this.element.setValues(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtElementAnnotationsReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.declaration.CtElement element;

		CtElementAnnotationsReplaceListener(spoon.reflect.declaration.CtElement element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setAnnotations(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtAnnotationAnnotationTypeReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.declaration.CtAnnotation element;

		CtAnnotationAnnotationTypeReplaceListener(spoon.reflect.declaration.CtAnnotation element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setAnnotationType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtElementCommentsReplaceListener implements spoon.support.visitor.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.declaration.CtElement element;

		CtElementCommentsReplaceListener(spoon.reflect.declaration.CtElement element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setComments(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	static class CtTypedElementTypeReplaceListener implements spoon.support.visitor.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.declaration.CtTypedElement element;

		CtTypedElementTypeReplaceListener(spoon.reflect.declaration.CtTypedElement element) {
			this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setType(replace);
		}
	}

	public static void replace(spoon.reflect.declaration.CtElement original, spoon.reflect.declaration.CtElement replace) {
		new spoon.support.visitor.replace.ReplacementVisitor(original, replace == null ? spoon.support.visitor.replace.ReplacementVisitor.EMPTY : new spoon.reflect.declaration.CtElement[]{ replace }).scan(original.getParent());
	}

	public static <E extends spoon.reflect.declaration.CtElement> void replace(spoon.reflect.declaration.CtElement original, java.util.Collection<E> replaces) {
		new spoon.support.visitor.replace.ReplacementVisitor(original, replaces.toArray(new spoon.reflect.declaration.CtElement[0])).scan(original.getParent());
	}

	private spoon.reflect.declaration.CtElement original;

	private spoon.reflect.declaration.CtElement[] replace;

	private static final spoon.reflect.declaration.CtElement[] EMPTY = new spoon.reflect.declaration.CtElement[0];

	private ReplacementVisitor(spoon.reflect.declaration.CtElement original, spoon.reflect.declaration.CtElement... replace) {
		this.original = original;
		this.replace = (replace == null) ? spoon.support.visitor.replace.ReplacementVisitor.EMPTY : replace;
	}

	private <K, V extends spoon.reflect.declaration.CtElement> void replaceInMapIfExist(java.util.Map<K, V> mapProtected, spoon.support.visitor.replace.ReplaceMapListener listener) {
		java.util.Map<K, V> map = new java.util.HashMap<>(mapProtected);
		V shouldBeDeleted = null;
		K key = null;
		for (java.util.Map.Entry<K, V> entry : map.entrySet()) {
			if (entry.getValue() == original) {
				shouldBeDeleted = entry.getValue();
				key = entry.getKey();
				break;
			}
		}
		if (shouldBeDeleted != null) {
			if (replace.length > 0) {
				if (replace.length > 1) {
					throw new spoon.support.visitor.replace.InvalidReplaceException("Cannot replace single value by multiple values in " + listener.getClass().getSimpleName());
				}
				V val = ((V) (replace[0]));
				if (val != null) {
					map.put(key, val);
					val.setParent(shouldBeDeleted.getParent());
				} else {
					map.remove(key);
				}
			} else {
				map.remove(key);
			}
			listener.set(map);
		}
	}

	private <T extends spoon.reflect.declaration.CtElement> void replaceInSetIfExist(java.util.Set<T> setProtected, spoon.support.visitor.replace.ReplaceSetListener listener) {
		java.util.Set<T> set = new java.util.HashSet<>(setProtected);
		T shouldBeDeleted = null;
		for (T element : set) {
			if (element == original) {
				shouldBeDeleted = element;
				break;
			}
		}
		if (shouldBeDeleted != null) {
			set.remove(shouldBeDeleted);
			for (spoon.reflect.declaration.CtElement ele : replace) {
				if (ele != null) {
					set.add(((T) (ele)));
					ele.setParent(shouldBeDeleted.getParent());
				}
			}
			listener.set(set);
		}
	}

	private <T extends spoon.reflect.declaration.CtElement> void replaceInListIfExist(java.util.List<T> listProtected, spoon.support.visitor.replace.ReplaceListListener listener) {
		java.util.List<T> list = new java.util.ArrayList<>(listProtected);
		T shouldBeDeleted = null;
		int index = 0;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == original) {
				index = i;
				shouldBeDeleted = list.get(i);
				break;
			}
		}
		if (shouldBeDeleted != null) {
			list.remove(index);
			if (replace.length > 0) {
				for (spoon.reflect.declaration.CtElement aReplace : replace) {
					T ele = ((T) (aReplace));
					if (ele != null) {
						list.add(index, ele);
						ele.setParent(shouldBeDeleted.getParent());
						index = index + 1;
					}
				}
			}
			listener.set(list);
		}
	}

	private void replaceElementIfExist(spoon.reflect.declaration.CtElement candidate, spoon.support.visitor.replace.ReplaceListener listener) {
		if (candidate == original) {
			spoon.reflect.declaration.CtElement val = null;
			if (replace.length > 0) {
				if (replace.length > 1) {
					throw new spoon.support.visitor.replace.InvalidReplaceException("Cannot replace single value by multiple values in " + listener.getClass().getSimpleName());
				}
				val = replace[0];
			}
			if (val != null) {
				val.setParent(candidate.getParent());
			}
			listener.set(val);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <A extends java.lang.annotation.Annotation> void visitCtAnnotation(final spoon.reflect.declaration.CtAnnotation<A> annotation) {
		replaceElementIfExist(annotation.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(annotation));
		replaceInListIfExist(annotation.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(annotation));
		replaceElementIfExist(annotation.getAnnotationType(), new spoon.support.visitor.replace.ReplacementVisitor.CtAnnotationAnnotationTypeReplaceListener(annotation));
		replaceInListIfExist(annotation.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(annotation));
		replaceInMapIfExist(annotation.getValues(), new spoon.support.visitor.replace.ReplacementVisitor.CtAnnotationValuesReplaceListener(annotation));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <A extends java.lang.annotation.Annotation> void visitCtAnnotationType(final spoon.reflect.declaration.CtAnnotationType<A> annotationType) {
		replaceInListIfExist(annotationType.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(annotationType));
		replaceInListIfExist(annotationType.getTypeMembers(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeTypeMembersReplaceListener(annotationType));
		replaceInListIfExist(annotationType.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(annotationType));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtAnonymousExecutable(final spoon.reflect.declaration.CtAnonymousExecutable anonymousExec) {
		replaceInListIfExist(anonymousExec.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(anonymousExec));
		replaceElementIfExist(anonymousExec.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableBodyReplaceListener(anonymousExec));
		replaceInListIfExist(anonymousExec.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(anonymousExec));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtArrayRead(final spoon.reflect.code.CtArrayRead<T> arrayRead) {
		replaceInListIfExist(arrayRead.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(arrayRead));
		replaceElementIfExist(arrayRead.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(arrayRead));
		replaceInListIfExist(arrayRead.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(arrayRead));
		replaceElementIfExist(arrayRead.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(arrayRead));
		replaceElementIfExist(arrayRead.getIndexExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtArrayAccessIndexExpressionReplaceListener(arrayRead));
		replaceInListIfExist(arrayRead.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(arrayRead));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtArrayWrite(final spoon.reflect.code.CtArrayWrite<T> arrayWrite) {
		replaceInListIfExist(arrayWrite.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(arrayWrite));
		replaceElementIfExist(arrayWrite.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(arrayWrite));
		replaceInListIfExist(arrayWrite.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(arrayWrite));
		replaceElementIfExist(arrayWrite.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(arrayWrite));
		replaceElementIfExist(arrayWrite.getIndexExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtArrayAccessIndexExpressionReplaceListener(arrayWrite));
		replaceInListIfExist(arrayWrite.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(arrayWrite));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtArrayTypeReference(final spoon.reflect.reference.CtArrayTypeReference<T> reference) {
		replaceElementIfExist(reference.getPackage(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferencePackageReplaceListener(reference));
		replaceElementIfExist(reference.getDeclaringType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferenceDeclaringTypeReplaceListener(reference));
		replaceElementIfExist(reference.getComponentType(), new spoon.support.visitor.replace.ReplacementVisitor.CtArrayTypeReferenceComponentTypeReplaceListener(reference));
		replaceInListIfExist(reference.getActualTypeArguments(), new spoon.support.visitor.replace.ReplacementVisitor.CtActualTypeContainerActualTypeArgumentsReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtAssert(final spoon.reflect.code.CtAssert<T> asserted) {
		replaceInListIfExist(asserted.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(asserted));
		replaceElementIfExist(asserted.getAssertExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtAssertAssertExpressionReplaceListener(asserted));
		replaceElementIfExist(asserted.getExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtAssertExpressionReplaceListener(asserted));
		replaceInListIfExist(asserted.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(asserted));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T, A extends T> void visitCtAssignment(final spoon.reflect.code.CtAssignment<T, A> assignement) {
		replaceInListIfExist(assignement.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(assignement));
		replaceElementIfExist(assignement.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(assignement));
		replaceInListIfExist(assignement.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(assignement));
		replaceElementIfExist(assignement.getAssigned(), new spoon.support.visitor.replace.ReplacementVisitor.CtAssignmentAssignedReplaceListener(assignement));
		replaceElementIfExist(assignement.getAssignment(), new spoon.support.visitor.replace.ReplacementVisitor.CtRHSReceiverAssignmentReplaceListener(assignement));
		replaceInListIfExist(assignement.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(assignement));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtBinaryOperator(final spoon.reflect.code.CtBinaryOperator<T> operator) {
		replaceInListIfExist(operator.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(operator));
		replaceElementIfExist(operator.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(operator));
		replaceInListIfExist(operator.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(operator));
		replaceElementIfExist(operator.getLeftHandOperand(), new spoon.support.visitor.replace.ReplacementVisitor.CtBinaryOperatorLeftHandOperandReplaceListener(operator));
		replaceElementIfExist(operator.getRightHandOperand(), new spoon.support.visitor.replace.ReplacementVisitor.CtBinaryOperatorRightHandOperandReplaceListener(operator));
		replaceInListIfExist(operator.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(operator));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <R> void visitCtBlock(final spoon.reflect.code.CtBlock<R> block) {
		replaceInListIfExist(block.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(block));
		replaceInListIfExist(block.getStatements(), new spoon.support.visitor.replace.ReplacementVisitor.CtStatementListStatementsReplaceListener(block));
		replaceInListIfExist(block.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(block));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtBreak(final spoon.reflect.code.CtBreak breakStatement) {
		replaceInListIfExist(breakStatement.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(breakStatement));
		replaceInListIfExist(breakStatement.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(breakStatement));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <S> void visitCtCase(final spoon.reflect.code.CtCase<S> caseStatement) {
		replaceInListIfExist(caseStatement.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(caseStatement));
		replaceInListIfExist(caseStatement.getCaseExpressions(), new spoon.support.visitor.replace.ReplacementVisitor.CtCaseCaseExpressionsReplaceListener(caseStatement));
		replaceElementIfExist(caseStatement.getGuard(), new spoon.support.visitor.replace.ReplacementVisitor.CtCaseGuardReplaceListener(caseStatement));
		replaceInListIfExist(caseStatement.getStatements(), new spoon.support.visitor.replace.ReplacementVisitor.CtStatementListStatementsReplaceListener(caseStatement));
		replaceInListIfExist(caseStatement.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(caseStatement));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtCatch(final spoon.reflect.code.CtCatch catchBlock) {
		replaceInListIfExist(catchBlock.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(catchBlock));
		replaceElementIfExist(catchBlock.getParameter(), new spoon.support.visitor.replace.ReplacementVisitor.CtCatchParameterReplaceListener(catchBlock));
		replaceElementIfExist(catchBlock.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtCatchBodyReplaceListener(catchBlock));
		replaceInListIfExist(catchBlock.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(catchBlock));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtClass(final spoon.reflect.declaration.CtClass<T> ctClass) {
		replaceInListIfExist(ctClass.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(ctClass));
		replaceElementIfExist(ctClass.getSuperclass(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeInformationSuperclassReplaceListener(ctClass));
		replaceInSetIfExist(ctClass.getSuperInterfaces(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeInformationSuperInterfacesReplaceListener(ctClass));
		replaceInListIfExist(ctClass.getFormalCtTypeParameters(), new spoon.support.visitor.replace.ReplacementVisitor.CtFormalTypeDeclarerFormalCtTypeParametersReplaceListener(ctClass));
		replaceInListIfExist(ctClass.getTypeMembers(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeTypeMembersReplaceListener(ctClass));
		replaceInSetIfExist(ctClass.getPermittedTypes(), new spoon.support.visitor.replace.ReplacementVisitor.CtSealablePermittedTypesReplaceListener(ctClass));
		replaceInListIfExist(ctClass.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(ctClass));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtTypeParameter(spoon.reflect.declaration.CtTypeParameter typeParameter) {
		replaceInListIfExist(typeParameter.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(typeParameter));
		replaceElementIfExist(typeParameter.getSuperclass(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeInformationSuperclassReplaceListener(typeParameter));
		replaceInListIfExist(typeParameter.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(typeParameter));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtConditional(final spoon.reflect.code.CtConditional<T> conditional) {
		replaceElementIfExist(conditional.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(conditional));
		replaceInListIfExist(conditional.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(conditional));
		replaceElementIfExist(conditional.getCondition(), new spoon.support.visitor.replace.ReplacementVisitor.CtConditionalConditionReplaceListener(conditional));
		replaceElementIfExist(conditional.getThenExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtConditionalThenExpressionReplaceListener(conditional));
		replaceElementIfExist(conditional.getElseExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtConditionalElseExpressionReplaceListener(conditional));
		replaceInListIfExist(conditional.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(conditional));
		replaceInListIfExist(conditional.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(conditional));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtConstructor(final spoon.reflect.declaration.CtConstructor<T> c) {
		replaceInListIfExist(c.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(c));
		replaceElementIfExist(c.getReceiverParameter(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableReceiverParameterReplaceListener(c));
		replaceInListIfExist(c.getParameters(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableParametersReplaceListener(c));
		replaceInSetIfExist(c.getThrownTypes(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableThrownTypesReplaceListener(c));
		replaceInListIfExist(c.getFormalCtTypeParameters(), new spoon.support.visitor.replace.ReplacementVisitor.CtFormalTypeDeclarerFormalCtTypeParametersReplaceListener(c));
		replaceElementIfExist(c.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableBodyReplaceListener(c));
		replaceInListIfExist(c.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(c));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtContinue(final spoon.reflect.code.CtContinue continueStatement) {
		replaceInListIfExist(continueStatement.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(continueStatement));
		replaceInListIfExist(continueStatement.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(continueStatement));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtDo(final spoon.reflect.code.CtDo doLoop) {
		replaceInListIfExist(doLoop.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(doLoop));
		replaceElementIfExist(doLoop.getLoopingExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtDoLoopingExpressionReplaceListener(doLoop));
		replaceElementIfExist(doLoop.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtLoopBodyReplaceListener(doLoop));
		replaceInListIfExist(doLoop.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(doLoop));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T extends java.lang.Enum<?>> void visitCtEnum(final spoon.reflect.declaration.CtEnum<T> ctEnum) {
		replaceInListIfExist(ctEnum.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(ctEnum));
		replaceInSetIfExist(ctEnum.getSuperInterfaces(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeInformationSuperInterfacesReplaceListener(ctEnum));
		replaceInListIfExist(ctEnum.getTypeMembers(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeTypeMembersReplaceListener(ctEnum));
		replaceInListIfExist(ctEnum.getEnumValues(), new spoon.support.visitor.replace.ReplacementVisitor.CtEnumEnumValuesReplaceListener(ctEnum));
		replaceInListIfExist(ctEnum.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(ctEnum));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtExecutableReference(final spoon.reflect.reference.CtExecutableReference<T> reference) {
		replaceElementIfExist(reference.getDeclaringType(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableReferenceDeclaringTypeReplaceListener(reference));
		replaceElementIfExist(reference.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableReferenceTypeReplaceListener(reference));
		replaceInListIfExist(reference.getParameters(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableReferenceParametersReplaceListener(reference));
		replaceInListIfExist(reference.getActualTypeArguments(), new spoon.support.visitor.replace.ReplacementVisitor.CtActualTypeContainerActualTypeArgumentsReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
		replaceInListIfExist(reference.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(reference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtField(final spoon.reflect.declaration.CtField<T> f) {
		replaceInListIfExist(f.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(f));
		replaceElementIfExist(f.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(f));
		replaceElementIfExist(f.getDefaultExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableDefaultExpressionReplaceListener(f));
		replaceInListIfExist(f.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(f));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtEnumValue(final spoon.reflect.declaration.CtEnumValue<T> enumValue) {
		replaceInListIfExist(enumValue.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(enumValue));
		replaceElementIfExist(enumValue.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(enumValue));
		replaceElementIfExist(enumValue.getDefaultExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableDefaultExpressionReplaceListener(enumValue));
		replaceInListIfExist(enumValue.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(enumValue));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtThisAccess(final spoon.reflect.code.CtThisAccess<T> thisAccess) {
		replaceInListIfExist(thisAccess.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(thisAccess));
		replaceInListIfExist(thisAccess.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(thisAccess));
		replaceElementIfExist(thisAccess.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(thisAccess));
		replaceInListIfExist(thisAccess.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(thisAccess));
		replaceElementIfExist(thisAccess.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(thisAccess));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtAnnotationFieldAccess(final spoon.reflect.code.CtAnnotationFieldAccess<T> annotationFieldAccess) {
		replaceInListIfExist(annotationFieldAccess.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(annotationFieldAccess));
		replaceInListIfExist(annotationFieldAccess.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(annotationFieldAccess));
		replaceInListIfExist(annotationFieldAccess.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(annotationFieldAccess));
		replaceElementIfExist(annotationFieldAccess.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(annotationFieldAccess));
		replaceElementIfExist(annotationFieldAccess.getVariable(), new spoon.support.visitor.replace.ReplacementVisitor.CtAnnotationFieldAccessVariableReplaceListener(annotationFieldAccess));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtFieldReference(final spoon.reflect.reference.CtFieldReference<T> reference) {
		replaceElementIfExist(reference.getDeclaringType(), new spoon.support.visitor.replace.ReplacementVisitor.CtFieldReferenceDeclaringTypeReplaceListener(reference));
		replaceElementIfExist(reference.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableReferenceTypeReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtFor(final spoon.reflect.code.CtFor forLoop) {
		replaceInListIfExist(forLoop.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(forLoop));
		replaceInListIfExist(forLoop.getForInit(), new spoon.support.visitor.replace.ReplacementVisitor.CtForForInitReplaceListener(forLoop));
		replaceElementIfExist(forLoop.getExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtForExpressionReplaceListener(forLoop));
		replaceInListIfExist(forLoop.getForUpdate(), new spoon.support.visitor.replace.ReplacementVisitor.CtForForUpdateReplaceListener(forLoop));
		replaceElementIfExist(forLoop.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtLoopBodyReplaceListener(forLoop));
		replaceInListIfExist(forLoop.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(forLoop));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtForEach(final spoon.reflect.code.CtForEach foreach) {
		replaceInListIfExist(foreach.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(foreach));
		replaceElementIfExist(foreach.getVariable(), new spoon.support.visitor.replace.ReplacementVisitor.CtForEachVariableReplaceListener(foreach));
		replaceElementIfExist(foreach.getExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtForEachExpressionReplaceListener(foreach));
		replaceElementIfExist(foreach.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtLoopBodyReplaceListener(foreach));
		replaceInListIfExist(foreach.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(foreach));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtIf(final spoon.reflect.code.CtIf ifElement) {
		replaceInListIfExist(ifElement.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(ifElement));
		replaceElementIfExist(ifElement.getCondition(), new spoon.support.visitor.replace.ReplacementVisitor.CtIfConditionReplaceListener(ifElement));
		replaceElementIfExist(((spoon.reflect.code.CtStatement) (ifElement.getThenStatement())), new spoon.support.visitor.replace.ReplacementVisitor.CtIfThenStatementReplaceListener(ifElement));
		replaceElementIfExist(((spoon.reflect.code.CtStatement) (ifElement.getElseStatement())), new spoon.support.visitor.replace.ReplacementVisitor.CtIfElseStatementReplaceListener(ifElement));
		replaceInListIfExist(ifElement.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(ifElement));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtInterface(final spoon.reflect.declaration.CtInterface<T> intrface) {
		replaceInListIfExist(intrface.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(intrface));
		replaceInSetIfExist(intrface.getSuperInterfaces(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeInformationSuperInterfacesReplaceListener(intrface));
		replaceInListIfExist(intrface.getFormalCtTypeParameters(), new spoon.support.visitor.replace.ReplacementVisitor.CtFormalTypeDeclarerFormalCtTypeParametersReplaceListener(intrface));
		replaceInListIfExist(intrface.getTypeMembers(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeTypeMembersReplaceListener(intrface));
		replaceInSetIfExist(intrface.getPermittedTypes(), new spoon.support.visitor.replace.ReplacementVisitor.CtSealablePermittedTypesReplaceListener(intrface));
		replaceInListIfExist(intrface.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(intrface));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtInvocation(final spoon.reflect.code.CtInvocation<T> invocation) {
		replaceInListIfExist(invocation.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(invocation));
		replaceInListIfExist(invocation.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(invocation));
		replaceElementIfExist(invocation.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(invocation));
		replaceElementIfExist(invocation.getExecutable(), new spoon.support.visitor.replace.ReplacementVisitor.CtAbstractInvocationExecutableReplaceListener(invocation));
		replaceInListIfExist(invocation.getArguments(), new spoon.support.visitor.replace.ReplacementVisitor.CtAbstractInvocationArgumentsReplaceListener(invocation));
		replaceInListIfExist(invocation.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(invocation));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtLiteral(final spoon.reflect.code.CtLiteral<T> literal) {
		replaceInListIfExist(literal.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(literal));
		replaceElementIfExist(literal.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(literal));
		replaceInListIfExist(literal.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(literal));
		replaceInListIfExist(literal.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(literal));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtTextBlock(final spoon.reflect.code.CtTextBlock literal) {
		replaceInListIfExist(literal.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(literal));
		replaceElementIfExist(literal.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(literal));
		replaceInListIfExist(literal.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(literal));
		replaceInListIfExist(literal.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(literal));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtLocalVariable(final spoon.reflect.code.CtLocalVariable<T> localVariable) {
		replaceInListIfExist(localVariable.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(localVariable));
		replaceElementIfExist(localVariable.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(localVariable));
		replaceElementIfExist(localVariable.getDefaultExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableDefaultExpressionReplaceListener(localVariable));
		replaceInListIfExist(localVariable.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(localVariable));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtLocalVariableReference(final spoon.reflect.reference.CtLocalVariableReference<T> reference) {
		replaceElementIfExist(reference.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableReferenceTypeReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtCatchVariable(final spoon.reflect.code.CtCatchVariable<T> catchVariable) {
		replaceInListIfExist(catchVariable.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(catchVariable));
		replaceInListIfExist(catchVariable.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(catchVariable));
		replaceInListIfExist(catchVariable.getMultiTypes(), new spoon.support.visitor.replace.ReplacementVisitor.CtMultiTypedElementMultiTypesReplaceListener(catchVariable));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtCatchVariableReference(final spoon.reflect.reference.CtCatchVariableReference<T> reference) {
		replaceElementIfExist(reference.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableReferenceTypeReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtMethod(final spoon.reflect.declaration.CtMethod<T> m) {
		replaceInListIfExist(m.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(m));
		replaceInListIfExist(m.getFormalCtTypeParameters(), new spoon.support.visitor.replace.ReplacementVisitor.CtFormalTypeDeclarerFormalCtTypeParametersReplaceListener(m));
		replaceElementIfExist(m.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(m));
		replaceElementIfExist(m.getReceiverParameter(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableReceiverParameterReplaceListener(m));
		replaceInListIfExist(m.getParameters(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableParametersReplaceListener(m));
		replaceInSetIfExist(m.getThrownTypes(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableThrownTypesReplaceListener(m));
		replaceElementIfExist(m.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableBodyReplaceListener(m));
		replaceInListIfExist(m.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(m));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtAnnotationMethod(spoon.reflect.declaration.CtAnnotationMethod<T> annotationMethod) {
		replaceInListIfExist(annotationMethod.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(annotationMethod));
		replaceElementIfExist(annotationMethod.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(annotationMethod));
		replaceElementIfExist(annotationMethod.getDefaultExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtAnnotationMethodDefaultExpressionReplaceListener(annotationMethod));
		replaceInListIfExist(annotationMethod.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(annotationMethod));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtNewArray(final spoon.reflect.code.CtNewArray<T> newArray) {
		replaceInListIfExist(newArray.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(newArray));
		replaceElementIfExist(newArray.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(newArray));
		replaceInListIfExist(newArray.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(newArray));
		replaceInListIfExist(newArray.getElements(), new spoon.support.visitor.replace.ReplacementVisitor.CtNewArrayElementsReplaceListener(newArray));
		replaceInListIfExist(newArray.getDimensionExpressions(), new spoon.support.visitor.replace.ReplacementVisitor.CtNewArrayDimensionExpressionsReplaceListener(newArray));
		replaceInListIfExist(newArray.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(newArray));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtConstructorCall(final spoon.reflect.code.CtConstructorCall<T> ctConstructorCall) {
		replaceInListIfExist(ctConstructorCall.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(ctConstructorCall));
		replaceInListIfExist(ctConstructorCall.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(ctConstructorCall));
		replaceElementIfExist(ctConstructorCall.getExecutable(), new spoon.support.visitor.replace.ReplacementVisitor.CtAbstractInvocationExecutableReplaceListener(ctConstructorCall));
		replaceElementIfExist(ctConstructorCall.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(ctConstructorCall));
		replaceInListIfExist(ctConstructorCall.getArguments(), new spoon.support.visitor.replace.ReplacementVisitor.CtAbstractInvocationArgumentsReplaceListener(ctConstructorCall));
		replaceInListIfExist(ctConstructorCall.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(ctConstructorCall));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtNewClass(final spoon.reflect.code.CtNewClass<T> newClass) {
		replaceInListIfExist(newClass.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(newClass));
		replaceInListIfExist(newClass.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(newClass));
		replaceElementIfExist(newClass.getExecutable(), new spoon.support.visitor.replace.ReplacementVisitor.CtAbstractInvocationExecutableReplaceListener(newClass));
		replaceElementIfExist(newClass.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(newClass));
		replaceInListIfExist(newClass.getArguments(), new spoon.support.visitor.replace.ReplacementVisitor.CtAbstractInvocationArgumentsReplaceListener(newClass));
		replaceElementIfExist(newClass.getAnonymousClass(), new spoon.support.visitor.replace.ReplacementVisitor.CtNewClassAnonymousClassReplaceListener(newClass));
		replaceInListIfExist(newClass.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(newClass));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtLambda(final spoon.reflect.code.CtLambda<T> lambda) {
		replaceInListIfExist(lambda.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(lambda));
		replaceElementIfExist(lambda.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(lambda));
		replaceInListIfExist(lambda.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(lambda));
		replaceInListIfExist(lambda.getParameters(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableParametersReplaceListener(lambda));
		replaceElementIfExist(lambda.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableBodyReplaceListener(lambda));
		replaceElementIfExist(lambda.getExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtLambdaExpressionReplaceListener(lambda));
		replaceInListIfExist(lambda.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(lambda));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T, E extends spoon.reflect.code.CtExpression<?>> void visitCtExecutableReferenceExpression(final spoon.reflect.code.CtExecutableReferenceExpression<T, E> expression) {
		replaceInListIfExist(expression.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(expression));
		replaceInListIfExist(expression.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(expression));
		replaceElementIfExist(expression.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(expression));
		replaceInListIfExist(expression.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(expression));
		replaceElementIfExist(expression.getExecutable(), new spoon.support.visitor.replace.ReplacementVisitor.CtExecutableReferenceExpressionExecutableReplaceListener(expression));
		replaceElementIfExist(expression.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(expression));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T, A extends T> void visitCtOperatorAssignment(final spoon.reflect.code.CtOperatorAssignment<T, A> assignment) {
		replaceInListIfExist(assignment.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(assignment));
		replaceElementIfExist(assignment.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(assignment));
		replaceInListIfExist(assignment.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(assignment));
		replaceElementIfExist(assignment.getAssigned(), new spoon.support.visitor.replace.ReplacementVisitor.CtAssignmentAssignedReplaceListener(assignment));
		replaceElementIfExist(assignment.getAssignment(), new spoon.support.visitor.replace.ReplacementVisitor.CtRHSReceiverAssignmentReplaceListener(assignment));
		replaceInListIfExist(assignment.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(assignment));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtPackage(final spoon.reflect.declaration.CtPackage ctPackage) {
		replaceInListIfExist(ctPackage.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(ctPackage));
		replaceInSetIfExist(ctPackage.getPackages(), new spoon.support.visitor.replace.ReplacementVisitor.CtPackagePackagesReplaceListener(ctPackage));
		replaceInSetIfExist(ctPackage.getTypes(), new spoon.support.visitor.replace.ReplacementVisitor.CtPackageTypesReplaceListener(ctPackage));
		replaceInListIfExist(ctPackage.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(ctPackage));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtPackageReference(final spoon.reflect.reference.CtPackageReference reference) {
		replaceInListIfExist(reference.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtParameter(final spoon.reflect.declaration.CtParameter<T> parameter) {
		replaceInListIfExist(parameter.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(parameter));
		replaceElementIfExist(parameter.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(parameter));
		replaceInListIfExist(parameter.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(parameter));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtParameterReference(final spoon.reflect.reference.CtParameterReference<T> reference) {
		replaceElementIfExist(reference.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableReferenceTypeReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <R> void visitCtReturn(final spoon.reflect.code.CtReturn<R> returnStatement) {
		replaceInListIfExist(returnStatement.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(returnStatement));
		replaceElementIfExist(returnStatement.getReturnedExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtReturnReturnedExpressionReplaceListener(returnStatement));
		replaceInListIfExist(returnStatement.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(returnStatement));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <R> void visitCtStatementList(final spoon.reflect.code.CtStatementList statements) {
		replaceInListIfExist(statements.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(statements));
		replaceInListIfExist(statements.getStatements(), new spoon.support.visitor.replace.ReplacementVisitor.CtStatementListStatementsReplaceListener(statements));
		replaceInListIfExist(statements.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(statements));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <S> void visitCtSwitch(final spoon.reflect.code.CtSwitch<S> switchStatement) {
		replaceInListIfExist(switchStatement.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(switchStatement));
		replaceElementIfExist(switchStatement.getSelector(), new spoon.support.visitor.replace.ReplacementVisitor.CtAbstractSwitchSelectorReplaceListener(switchStatement));
		replaceInListIfExist(switchStatement.getCases(), new spoon.support.visitor.replace.ReplacementVisitor.CtAbstractSwitchCasesReplaceListener(switchStatement));
		replaceInListIfExist(switchStatement.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(switchStatement));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T, S> void visitCtSwitchExpression(final spoon.reflect.code.CtSwitchExpression<T, S> switchExpression) {
		replaceInListIfExist(switchExpression.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(switchExpression));
		replaceElementIfExist(switchExpression.getSelector(), new spoon.support.visitor.replace.ReplacementVisitor.CtAbstractSwitchSelectorReplaceListener(switchExpression));
		replaceInListIfExist(switchExpression.getCases(), new spoon.support.visitor.replace.ReplacementVisitor.CtAbstractSwitchCasesReplaceListener(switchExpression));
		replaceInListIfExist(switchExpression.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(switchExpression));
		replaceElementIfExist(switchExpression.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(switchExpression));
		replaceInListIfExist(switchExpression.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(switchExpression));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtSynchronized(final spoon.reflect.code.CtSynchronized synchro) {
		replaceInListIfExist(synchro.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(synchro));
		replaceElementIfExist(synchro.getExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtSynchronizedExpressionReplaceListener(synchro));
		replaceElementIfExist(synchro.getBlock(), new spoon.support.visitor.replace.ReplacementVisitor.CtSynchronizedBlockReplaceListener(synchro));
		replaceInListIfExist(synchro.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(synchro));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtThrow(final spoon.reflect.code.CtThrow throwStatement) {
		replaceInListIfExist(throwStatement.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(throwStatement));
		replaceElementIfExist(throwStatement.getThrownExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtThrowThrownExpressionReplaceListener(throwStatement));
		replaceInListIfExist(throwStatement.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(throwStatement));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtTry(final spoon.reflect.code.CtTry tryBlock) {
		replaceInListIfExist(tryBlock.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(tryBlock));
		replaceElementIfExist(tryBlock.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtTryBodyReplaceListener(tryBlock));
		replaceInListIfExist(tryBlock.getCatchers(), new spoon.support.visitor.replace.ReplacementVisitor.CtTryCatchersReplaceListener(tryBlock));
		replaceElementIfExist(tryBlock.getFinalizer(), new spoon.support.visitor.replace.ReplacementVisitor.CtTryFinalizerReplaceListener(tryBlock));
		replaceInListIfExist(tryBlock.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(tryBlock));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtTryWithResource(final spoon.reflect.code.CtTryWithResource tryWithResource) {
		replaceInListIfExist(tryWithResource.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(tryWithResource));
		replaceInListIfExist(tryWithResource.getResources(), new spoon.support.visitor.replace.ReplacementVisitor.CtTryWithResourceResourcesReplaceListener(tryWithResource));
		replaceElementIfExist(tryWithResource.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtTryBodyReplaceListener(tryWithResource));
		replaceInListIfExist(tryWithResource.getCatchers(), new spoon.support.visitor.replace.ReplacementVisitor.CtTryCatchersReplaceListener(tryWithResource));
		replaceElementIfExist(tryWithResource.getFinalizer(), new spoon.support.visitor.replace.ReplacementVisitor.CtTryFinalizerReplaceListener(tryWithResource));
		replaceInListIfExist(tryWithResource.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(tryWithResource));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtTypeParameterReference(final spoon.reflect.reference.CtTypeParameterReference ref) {
		replaceElementIfExist(ref.getPackage(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferencePackageReplaceListener(ref));
		replaceElementIfExist(ref.getDeclaringType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferenceDeclaringTypeReplaceListener(ref));
		replaceInListIfExist(ref.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(ref));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtWildcardReference(spoon.reflect.reference.CtWildcardReference wildcardReference) {
		replaceElementIfExist(wildcardReference.getPackage(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferencePackageReplaceListener(wildcardReference));
		replaceElementIfExist(wildcardReference.getDeclaringType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferenceDeclaringTypeReplaceListener(wildcardReference));
		replaceInListIfExist(wildcardReference.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(wildcardReference));
		replaceElementIfExist(wildcardReference.getBoundingType(), new spoon.support.visitor.replace.ReplacementVisitor.CtWildcardReferenceBoundingTypeReplaceListener(wildcardReference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtIntersectionTypeReference(final spoon.reflect.reference.CtIntersectionTypeReference<T> reference) {
		replaceElementIfExist(reference.getPackage(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferencePackageReplaceListener(reference));
		replaceElementIfExist(reference.getDeclaringType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferenceDeclaringTypeReplaceListener(reference));
		replaceInListIfExist(reference.getActualTypeArguments(), new spoon.support.visitor.replace.ReplacementVisitor.CtActualTypeContainerActualTypeArgumentsReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
		replaceInListIfExist(reference.getBounds(), new spoon.support.visitor.replace.ReplacementVisitor.CtIntersectionTypeReferenceBoundsReplaceListener(reference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtTypeReference(final spoon.reflect.reference.CtTypeReference<T> reference) {
		replaceElementIfExist(reference.getPackage(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferencePackageReplaceListener(reference));
		replaceElementIfExist(reference.getDeclaringType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeReferenceDeclaringTypeReplaceListener(reference));
		replaceInListIfExist(reference.getActualTypeArguments(), new spoon.support.visitor.replace.ReplacementVisitor.CtActualTypeContainerActualTypeArgumentsReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
		replaceInListIfExist(reference.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(reference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtTypeAccess(final spoon.reflect.code.CtTypeAccess<T> typeAccess) {
		replaceInListIfExist(typeAccess.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(typeAccess));
		replaceInListIfExist(typeAccess.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(typeAccess));
		replaceElementIfExist(typeAccess.getAccessedType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeAccessAccessedTypeReplaceListener(typeAccess));
		replaceInListIfExist(typeAccess.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(typeAccess));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtUnaryOperator(final spoon.reflect.code.CtUnaryOperator<T> operator) {
		replaceInListIfExist(operator.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(operator));
		replaceElementIfExist(operator.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(operator));
		replaceInListIfExist(operator.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(operator));
		replaceElementIfExist(operator.getOperand(), new spoon.support.visitor.replace.ReplacementVisitor.CtUnaryOperatorOperandReplaceListener(operator));
		replaceInListIfExist(operator.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(operator));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtVariableRead(final spoon.reflect.code.CtVariableRead<T> variableRead) {
		replaceInListIfExist(variableRead.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(variableRead));
		replaceInListIfExist(variableRead.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(variableRead));
		replaceElementIfExist(variableRead.getVariable(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableAccessVariableReplaceListener(variableRead));
		replaceInListIfExist(variableRead.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(variableRead));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtVariableWrite(final spoon.reflect.code.CtVariableWrite<T> variableWrite) {
		replaceInListIfExist(variableWrite.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(variableWrite));
		replaceInListIfExist(variableWrite.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(variableWrite));
		replaceElementIfExist(variableWrite.getVariable(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableAccessVariableReplaceListener(variableWrite));
		replaceInListIfExist(variableWrite.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(variableWrite));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtWhile(final spoon.reflect.code.CtWhile whileLoop) {
		replaceInListIfExist(whileLoop.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(whileLoop));
		replaceElementIfExist(whileLoop.getLoopingExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtWhileLoopingExpressionReplaceListener(whileLoop));
		replaceElementIfExist(whileLoop.getBody(), new spoon.support.visitor.replace.ReplacementVisitor.CtLoopBodyReplaceListener(whileLoop));
		replaceInListIfExist(whileLoop.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(whileLoop));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtCodeSnippetExpression(final spoon.reflect.code.CtCodeSnippetExpression<T> expression) {
		replaceElementIfExist(expression.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(expression));
		replaceInListIfExist(expression.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(expression));
		replaceInListIfExist(expression.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(expression));
		replaceInListIfExist(expression.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(expression));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtCodeSnippetStatement(final spoon.reflect.code.CtCodeSnippetStatement statement) {
		replaceInListIfExist(statement.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(statement));
		replaceInListIfExist(statement.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(statement));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtUnboundVariableReference(final spoon.reflect.reference.CtUnboundVariableReference<T> reference) {
		replaceElementIfExist(reference.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableReferenceTypeReplaceListener(reference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtFieldRead(final spoon.reflect.code.CtFieldRead<T> fieldRead) {
		replaceInListIfExist(fieldRead.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(fieldRead));
		replaceInListIfExist(fieldRead.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(fieldRead));
		replaceElementIfExist(fieldRead.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(fieldRead));
		replaceElementIfExist(fieldRead.getVariable(), new spoon.support.visitor.replace.ReplacementVisitor.CtFieldAccessVariableReplaceListener(fieldRead));
		replaceInListIfExist(fieldRead.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(fieldRead));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtFieldWrite(final spoon.reflect.code.CtFieldWrite<T> fieldWrite) {
		replaceInListIfExist(fieldWrite.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(fieldWrite));
		replaceInListIfExist(fieldWrite.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(fieldWrite));
		replaceElementIfExist(fieldWrite.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(fieldWrite));
		replaceElementIfExist(fieldWrite.getVariable(), new spoon.support.visitor.replace.ReplacementVisitor.CtFieldAccessVariableReplaceListener(fieldWrite));
		replaceInListIfExist(fieldWrite.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(fieldWrite));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtSuperAccess(final spoon.reflect.code.CtSuperAccess<T> f) {
		replaceInListIfExist(f.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(f));
		replaceInListIfExist(f.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(f));
		replaceInListIfExist(f.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(f));
		replaceElementIfExist(f.getTarget(), new spoon.support.visitor.replace.ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(f));
		replaceElementIfExist(f.getVariable(), new spoon.support.visitor.replace.ReplacementVisitor.CtVariableAccessVariableReplaceListener(f));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtComment(final spoon.reflect.code.CtComment comment) {
		replaceInListIfExist(comment.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(comment));
		replaceInListIfExist(comment.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(comment));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtJavaDoc(final spoon.reflect.code.CtJavaDoc javaDoc) {
		replaceInListIfExist(javaDoc.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(javaDoc));
		replaceInListIfExist(javaDoc.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(javaDoc));
		replaceInListIfExist(javaDoc.getTags(), new spoon.support.visitor.replace.ReplacementVisitor.CtJavaDocTagsReplaceListener(javaDoc));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtJavaDocTag(final spoon.reflect.code.CtJavaDocTag docTag) {
		replaceInListIfExist(docTag.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(docTag));
		replaceInListIfExist(docTag.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(docTag));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtImport(final spoon.reflect.declaration.CtImport ctImport) {
		replaceElementIfExist(ctImport.getReference(), new spoon.support.visitor.replace.ReplacementVisitor.CtImportReferenceReplaceListener(ctImport));
		replaceInListIfExist(ctImport.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(ctImport));
		replaceInListIfExist(ctImport.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(ctImport));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtModule(spoon.reflect.declaration.CtModule module) {
		replaceInListIfExist(module.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(module));
		replaceInListIfExist(module.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(module));
		replaceInListIfExist(module.getModuleDirectives(), new spoon.support.visitor.replace.ReplacementVisitor.CtModuleModuleDirectivesReplaceListener(module));
		replaceElementIfExist(module.getRootPackage(), new spoon.support.visitor.replace.ReplacementVisitor.CtModuleRootPackageReplaceListener(module));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtModuleReference(spoon.reflect.reference.CtModuleReference moduleReference) {
		replaceInListIfExist(moduleReference.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(moduleReference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtPackageExport(spoon.reflect.declaration.CtPackageExport moduleExport) {
		replaceInListIfExist(moduleExport.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(moduleExport));
		replaceElementIfExist(moduleExport.getPackageReference(), new spoon.support.visitor.replace.ReplacementVisitor.CtPackageExportPackageReferenceReplaceListener(moduleExport));
		replaceInListIfExist(moduleExport.getTargetExport(), new spoon.support.visitor.replace.ReplacementVisitor.CtPackageExportTargetExportReplaceListener(moduleExport));
		replaceInListIfExist(moduleExport.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(moduleExport));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtModuleRequirement(spoon.reflect.declaration.CtModuleRequirement moduleRequirement) {
		replaceInListIfExist(moduleRequirement.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(moduleRequirement));
		replaceElementIfExist(moduleRequirement.getModuleReference(), new spoon.support.visitor.replace.ReplacementVisitor.CtModuleRequirementModuleReferenceReplaceListener(moduleRequirement));
		replaceInListIfExist(moduleRequirement.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(moduleRequirement));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtProvidedService(spoon.reflect.declaration.CtProvidedService moduleProvidedService) {
		replaceInListIfExist(moduleProvidedService.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(moduleProvidedService));
		replaceElementIfExist(moduleProvidedService.getServiceType(), new spoon.support.visitor.replace.ReplacementVisitor.CtProvidedServiceServiceTypeReplaceListener(moduleProvidedService));
		replaceInListIfExist(moduleProvidedService.getImplementationTypes(), new spoon.support.visitor.replace.ReplacementVisitor.CtProvidedServiceImplementationTypesReplaceListener(moduleProvidedService));
		replaceInListIfExist(moduleProvidedService.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(moduleProvidedService));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtUsedService(spoon.reflect.declaration.CtUsedService usedService) {
		replaceInListIfExist(usedService.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(usedService));
		replaceElementIfExist(usedService.getServiceType(), new spoon.support.visitor.replace.ReplacementVisitor.CtUsedServiceServiceTypeReplaceListener(usedService));
		replaceInListIfExist(usedService.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(usedService));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtCompilationUnit(spoon.reflect.declaration.CtCompilationUnit compilationUnit) {
		replaceInListIfExist(compilationUnit.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(compilationUnit));
		replaceInListIfExist(compilationUnit.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(compilationUnit));
		replaceElementIfExist(compilationUnit.getPackageDeclaration(), new spoon.support.visitor.replace.ReplacementVisitor.CtCompilationUnitPackageDeclarationReplaceListener(compilationUnit));
		replaceInListIfExist(compilationUnit.getImports(), new spoon.support.visitor.replace.ReplacementVisitor.CtCompilationUnitImportsReplaceListener(compilationUnit));
		replaceElementIfExist(compilationUnit.getDeclaredModuleReference(), new spoon.support.visitor.replace.ReplacementVisitor.CtCompilationUnitDeclaredModuleReferenceReplaceListener(compilationUnit));
		replaceInListIfExist(compilationUnit.getDeclaredTypeReferences(), new spoon.support.visitor.replace.ReplacementVisitor.CtCompilationUnitDeclaredTypeReferencesReplaceListener(compilationUnit));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtPackageDeclaration(spoon.reflect.declaration.CtPackageDeclaration packageDeclaration) {
		replaceInListIfExist(packageDeclaration.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(packageDeclaration));
		replaceInListIfExist(packageDeclaration.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(packageDeclaration));
		replaceElementIfExist(packageDeclaration.getReference(), new spoon.support.visitor.replace.ReplacementVisitor.CtPackageDeclarationReferenceReplaceListener(packageDeclaration));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtTypeMemberWildcardImportReference(spoon.reflect.reference.CtTypeMemberWildcardImportReference wildcardReference) {
		replaceElementIfExist(wildcardReference.getTypeReference(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeMemberWildcardImportReferenceTypeReferenceReplaceListener(wildcardReference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtYieldStatement(spoon.reflect.code.CtYieldStatement statement) {
		replaceInListIfExist(statement.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(statement));
		replaceElementIfExist(statement.getExpression(), new spoon.support.visitor.replace.ReplacementVisitor.CtYieldStatementExpressionReplaceListener(statement));
		replaceInListIfExist(statement.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(statement));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtTypePattern(spoon.reflect.code.CtTypePattern pattern) {
		replaceElementIfExist(pattern.getVariable(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypePatternVariableReplaceListener(pattern));
		replaceInListIfExist(pattern.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(pattern));
		replaceElementIfExist(pattern.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(pattern));
		replaceInListIfExist(pattern.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(pattern));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtRecord(spoon.reflect.declaration.CtRecord recordType) {
		replaceInListIfExist(recordType.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(recordType));
		replaceInSetIfExist(recordType.getSuperInterfaces(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeInformationSuperInterfacesReplaceListener(recordType));
		replaceInListIfExist(recordType.getTypeMembers(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypeTypeMembersReplaceListener(recordType));
		replaceInListIfExist(recordType.getFormalCtTypeParameters(), new spoon.support.visitor.replace.ReplacementVisitor.CtFormalTypeDeclarerFormalCtTypeParametersReplaceListener(recordType));
		replaceInSetIfExist(recordType.getRecordComponents(), new spoon.support.visitor.replace.ReplacementVisitor.CtRecordRecordComponentsReplaceListener(recordType));
		replaceInListIfExist(recordType.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(recordType));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtRecordComponent(spoon.reflect.declaration.CtRecordComponent recordType) {
		replaceInListIfExist(recordType.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(recordType));
		replaceElementIfExist(recordType.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(recordType));
		replaceInListIfExist(recordType.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(recordType));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtReceiverParameter(spoon.reflect.declaration.CtReceiverParameter receiverParameter) {
		replaceInListIfExist(receiverParameter.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(receiverParameter));
		replaceElementIfExist(receiverParameter.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtReceiverParameterTypeReplaceListener(receiverParameter));
		replaceInListIfExist(receiverParameter.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(receiverParameter));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtCasePattern(spoon.reflect.code.CtCasePattern casePattern) {
		replaceInListIfExist(casePattern.getTypeCasts(), new spoon.support.visitor.replace.ReplacementVisitor.CtExpressionTypeCastsReplaceListener(casePattern));
		replaceInListIfExist(casePattern.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(casePattern));
		replaceElementIfExist(casePattern.getPattern(), new spoon.support.visitor.replace.ReplacementVisitor.CtCasePatternPatternReplaceListener(casePattern));
		replaceElementIfExist(casePattern.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(casePattern));
		replaceInListIfExist(casePattern.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(casePattern));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtRecordPattern(spoon.reflect.code.CtRecordPattern recordPattern) {
		replaceElementIfExist(recordPattern.getRecordType(), new spoon.support.visitor.replace.ReplacementVisitor.CtRecordPatternRecordTypeReplaceListener(recordPattern));
		replaceInListIfExist(recordPattern.getPatternList(), new spoon.support.visitor.replace.ReplacementVisitor.CtRecordPatternPatternListReplaceListener(recordPattern));
		replaceInListIfExist(recordPattern.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(recordPattern));
		replaceElementIfExist(recordPattern.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(recordPattern));
		replaceInListIfExist(recordPattern.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(recordPattern));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtUnnamedPattern(spoon.reflect.code.CtUnnamedPattern unnamedPattern) {
		replaceInListIfExist(unnamedPattern.getAnnotations(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementAnnotationsReplaceListener(unnamedPattern));
		replaceElementIfExist(unnamedPattern.getType(), new spoon.support.visitor.replace.ReplacementVisitor.CtTypedElementTypeReplaceListener(unnamedPattern));
		replaceInListIfExist(unnamedPattern.getComments(), new spoon.support.visitor.replace.ReplacementVisitor.CtElementCommentsReplaceListener(unnamedPattern));
	}
}
