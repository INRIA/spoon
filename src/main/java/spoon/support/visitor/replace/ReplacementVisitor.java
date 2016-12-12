/**
 * Copyright (C) 2006-2016 INRIA and contributors
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


package spoon.support.visitor.replace;


/**
 * Used to replace an element by another one.
 *
 * This class is generated automatically by the processor {@link spoon.generating.ReplacementVisitorGenerator}.
 */
public class ReplacementVisitor extends spoon.reflect.visitor.CtScanner {
	public static void replace(spoon.reflect.declaration.CtElement original, spoon.reflect.declaration.CtElement replace) {
		try {
			new ReplacementVisitor(original, replace).scan(original.getParent());
		} catch (spoon.SpoonException ignore) {
		}
	}

	private spoon.reflect.declaration.CtElement original;

	private spoon.reflect.declaration.CtElement replace;

	private ReplacementVisitor(spoon.reflect.declaration.CtElement original, spoon.reflect.declaration.CtElement replace) {
		this.original = original;
		this.replace = replace;
	}

	private <K, V extends spoon.reflect.declaration.CtElement> void replaceInMapIfExist(java.util.Map<K, V> mapProtected, spoon.generating.replace.ReplaceMapListener listener) {
		java.util.Map<K, V> map = new java.util.HashMap<>(mapProtected);
		V shouldBeDeleted = null;
		K key = null;
		for (java.util.Map.Entry<K, V> entry : map.entrySet()) {
			if ((entry.getValue()) == (original)) {
				shouldBeDeleted = entry.getValue();
				key = entry.getKey();
				break;
			}
		}
		if (shouldBeDeleted != null) {
			if ((replace) != null) {
				map.put(key, ((V) (replace)));
				replace.setParent(shouldBeDeleted.getParent());
			}else {
				map.remove(key);
			}
			listener.set(map);
		}
	}

	private <T extends spoon.reflect.declaration.CtElement> void replaceInSetIfExist(java.util.Set<T> setProtected, spoon.generating.replace.ReplaceSetListener listener) {
		java.util.Set<T> set = new java.util.HashSet<>(setProtected);
		T shouldBeDeleted = null;
		for (T element : set) {
			if (element == (original)) {
				shouldBeDeleted = element;
				break;
			}
		}
		if (shouldBeDeleted != null) {
			set.remove(shouldBeDeleted);
			if ((replace) != null) {
				set.add(((T) (replace)));
				replace.setParent(shouldBeDeleted.getParent());
			}
			listener.set(set);
		}
	}

	private <T extends spoon.reflect.declaration.CtElement> void replaceInListIfExist(java.util.List<T> listProtected, spoon.generating.replace.ReplaceListListener listener) {
		java.util.List<T> list = new java.util.ArrayList<>(listProtected);
		T shouldBeDeleted = null;
		int index = 0;
		for (int i = 0; i < (list.size()); i++) {
			if ((list.get(i)) == (original)) {
				index = i;
				shouldBeDeleted = list.get(i);
				break;
			}
		}
		if (shouldBeDeleted != null) {
			if ((replace) != null) {
				list.set(index, ((T) (replace)));
				replace.setParent(shouldBeDeleted.getParent());
			}else {
				list.remove(index);
			}
			listener.set(list);
		}
	}

	private void replaceElementIfExist(spoon.reflect.declaration.CtElement candidate, spoon.generating.replace.ReplaceListener listener) {
		if (candidate == (original)) {
			listener.set(replace);
			if ((replace) != null) {
				replace.setParent(candidate.getParent());
			}
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtTypedElementTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.declaration.CtTypedElement element;

		CtTypedElementTypeReplaceListener(spoon.reflect.declaration.CtTypedElement element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtElementCommentsReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.declaration.CtElement element;

		CtElementCommentsReplaceListener(spoon.reflect.declaration.CtElement element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setComments(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtAnnotationAnnotationTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.declaration.CtAnnotation element;

		CtAnnotationAnnotationTypeReplaceListener(spoon.reflect.declaration.CtAnnotation element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setAnnotationType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtElementAnnotationsReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.declaration.CtElement element;

		CtElementAnnotationsReplaceListener(spoon.reflect.declaration.CtElement element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setAnnotations(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtAnnotationValuesReplaceListener implements spoon.generating.replace.ReplaceMapListener<java.util.Map> {
		private final spoon.reflect.declaration.CtAnnotation element;

		CtAnnotationValuesReplaceListener(spoon.reflect.declaration.CtAnnotation element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Map replace) {
			this.element.setValues(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <A extends java.lang.annotation.Annotation> void visitCtAnnotation(final spoon.reflect.declaration.CtAnnotation<A> annotation) {
		replaceElementIfExist(annotation.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(annotation));
		replaceInListIfExist(annotation.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(annotation));
		replaceElementIfExist(annotation.getAnnotationType(), new ReplacementVisitor.CtAnnotationAnnotationTypeReplaceListener(annotation));
		replaceInListIfExist(annotation.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(annotation));
		replaceInMapIfExist(annotation.getValues(), new ReplacementVisitor.CtAnnotationValuesReplaceListener(annotation));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtTypeTypeMembersReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.declaration.CtType element;

		CtTypeTypeMembersReplaceListener(spoon.reflect.declaration.CtType element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setTypeMembers(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <A extends java.lang.annotation.Annotation> void visitCtAnnotationType(final spoon.reflect.declaration.CtAnnotationType<A> annotationType) {
		replaceInListIfExist(annotationType.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(annotationType));
		replaceInListIfExist(annotationType.getTypeMembers(), new ReplacementVisitor.CtTypeTypeMembersReplaceListener(annotationType));
		replaceInListIfExist(annotationType.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(annotationType));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtExecutableBodyReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtBlock> {
		private final spoon.reflect.code.CtBodyHolder element;

		CtExecutableBodyReplaceListener(spoon.reflect.code.CtBodyHolder element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtBlock replace) {
			this.element.setBody(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtAnonymousExecutable(final spoon.reflect.declaration.CtAnonymousExecutable anonymousExec) {
		replaceInListIfExist(anonymousExec.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(anonymousExec));
		replaceElementIfExist(anonymousExec.getBody(), new ReplacementVisitor.CtExecutableBodyReplaceListener(anonymousExec));
		replaceInListIfExist(anonymousExec.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(anonymousExec));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtExpressionTypeCastsReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtExpression element;

		CtExpressionTypeCastsReplaceListener(spoon.reflect.code.CtExpression element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setTypeCasts(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtTargetedExpressionTargetReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtTargetedExpression element;

		CtTargetedExpressionTargetReplaceListener(spoon.reflect.code.CtTargetedExpression element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setTarget(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtArrayAccessIndexExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtArrayAccess element;

		CtArrayAccessIndexExpressionReplaceListener(spoon.reflect.code.CtArrayAccess element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setIndexExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtArrayRead(final spoon.reflect.code.CtArrayRead<T> arrayRead) {
		replaceInListIfExist(arrayRead.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(arrayRead));
		replaceElementIfExist(arrayRead.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(arrayRead));
		replaceInListIfExist(arrayRead.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(arrayRead));
		replaceElementIfExist(arrayRead.getTarget(), new ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(arrayRead));
		replaceElementIfExist(arrayRead.getIndexExpression(), new ReplacementVisitor.CtArrayAccessIndexExpressionReplaceListener(arrayRead));
		replaceInListIfExist(arrayRead.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(arrayRead));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtArrayWrite(final spoon.reflect.code.CtArrayWrite<T> arrayWrite) {
		replaceInListIfExist(arrayWrite.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(arrayWrite));
		replaceElementIfExist(arrayWrite.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(arrayWrite));
		replaceInListIfExist(arrayWrite.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(arrayWrite));
		replaceElementIfExist(arrayWrite.getTarget(), new ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(arrayWrite));
		replaceElementIfExist(arrayWrite.getIndexExpression(), new ReplacementVisitor.CtArrayAccessIndexExpressionReplaceListener(arrayWrite));
		replaceInListIfExist(arrayWrite.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(arrayWrite));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtTypeReferencePackageReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtPackageReference> {
		private final spoon.reflect.reference.CtTypeReference element;

		CtTypeReferencePackageReplaceListener(spoon.reflect.reference.CtTypeReference element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtPackageReference replace) {
			this.element.setPackage(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtTypeReferenceDeclaringTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.reference.CtTypeReference element;

		CtTypeReferenceDeclaringTypeReplaceListener(spoon.reflect.reference.CtTypeReference element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setDeclaringType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtArrayTypeReferenceComponentTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.reference.CtArrayTypeReference element;

		CtArrayTypeReferenceComponentTypeReplaceListener(spoon.reflect.reference.CtArrayTypeReference element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setComponentType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtActualTypeContainerActualTypeArgumentsReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.reference.CtActualTypeContainer element;

		CtActualTypeContainerActualTypeArgumentsReplaceListener(spoon.reflect.reference.CtActualTypeContainer element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setActualTypeArguments(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtArrayTypeReference(final spoon.reflect.reference.CtArrayTypeReference<T> reference) {
		replaceInListIfExist(reference.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(reference));
		replaceElementIfExist(reference.getPackage(), new ReplacementVisitor.CtTypeReferencePackageReplaceListener(reference));
		replaceElementIfExist(reference.getDeclaringType(), new ReplacementVisitor.CtTypeReferenceDeclaringTypeReplaceListener(reference));
		replaceElementIfExist(reference.getComponentType(), new ReplacementVisitor.CtArrayTypeReferenceComponentTypeReplaceListener(reference));
		replaceInListIfExist(reference.getActualTypeArguments(), new ReplacementVisitor.CtActualTypeContainerActualTypeArgumentsReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtAssertAssertExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtAssert element;

		CtAssertAssertExpressionReplaceListener(spoon.reflect.code.CtAssert element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setAssertExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtAssertExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtAssert element;

		CtAssertExpressionReplaceListener(spoon.reflect.code.CtAssert element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtAssert(final spoon.reflect.code.CtAssert<T> asserted) {
		replaceInListIfExist(asserted.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(asserted));
		replaceElementIfExist(asserted.getAssertExpression(), new ReplacementVisitor.CtAssertAssertExpressionReplaceListener(asserted));
		replaceElementIfExist(asserted.getExpression(), new ReplacementVisitor.CtAssertExpressionReplaceListener(asserted));
		replaceInListIfExist(asserted.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(asserted));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtAssignmentAssignedReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtAssignment element;

		CtAssignmentAssignedReplaceListener(spoon.reflect.code.CtAssignment element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setAssigned(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtRHSReceiverAssignmentReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtRHSReceiver element;

		CtRHSReceiverAssignmentReplaceListener(spoon.reflect.code.CtRHSReceiver element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setAssignment(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T, A extends T> void visitCtAssignment(final spoon.reflect.code.CtAssignment<T, A> assignement) {
		replaceInListIfExist(assignement.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(assignement));
		replaceElementIfExist(assignement.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(assignement));
		replaceInListIfExist(assignement.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(assignement));
		replaceElementIfExist(assignement.getAssigned(), new ReplacementVisitor.CtAssignmentAssignedReplaceListener(assignement));
		replaceElementIfExist(assignement.getAssignment(), new ReplacementVisitor.CtRHSReceiverAssignmentReplaceListener(assignement));
		replaceInListIfExist(assignement.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(assignement));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtBinaryOperatorLeftHandOperandReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtBinaryOperator element;

		CtBinaryOperatorLeftHandOperandReplaceListener(spoon.reflect.code.CtBinaryOperator element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setLeftHandOperand(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtBinaryOperatorRightHandOperandReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtBinaryOperator element;

		CtBinaryOperatorRightHandOperandReplaceListener(spoon.reflect.code.CtBinaryOperator element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setRightHandOperand(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtBinaryOperator(final spoon.reflect.code.CtBinaryOperator<T> operator) {
		replaceInListIfExist(operator.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(operator));
		replaceElementIfExist(operator.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(operator));
		replaceInListIfExist(operator.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(operator));
		replaceElementIfExist(operator.getLeftHandOperand(), new ReplacementVisitor.CtBinaryOperatorLeftHandOperandReplaceListener(operator));
		replaceElementIfExist(operator.getRightHandOperand(), new ReplacementVisitor.CtBinaryOperatorRightHandOperandReplaceListener(operator));
		replaceInListIfExist(operator.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(operator));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtStatementListStatementsReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtStatementList element;

		CtStatementListStatementsReplaceListener(spoon.reflect.code.CtStatementList element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setStatements(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <R> void visitCtBlock(final spoon.reflect.code.CtBlock<R> block) {
		replaceInListIfExist(block.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(block));
		replaceInListIfExist(block.getStatements(), new ReplacementVisitor.CtStatementListStatementsReplaceListener(block));
		replaceInListIfExist(block.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(block));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtBreak(final spoon.reflect.code.CtBreak breakStatement) {
		replaceInListIfExist(breakStatement.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(breakStatement));
		replaceInListIfExist(breakStatement.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(breakStatement));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtCaseCaseExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtCase element;

		CtCaseCaseExpressionReplaceListener(spoon.reflect.code.CtCase element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setCaseExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <S> void visitCtCase(final spoon.reflect.code.CtCase<S> caseStatement) {
		replaceInListIfExist(caseStatement.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(caseStatement));
		replaceElementIfExist(caseStatement.getCaseExpression(), new ReplacementVisitor.CtCaseCaseExpressionReplaceListener(caseStatement));
		replaceInListIfExist(caseStatement.getStatements(), new ReplacementVisitor.CtStatementListStatementsReplaceListener(caseStatement));
		replaceInListIfExist(caseStatement.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(caseStatement));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtCatchParameterReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtCatchVariable> {
		private final spoon.reflect.code.CtCatch element;

		CtCatchParameterReplaceListener(spoon.reflect.code.CtCatch element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtCatchVariable replace) {
			this.element.setParameter(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtCatchBodyReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtBlock> {
		private final spoon.reflect.code.CtBodyHolder element;

		CtCatchBodyReplaceListener(spoon.reflect.code.CtBodyHolder element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtBlock replace) {
			this.element.setBody(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtCatch(final spoon.reflect.code.CtCatch catchBlock) {
		replaceInListIfExist(catchBlock.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(catchBlock));
		replaceElementIfExist(catchBlock.getParameter(), new ReplacementVisitor.CtCatchParameterReplaceListener(catchBlock));
		replaceElementIfExist(catchBlock.getBody(), new ReplacementVisitor.CtCatchBodyReplaceListener(catchBlock));
		replaceInListIfExist(catchBlock.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(catchBlock));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtTypeInformationSuperclassReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.declaration.CtType element;

		CtTypeInformationSuperclassReplaceListener(spoon.reflect.declaration.CtType element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setSuperclass(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtTypeInformationSuperInterfacesReplaceListener implements spoon.generating.replace.ReplaceSetListener<java.util.Set> {
		private final spoon.reflect.declaration.CtType element;

		CtTypeInformationSuperInterfacesReplaceListener(spoon.reflect.declaration.CtType element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Set replace) {
			this.element.setSuperInterfaces(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtFormalTypeDeclarerFormalCtTypeParametersReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.declaration.CtFormalTypeDeclarer element;

		CtFormalTypeDeclarerFormalCtTypeParametersReplaceListener(spoon.reflect.declaration.CtFormalTypeDeclarer element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setFormalCtTypeParameters(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtClass(final spoon.reflect.declaration.CtClass<T> ctClass) {
		replaceInListIfExist(ctClass.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(ctClass));
		replaceElementIfExist(ctClass.getSuperclass(), new ReplacementVisitor.CtTypeInformationSuperclassReplaceListener(ctClass));
		replaceInSetIfExist(ctClass.getSuperInterfaces(), new ReplacementVisitor.CtTypeInformationSuperInterfacesReplaceListener(ctClass));
		replaceInListIfExist(ctClass.getFormalCtTypeParameters(), new ReplacementVisitor.CtFormalTypeDeclarerFormalCtTypeParametersReplaceListener(ctClass));
		replaceInListIfExist(ctClass.getTypeMembers(), new ReplacementVisitor.CtTypeTypeMembersReplaceListener(ctClass));
		replaceInListIfExist(ctClass.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(ctClass));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtTypeParameter(spoon.reflect.declaration.CtTypeParameter typeParameter) {
		replaceInListIfExist(typeParameter.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(typeParameter));
		replaceElementIfExist(typeParameter.getSuperclass(), new ReplacementVisitor.CtTypeInformationSuperclassReplaceListener(typeParameter));
		replaceInListIfExist(typeParameter.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(typeParameter));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtConditionalConditionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtConditional element;

		CtConditionalConditionReplaceListener(spoon.reflect.code.CtConditional element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setCondition(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtConditionalThenExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtConditional element;

		CtConditionalThenExpressionReplaceListener(spoon.reflect.code.CtConditional element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setThenExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtConditionalElseExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtConditional element;

		CtConditionalElseExpressionReplaceListener(spoon.reflect.code.CtConditional element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setElseExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtConditional(final spoon.reflect.code.CtConditional<T> conditional) {
		replaceElementIfExist(conditional.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(conditional));
		replaceInListIfExist(conditional.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(conditional));
		replaceElementIfExist(conditional.getCondition(), new ReplacementVisitor.CtConditionalConditionReplaceListener(conditional));
		replaceElementIfExist(conditional.getThenExpression(), new ReplacementVisitor.CtConditionalThenExpressionReplaceListener(conditional));
		replaceElementIfExist(conditional.getElseExpression(), new ReplacementVisitor.CtConditionalElseExpressionReplaceListener(conditional));
		replaceInListIfExist(conditional.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(conditional));
		replaceInListIfExist(conditional.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(conditional));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtExecutableParametersReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.declaration.CtExecutable element;

		CtExecutableParametersReplaceListener(spoon.reflect.declaration.CtExecutable element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setParameters(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtExecutableThrownTypesReplaceListener implements spoon.generating.replace.ReplaceSetListener<java.util.Set> {
		private final spoon.reflect.declaration.CtExecutable element;

		CtExecutableThrownTypesReplaceListener(spoon.reflect.declaration.CtExecutable element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Set replace) {
			this.element.setThrownTypes(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtConstructor(final spoon.reflect.declaration.CtConstructor<T> c) {
		replaceInListIfExist(c.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(c));
		replaceInListIfExist(c.getParameters(), new ReplacementVisitor.CtExecutableParametersReplaceListener(c));
		replaceInSetIfExist(c.getThrownTypes(), new ReplacementVisitor.CtExecutableThrownTypesReplaceListener(c));
		replaceInListIfExist(c.getFormalCtTypeParameters(), new ReplacementVisitor.CtFormalTypeDeclarerFormalCtTypeParametersReplaceListener(c));
		replaceElementIfExist(c.getBody(), new ReplacementVisitor.CtExecutableBodyReplaceListener(c));
		replaceInListIfExist(c.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(c));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtContinueLabelledStatementReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtStatement> {
		private final spoon.reflect.code.CtContinue element;

		CtContinueLabelledStatementReplaceListener(spoon.reflect.code.CtContinue element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtStatement replace) {
			this.element.setLabelledStatement(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtContinue(final spoon.reflect.code.CtContinue continueStatement) {
		replaceInListIfExist(continueStatement.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(continueStatement));
		replaceElementIfExist(continueStatement.getLabelledStatement(), new ReplacementVisitor.CtContinueLabelledStatementReplaceListener(continueStatement));
		replaceInListIfExist(continueStatement.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(continueStatement));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtDoLoopingExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtDo element;

		CtDoLoopingExpressionReplaceListener(spoon.reflect.code.CtDo element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setLoopingExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtLoopBodyReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtStatement> {
		private final spoon.reflect.code.CtBodyHolder element;

		CtLoopBodyReplaceListener(spoon.reflect.code.CtBodyHolder element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtStatement replace) {
			this.element.setBody(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtDo(final spoon.reflect.code.CtDo doLoop) {
		replaceInListIfExist(doLoop.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(doLoop));
		replaceElementIfExist(doLoop.getLoopingExpression(), new ReplacementVisitor.CtDoLoopingExpressionReplaceListener(doLoop));
		replaceElementIfExist(doLoop.getBody(), new ReplacementVisitor.CtLoopBodyReplaceListener(doLoop));
		replaceInListIfExist(doLoop.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(doLoop));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtEnumEnumValuesReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.declaration.CtEnum element;

		CtEnumEnumValuesReplaceListener(spoon.reflect.declaration.CtEnum element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setEnumValues(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T extends java.lang.Enum<?>> void visitCtEnum(final spoon.reflect.declaration.CtEnum<T> ctEnum) {
		replaceInListIfExist(ctEnum.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(ctEnum));
		replaceInSetIfExist(ctEnum.getSuperInterfaces(), new ReplacementVisitor.CtTypeInformationSuperInterfacesReplaceListener(ctEnum));
		replaceInListIfExist(ctEnum.getTypeMembers(), new ReplacementVisitor.CtTypeTypeMembersReplaceListener(ctEnum));
		replaceInListIfExist(ctEnum.getEnumValues(), new ReplacementVisitor.CtEnumEnumValuesReplaceListener(ctEnum));
		replaceInListIfExist(ctEnum.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(ctEnum));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtExecutableReferenceDeclaringTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.reference.CtExecutableReference element;

		CtExecutableReferenceDeclaringTypeReplaceListener(spoon.reflect.reference.CtExecutableReference element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setDeclaringType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtExecutableReferenceTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.reference.CtExecutableReference element;

		CtExecutableReferenceTypeReplaceListener(spoon.reflect.reference.CtExecutableReference element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtExecutableReferenceParametersReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.reference.CtExecutableReference element;

		CtExecutableReferenceParametersReplaceListener(spoon.reflect.reference.CtExecutableReference element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setParameters(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtExecutableReference(final spoon.reflect.reference.CtExecutableReference<T> reference) {
		replaceElementIfExist(reference.getDeclaringType(), new ReplacementVisitor.CtExecutableReferenceDeclaringTypeReplaceListener(reference));
		replaceElementIfExist(reference.getType(), new ReplacementVisitor.CtExecutableReferenceTypeReplaceListener(reference));
		replaceInListIfExist(reference.getParameters(), new ReplacementVisitor.CtExecutableReferenceParametersReplaceListener(reference));
		replaceInListIfExist(reference.getActualTypeArguments(), new ReplacementVisitor.CtActualTypeContainerActualTypeArgumentsReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
		replaceInListIfExist(reference.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(reference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtVariableDefaultExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.declaration.CtVariable element;

		CtVariableDefaultExpressionReplaceListener(spoon.reflect.declaration.CtVariable element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setDefaultExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtField(final spoon.reflect.declaration.CtField<T> f) {
		replaceInListIfExist(f.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(f));
		replaceElementIfExist(f.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(f));
		replaceElementIfExist(f.getDefaultExpression(), new ReplacementVisitor.CtVariableDefaultExpressionReplaceListener(f));
		replaceInListIfExist(f.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(f));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtEnumValue(final spoon.reflect.declaration.CtEnumValue<T> enumValue) {
		replaceInListIfExist(enumValue.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(enumValue));
		replaceElementIfExist(enumValue.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(enumValue));
		replaceElementIfExist(enumValue.getDefaultExpression(), new ReplacementVisitor.CtVariableDefaultExpressionReplaceListener(enumValue));
		replaceInListIfExist(enumValue.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(enumValue));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtThisAccess(final spoon.reflect.code.CtThisAccess<T> thisAccess) {
		replaceInListIfExist(thisAccess.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(thisAccess));
		replaceInListIfExist(thisAccess.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(thisAccess));
		replaceElementIfExist(thisAccess.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(thisAccess));
		replaceInListIfExist(thisAccess.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(thisAccess));
		replaceElementIfExist(thisAccess.getTarget(), new ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(thisAccess));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtVariableAccessTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.declaration.CtTypedElement element;

		CtVariableAccessTypeReplaceListener(spoon.reflect.declaration.CtTypedElement element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtAnnotationFieldAccessVariableReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtFieldReference> {
		private final spoon.reflect.code.CtVariableAccess element;

		CtAnnotationFieldAccessVariableReplaceListener(spoon.reflect.code.CtVariableAccess element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtFieldReference replace) {
			this.element.setVariable(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtAnnotationFieldAccess(final spoon.reflect.code.CtAnnotationFieldAccess<T> annotationFieldAccess) {
		replaceInListIfExist(annotationFieldAccess.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(annotationFieldAccess));
		replaceInListIfExist(annotationFieldAccess.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(annotationFieldAccess));
		replaceInListIfExist(annotationFieldAccess.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(annotationFieldAccess));
		replaceElementIfExist(annotationFieldAccess.getTarget(), new ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(annotationFieldAccess));
		replaceElementIfExist(annotationFieldAccess.getType(), new ReplacementVisitor.CtVariableAccessTypeReplaceListener(annotationFieldAccess));
		replaceElementIfExist(annotationFieldAccess.getVariable(), new ReplacementVisitor.CtAnnotationFieldAccessVariableReplaceListener(annotationFieldAccess));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtFieldReferenceDeclaringTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.reference.CtFieldReference element;

		CtFieldReferenceDeclaringTypeReplaceListener(spoon.reflect.reference.CtFieldReference element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setDeclaringType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtVariableReferenceTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.reference.CtVariableReference element;

		CtVariableReferenceTypeReplaceListener(spoon.reflect.reference.CtVariableReference element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtFieldReference(final spoon.reflect.reference.CtFieldReference<T> reference) {
		replaceElementIfExist(reference.getDeclaringType(), new ReplacementVisitor.CtFieldReferenceDeclaringTypeReplaceListener(reference));
		replaceElementIfExist(reference.getType(), new ReplacementVisitor.CtVariableReferenceTypeReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtForForInitReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtFor element;

		CtForForInitReplaceListener(spoon.reflect.code.CtFor element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setForInit(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtForExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtFor element;

		CtForExpressionReplaceListener(spoon.reflect.code.CtFor element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtForForUpdateReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtFor element;

		CtForForUpdateReplaceListener(spoon.reflect.code.CtFor element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setForUpdate(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtFor(final spoon.reflect.code.CtFor forLoop) {
		replaceInListIfExist(forLoop.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(forLoop));
		replaceInListIfExist(forLoop.getForInit(), new ReplacementVisitor.CtForForInitReplaceListener(forLoop));
		replaceElementIfExist(forLoop.getExpression(), new ReplacementVisitor.CtForExpressionReplaceListener(forLoop));
		replaceInListIfExist(forLoop.getForUpdate(), new ReplacementVisitor.CtForForUpdateReplaceListener(forLoop));
		replaceElementIfExist(forLoop.getBody(), new ReplacementVisitor.CtLoopBodyReplaceListener(forLoop));
		replaceInListIfExist(forLoop.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(forLoop));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtForEachVariableReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtLocalVariable> {
		private final spoon.reflect.code.CtForEach element;

		CtForEachVariableReplaceListener(spoon.reflect.code.CtForEach element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtLocalVariable replace) {
			this.element.setVariable(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtForEachExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtForEach element;

		CtForEachExpressionReplaceListener(spoon.reflect.code.CtForEach element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtForEach(final spoon.reflect.code.CtForEach foreach) {
		replaceInListIfExist(foreach.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(foreach));
		replaceElementIfExist(foreach.getVariable(), new ReplacementVisitor.CtForEachVariableReplaceListener(foreach));
		replaceElementIfExist(foreach.getExpression(), new ReplacementVisitor.CtForEachExpressionReplaceListener(foreach));
		replaceElementIfExist(foreach.getBody(), new ReplacementVisitor.CtLoopBodyReplaceListener(foreach));
		replaceInListIfExist(foreach.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(foreach));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtIfConditionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtIf element;

		CtIfConditionReplaceListener(spoon.reflect.code.CtIf element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setCondition(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtIfThenStatementReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtStatement> {
		private final spoon.reflect.code.CtIf element;

		CtIfThenStatementReplaceListener(spoon.reflect.code.CtIf element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtStatement replace) {
			this.element.setThenStatement(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtIfElseStatementReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtStatement> {
		private final spoon.reflect.code.CtIf element;

		CtIfElseStatementReplaceListener(spoon.reflect.code.CtIf element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtStatement replace) {
			this.element.setElseStatement(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtIf(final spoon.reflect.code.CtIf ifElement) {
		replaceInListIfExist(ifElement.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(ifElement));
		replaceElementIfExist(ifElement.getCondition(), new ReplacementVisitor.CtIfConditionReplaceListener(ifElement));
		replaceElementIfExist(((spoon.reflect.code.CtStatement) (ifElement.getThenStatement())), new ReplacementVisitor.CtIfThenStatementReplaceListener(ifElement));
		replaceElementIfExist(((spoon.reflect.code.CtStatement) (ifElement.getElseStatement())), new ReplacementVisitor.CtIfElseStatementReplaceListener(ifElement));
		replaceInListIfExist(ifElement.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(ifElement));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtInterface(final spoon.reflect.declaration.CtInterface<T> intrface) {
		replaceInListIfExist(intrface.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(intrface));
		replaceInSetIfExist(intrface.getSuperInterfaces(), new ReplacementVisitor.CtTypeInformationSuperInterfacesReplaceListener(intrface));
		replaceInListIfExist(intrface.getFormalCtTypeParameters(), new ReplacementVisitor.CtFormalTypeDeclarerFormalCtTypeParametersReplaceListener(intrface));
		replaceInListIfExist(intrface.getTypeMembers(), new ReplacementVisitor.CtTypeTypeMembersReplaceListener(intrface));
		replaceInListIfExist(intrface.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(intrface));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtAbstractInvocationExecutableReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtExecutableReference> {
		private final spoon.reflect.code.CtAbstractInvocation element;

		CtAbstractInvocationExecutableReplaceListener(spoon.reflect.code.CtAbstractInvocation element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtExecutableReference replace) {
			this.element.setExecutable(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtAbstractInvocationArgumentsReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtAbstractInvocation element;

		CtAbstractInvocationArgumentsReplaceListener(spoon.reflect.code.CtAbstractInvocation element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setArguments(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtInvocation(final spoon.reflect.code.CtInvocation<T> invocation) {
		replaceInListIfExist(invocation.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(invocation));
		replaceInListIfExist(invocation.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(invocation));
		replaceElementIfExist(invocation.getTarget(), new ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(invocation));
		replaceElementIfExist(invocation.getExecutable(), new ReplacementVisitor.CtAbstractInvocationExecutableReplaceListener(invocation));
		replaceInListIfExist(invocation.getArguments(), new ReplacementVisitor.CtAbstractInvocationArgumentsReplaceListener(invocation));
		replaceInListIfExist(invocation.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(invocation));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtLiteral(final spoon.reflect.code.CtLiteral<T> literal) {
		replaceInListIfExist(literal.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(literal));
		replaceElementIfExist(literal.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(literal));
		replaceInListIfExist(literal.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(literal));
		replaceInListIfExist(literal.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(literal));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtLocalVariable(final spoon.reflect.code.CtLocalVariable<T> localVariable) {
		replaceInListIfExist(localVariable.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(localVariable));
		replaceElementIfExist(localVariable.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(localVariable));
		replaceElementIfExist(localVariable.getDefaultExpression(), new ReplacementVisitor.CtVariableDefaultExpressionReplaceListener(localVariable));
		replaceInListIfExist(localVariable.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(localVariable));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtLocalVariableReference(final spoon.reflect.reference.CtLocalVariableReference<T> reference) {
		replaceElementIfExist(reference.getType(), new ReplacementVisitor.CtVariableReferenceTypeReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtMultiTypedElementMultiTypesReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.declaration.CtMultiTypedElement element;

		CtMultiTypedElementMultiTypesReplaceListener(spoon.reflect.declaration.CtMultiTypedElement element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setMultiTypes(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtCatchVariable(final spoon.reflect.code.CtCatchVariable<T> catchVariable) {
		replaceInListIfExist(catchVariable.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(catchVariable));
		replaceInListIfExist(catchVariable.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(catchVariable));
		replaceElementIfExist(catchVariable.getDefaultExpression(), new ReplacementVisitor.CtVariableDefaultExpressionReplaceListener(catchVariable));
		replaceElementIfExist(catchVariable.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(catchVariable));
		replaceInListIfExist(catchVariable.getMultiTypes(), new ReplacementVisitor.CtMultiTypedElementMultiTypesReplaceListener(catchVariable));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtCatchVariableReference(final spoon.reflect.reference.CtCatchVariableReference<T> reference) {
		replaceInListIfExist(reference.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(reference));
		replaceElementIfExist(reference.getType(), new ReplacementVisitor.CtVariableReferenceTypeReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtMethod(final spoon.reflect.declaration.CtMethod<T> m) {
		replaceInListIfExist(m.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(m));
		replaceInListIfExist(m.getFormalCtTypeParameters(), new ReplacementVisitor.CtFormalTypeDeclarerFormalCtTypeParametersReplaceListener(m));
		replaceElementIfExist(m.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(m));
		replaceInListIfExist(m.getParameters(), new ReplacementVisitor.CtExecutableParametersReplaceListener(m));
		replaceInSetIfExist(m.getThrownTypes(), new ReplacementVisitor.CtExecutableThrownTypesReplaceListener(m));
		replaceElementIfExist(m.getBody(), new ReplacementVisitor.CtExecutableBodyReplaceListener(m));
		replaceInListIfExist(m.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(m));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtAnnotationMethodDefaultExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.declaration.CtAnnotationMethod element;

		CtAnnotationMethodDefaultExpressionReplaceListener(spoon.reflect.declaration.CtAnnotationMethod element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setDefaultExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtAnnotationMethod(spoon.reflect.declaration.CtAnnotationMethod<T> annotationMethod) {
		replaceInListIfExist(annotationMethod.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(annotationMethod));
		replaceElementIfExist(annotationMethod.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(annotationMethod));
		replaceElementIfExist(annotationMethod.getDefaultExpression(), new ReplacementVisitor.CtAnnotationMethodDefaultExpressionReplaceListener(annotationMethod));
		replaceInListIfExist(annotationMethod.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(annotationMethod));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtNewArrayElementsReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtNewArray element;

		CtNewArrayElementsReplaceListener(spoon.reflect.code.CtNewArray element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setElements(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtNewArrayDimensionExpressionsReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtNewArray element;

		CtNewArrayDimensionExpressionsReplaceListener(spoon.reflect.code.CtNewArray element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setDimensionExpressions(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtNewArray(final spoon.reflect.code.CtNewArray<T> newArray) {
		replaceInListIfExist(newArray.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(newArray));
		replaceElementIfExist(newArray.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(newArray));
		replaceInListIfExist(newArray.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(newArray));
		replaceInListIfExist(newArray.getElements(), new ReplacementVisitor.CtNewArrayElementsReplaceListener(newArray));
		replaceInListIfExist(newArray.getDimensionExpressions(), new ReplacementVisitor.CtNewArrayDimensionExpressionsReplaceListener(newArray));
		replaceInListIfExist(newArray.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(newArray));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtConstructorCall(final spoon.reflect.code.CtConstructorCall<T> ctConstructorCall) {
		replaceInListIfExist(ctConstructorCall.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(ctConstructorCall));
		replaceInListIfExist(ctConstructorCall.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(ctConstructorCall));
		replaceElementIfExist(ctConstructorCall.getExecutable(), new ReplacementVisitor.CtAbstractInvocationExecutableReplaceListener(ctConstructorCall));
		replaceElementIfExist(ctConstructorCall.getTarget(), new ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(ctConstructorCall));
		replaceInListIfExist(ctConstructorCall.getArguments(), new ReplacementVisitor.CtAbstractInvocationArgumentsReplaceListener(ctConstructorCall));
		replaceInListIfExist(ctConstructorCall.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(ctConstructorCall));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtNewClassAnonymousClassReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.declaration.CtClass> {
		private final spoon.reflect.code.CtNewClass element;

		CtNewClassAnonymousClassReplaceListener(spoon.reflect.code.CtNewClass element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.declaration.CtClass replace) {
			this.element.setAnonymousClass(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtNewClass(final spoon.reflect.code.CtNewClass<T> newClass) {
		replaceInListIfExist(newClass.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(newClass));
		replaceInListIfExist(newClass.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(newClass));
		replaceElementIfExist(newClass.getExecutable(), new ReplacementVisitor.CtAbstractInvocationExecutableReplaceListener(newClass));
		replaceElementIfExist(newClass.getTarget(), new ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(newClass));
		replaceInListIfExist(newClass.getArguments(), new ReplacementVisitor.CtAbstractInvocationArgumentsReplaceListener(newClass));
		replaceElementIfExist(newClass.getAnonymousClass(), new ReplacementVisitor.CtNewClassAnonymousClassReplaceListener(newClass));
		replaceInListIfExist(newClass.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(newClass));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtLambdaExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtLambda element;

		CtLambdaExpressionReplaceListener(spoon.reflect.code.CtLambda element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtLambda(final spoon.reflect.code.CtLambda<T> lambda) {
		replaceInListIfExist(lambda.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(lambda));
		replaceElementIfExist(lambda.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(lambda));
		replaceInListIfExist(lambda.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(lambda));
		replaceInListIfExist(lambda.getParameters(), new ReplacementVisitor.CtExecutableParametersReplaceListener(lambda));
		replaceInSetIfExist(lambda.getThrownTypes(), new ReplacementVisitor.CtExecutableThrownTypesReplaceListener(lambda));
		replaceElementIfExist(lambda.getBody(), new ReplacementVisitor.CtExecutableBodyReplaceListener(lambda));
		replaceElementIfExist(lambda.getExpression(), new ReplacementVisitor.CtLambdaExpressionReplaceListener(lambda));
		replaceInListIfExist(lambda.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(lambda));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtExecutableReferenceExpressionExecutableReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtExecutableReference> {
		private final spoon.reflect.code.CtExecutableReferenceExpression element;

		CtExecutableReferenceExpressionExecutableReplaceListener(spoon.reflect.code.CtExecutableReferenceExpression element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtExecutableReference replace) {
			this.element.setExecutable(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T, E extends spoon.reflect.code.CtExpression<?>> void visitCtExecutableReferenceExpression(final spoon.reflect.code.CtExecutableReferenceExpression<T, E> expression) {
		replaceInListIfExist(expression.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(expression));
		replaceInListIfExist(expression.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(expression));
		replaceElementIfExist(expression.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(expression));
		replaceInListIfExist(expression.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(expression));
		replaceElementIfExist(expression.getExecutable(), new ReplacementVisitor.CtExecutableReferenceExpressionExecutableReplaceListener(expression));
		replaceElementIfExist(expression.getTarget(), new ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(expression));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T, A extends T> void visitCtOperatorAssignment(final spoon.reflect.code.CtOperatorAssignment<T, A> assignment) {
		replaceInListIfExist(assignment.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(assignment));
		replaceElementIfExist(assignment.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(assignment));
		replaceInListIfExist(assignment.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(assignment));
		replaceElementIfExist(assignment.getAssigned(), new ReplacementVisitor.CtAssignmentAssignedReplaceListener(assignment));
		replaceElementIfExist(assignment.getAssignment(), new ReplacementVisitor.CtRHSReceiverAssignmentReplaceListener(assignment));
		replaceInListIfExist(assignment.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(assignment));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtPackagePackagesReplaceListener implements spoon.generating.replace.ReplaceSetListener<java.util.Set> {
		private final spoon.reflect.declaration.CtPackage element;

		CtPackagePackagesReplaceListener(spoon.reflect.declaration.CtPackage element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Set replace) {
			this.element.setPackages(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtPackageTypesReplaceListener implements spoon.generating.replace.ReplaceSetListener<java.util.Set> {
		private final spoon.reflect.declaration.CtPackage element;

		CtPackageTypesReplaceListener(spoon.reflect.declaration.CtPackage element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.Set replace) {
			this.element.setTypes(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtPackage(final spoon.reflect.declaration.CtPackage ctPackage) {
		replaceInListIfExist(ctPackage.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(ctPackage));
		replaceInSetIfExist(ctPackage.getPackages(), new ReplacementVisitor.CtPackagePackagesReplaceListener(ctPackage));
		replaceInSetIfExist(ctPackage.getTypes(), new ReplacementVisitor.CtPackageTypesReplaceListener(ctPackage));
		replaceInListIfExist(ctPackage.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(ctPackage));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtPackageReference(final spoon.reflect.reference.CtPackageReference reference) {
		replaceInListIfExist(reference.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtParameter(final spoon.reflect.declaration.CtParameter<T> parameter) {
		replaceInListIfExist(parameter.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(parameter));
		replaceElementIfExist(parameter.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(parameter));
		replaceInListIfExist(parameter.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(parameter));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtParameterReferenceDeclaringExecutableReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtExecutableReference> {
		private final spoon.reflect.reference.CtParameterReference element;

		CtParameterReferenceDeclaringExecutableReplaceListener(spoon.reflect.reference.CtParameterReference element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtExecutableReference replace) {
			this.element.setDeclaringExecutable(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtParameterReference(final spoon.reflect.reference.CtParameterReference<T> reference) {
		replaceElementIfExist(reference.getType(), new ReplacementVisitor.CtVariableReferenceTypeReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
		replaceElementIfExist(reference.getDeclaringExecutable(), new ReplacementVisitor.CtParameterReferenceDeclaringExecutableReplaceListener(reference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtReturnReturnedExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtReturn element;

		CtReturnReturnedExpressionReplaceListener(spoon.reflect.code.CtReturn element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setReturnedExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <R> void visitCtReturn(final spoon.reflect.code.CtReturn<R> returnStatement) {
		replaceInListIfExist(returnStatement.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(returnStatement));
		replaceElementIfExist(returnStatement.getReturnedExpression(), new ReplacementVisitor.CtReturnReturnedExpressionReplaceListener(returnStatement));
		replaceInListIfExist(returnStatement.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(returnStatement));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <R> void visitCtStatementList(final spoon.reflect.code.CtStatementList statements) {
		replaceInListIfExist(statements.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(statements));
		replaceInListIfExist(statements.getStatements(), new ReplacementVisitor.CtStatementListStatementsReplaceListener(statements));
		replaceInListIfExist(statements.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(statements));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtSwitchSelectorReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtSwitch element;

		CtSwitchSelectorReplaceListener(spoon.reflect.code.CtSwitch element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setSelector(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtSwitchCasesReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtSwitch element;

		CtSwitchCasesReplaceListener(spoon.reflect.code.CtSwitch element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setCases(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <S> void visitCtSwitch(final spoon.reflect.code.CtSwitch<S> switchStatement) {
		replaceInListIfExist(switchStatement.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(switchStatement));
		replaceElementIfExist(switchStatement.getSelector(), new ReplacementVisitor.CtSwitchSelectorReplaceListener(switchStatement));
		replaceInListIfExist(switchStatement.getCases(), new ReplacementVisitor.CtSwitchCasesReplaceListener(switchStatement));
		replaceInListIfExist(switchStatement.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(switchStatement));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtSynchronizedExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtSynchronized element;

		CtSynchronizedExpressionReplaceListener(spoon.reflect.code.CtSynchronized element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtSynchronizedBlockReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtBlock> {
		private final spoon.reflect.code.CtSynchronized element;

		CtSynchronizedBlockReplaceListener(spoon.reflect.code.CtSynchronized element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtBlock replace) {
			this.element.setBlock(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtSynchronized(final spoon.reflect.code.CtSynchronized synchro) {
		replaceInListIfExist(synchro.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(synchro));
		replaceElementIfExist(synchro.getExpression(), new ReplacementVisitor.CtSynchronizedExpressionReplaceListener(synchro));
		replaceElementIfExist(synchro.getBlock(), new ReplacementVisitor.CtSynchronizedBlockReplaceListener(synchro));
		replaceInListIfExist(synchro.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(synchro));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtThrowThrownExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtThrow element;

		CtThrowThrownExpressionReplaceListener(spoon.reflect.code.CtThrow element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setThrownExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtThrow(final spoon.reflect.code.CtThrow throwStatement) {
		replaceInListIfExist(throwStatement.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(throwStatement));
		replaceElementIfExist(throwStatement.getThrownExpression(), new ReplacementVisitor.CtThrowThrownExpressionReplaceListener(throwStatement));
		replaceInListIfExist(throwStatement.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(throwStatement));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtTryBodyReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtBlock> {
		private final spoon.reflect.code.CtBodyHolder element;

		CtTryBodyReplaceListener(spoon.reflect.code.CtBodyHolder element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtBlock replace) {
			this.element.setBody(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtTryCatchersReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtTry element;

		CtTryCatchersReplaceListener(spoon.reflect.code.CtTry element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setCatchers(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtTryFinalizerReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtBlock> {
		private final spoon.reflect.code.CtTry element;

		CtTryFinalizerReplaceListener(spoon.reflect.code.CtTry element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtBlock replace) {
			this.element.setFinalizer(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtTry(final spoon.reflect.code.CtTry tryBlock) {
		replaceInListIfExist(tryBlock.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(tryBlock));
		replaceElementIfExist(tryBlock.getBody(), new ReplacementVisitor.CtTryBodyReplaceListener(tryBlock));
		replaceInListIfExist(tryBlock.getCatchers(), new ReplacementVisitor.CtTryCatchersReplaceListener(tryBlock));
		replaceElementIfExist(tryBlock.getFinalizer(), new ReplacementVisitor.CtTryFinalizerReplaceListener(tryBlock));
		replaceInListIfExist(tryBlock.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(tryBlock));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtTryWithResourceResourcesReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.code.CtTryWithResource element;

		CtTryWithResourceResourcesReplaceListener(spoon.reflect.code.CtTryWithResource element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setResources(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtTryWithResource(final spoon.reflect.code.CtTryWithResource tryWithResource) {
		replaceInListIfExist(tryWithResource.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(tryWithResource));
		replaceInListIfExist(tryWithResource.getResources(), new ReplacementVisitor.CtTryWithResourceResourcesReplaceListener(tryWithResource));
		replaceElementIfExist(tryWithResource.getBody(), new ReplacementVisitor.CtTryBodyReplaceListener(tryWithResource));
		replaceInListIfExist(tryWithResource.getCatchers(), new ReplacementVisitor.CtTryCatchersReplaceListener(tryWithResource));
		replaceElementIfExist(tryWithResource.getFinalizer(), new ReplacementVisitor.CtTryFinalizerReplaceListener(tryWithResource));
		replaceInListIfExist(tryWithResource.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(tryWithResource));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtTypeParameterReferenceBoundingTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.reference.CtTypeParameterReference element;

		CtTypeParameterReferenceBoundingTypeReplaceListener(spoon.reflect.reference.CtTypeParameterReference element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setBoundingType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtTypeParameterReference(final spoon.reflect.reference.CtTypeParameterReference ref) {
		replaceElementIfExist(ref.getPackage(), new ReplacementVisitor.CtTypeReferencePackageReplaceListener(ref));
		replaceElementIfExist(ref.getDeclaringType(), new ReplacementVisitor.CtTypeReferenceDeclaringTypeReplaceListener(ref));
		replaceInListIfExist(ref.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(ref));
		replaceElementIfExist(ref.getBoundingType(), new ReplacementVisitor.CtTypeParameterReferenceBoundingTypeReplaceListener(ref));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtWildcardReference(spoon.reflect.reference.CtWildcardReference wildcardReference) {
		replaceElementIfExist(wildcardReference.getPackage(), new ReplacementVisitor.CtTypeReferencePackageReplaceListener(wildcardReference));
		replaceElementIfExist(wildcardReference.getDeclaringType(), new ReplacementVisitor.CtTypeReferenceDeclaringTypeReplaceListener(wildcardReference));
		replaceInListIfExist(wildcardReference.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(wildcardReference));
		replaceElementIfExist(wildcardReference.getBoundingType(), new ReplacementVisitor.CtTypeParameterReferenceBoundingTypeReplaceListener(wildcardReference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtIntersectionTypeReferenceBoundsReplaceListener implements spoon.generating.replace.ReplaceListListener<java.util.List> {
		private final spoon.reflect.reference.CtIntersectionTypeReference element;

		CtIntersectionTypeReferenceBoundsReplaceListener(spoon.reflect.reference.CtIntersectionTypeReference element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(java.util.List replace) {
			this.element.setBounds(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtIntersectionTypeReference(final spoon.reflect.reference.CtIntersectionTypeReference<T> reference) {
		replaceElementIfExist(reference.getPackage(), new ReplacementVisitor.CtTypeReferencePackageReplaceListener(reference));
		replaceElementIfExist(reference.getDeclaringType(), new ReplacementVisitor.CtTypeReferenceDeclaringTypeReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
		replaceInListIfExist(reference.getBounds(), new ReplacementVisitor.CtIntersectionTypeReferenceBoundsReplaceListener(reference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtTypeReference(final spoon.reflect.reference.CtTypeReference<T> reference) {
		replaceElementIfExist(reference.getPackage(), new ReplacementVisitor.CtTypeReferencePackageReplaceListener(reference));
		replaceElementIfExist(reference.getDeclaringType(), new ReplacementVisitor.CtTypeReferenceDeclaringTypeReplaceListener(reference));
		replaceInListIfExist(reference.getActualTypeArguments(), new ReplacementVisitor.CtActualTypeContainerActualTypeArgumentsReplaceListener(reference));
		replaceInListIfExist(reference.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(reference));
		replaceInListIfExist(reference.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(reference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtTypeAccessAccessedTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.code.CtTypeAccess element;

		CtTypeAccessAccessedTypeReplaceListener(spoon.reflect.code.CtTypeAccess element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setAccessedType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtTypeAccess(final spoon.reflect.code.CtTypeAccess<T> typeAccess) {
		replaceInListIfExist(typeAccess.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(typeAccess));
		replaceInListIfExist(typeAccess.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(typeAccess));
		replaceElementIfExist(typeAccess.getAccessedType(), new ReplacementVisitor.CtTypeAccessAccessedTypeReplaceListener(typeAccess));
		replaceInListIfExist(typeAccess.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(typeAccess));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtUnaryOperatorOperandReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtUnaryOperator element;

		CtUnaryOperatorOperandReplaceListener(spoon.reflect.code.CtUnaryOperator element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setOperand(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtUnaryOperator(final spoon.reflect.code.CtUnaryOperator<T> operator) {
		replaceInListIfExist(operator.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(operator));
		replaceElementIfExist(operator.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(operator));
		replaceInListIfExist(operator.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(operator));
		replaceElementIfExist(operator.getOperand(), new ReplacementVisitor.CtUnaryOperatorOperandReplaceListener(operator));
		replaceInListIfExist(operator.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(operator));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtVariableAccessVariableReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtVariableReference> {
		private final spoon.reflect.code.CtVariableAccess element;

		CtVariableAccessVariableReplaceListener(spoon.reflect.code.CtVariableAccess element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtVariableReference replace) {
			this.element.setVariable(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtVariableRead(final spoon.reflect.code.CtVariableRead<T> variableRead) {
		replaceInListIfExist(variableRead.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(variableRead));
		replaceInListIfExist(variableRead.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(variableRead));
		replaceElementIfExist(variableRead.getVariable(), new ReplacementVisitor.CtVariableAccessVariableReplaceListener(variableRead));
		replaceInListIfExist(variableRead.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(variableRead));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtVariableWrite(final spoon.reflect.code.CtVariableWrite<T> variableWrite) {
		replaceInListIfExist(variableWrite.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(variableWrite));
		replaceInListIfExist(variableWrite.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(variableWrite));
		replaceElementIfExist(variableWrite.getVariable(), new ReplacementVisitor.CtVariableAccessVariableReplaceListener(variableWrite));
		replaceInListIfExist(variableWrite.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(variableWrite));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtWhileLoopingExpressionReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.code.CtExpression> {
		private final spoon.reflect.code.CtWhile element;

		CtWhileLoopingExpressionReplaceListener(spoon.reflect.code.CtWhile element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.code.CtExpression replace) {
			this.element.setLoopingExpression(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtWhile(final spoon.reflect.code.CtWhile whileLoop) {
		replaceInListIfExist(whileLoop.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(whileLoop));
		replaceElementIfExist(whileLoop.getLoopingExpression(), new ReplacementVisitor.CtWhileLoopingExpressionReplaceListener(whileLoop));
		replaceElementIfExist(whileLoop.getBody(), new ReplacementVisitor.CtLoopBodyReplaceListener(whileLoop));
		replaceInListIfExist(whileLoop.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(whileLoop));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtCodeSnippetExpression(final spoon.reflect.code.CtCodeSnippetExpression<T> expression) {
		replaceElementIfExist(expression.getType(), new ReplacementVisitor.CtTypedElementTypeReplaceListener(expression));
		replaceInListIfExist(expression.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(expression));
		replaceInListIfExist(expression.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(expression));
		replaceInListIfExist(expression.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(expression));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtCodeSnippetStatement(final spoon.reflect.code.CtCodeSnippetStatement statement) {
		replaceInListIfExist(statement.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(statement));
		replaceInListIfExist(statement.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(statement));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtUnboundVariableReference(final spoon.reflect.reference.CtUnboundVariableReference<T> reference) {
		replaceElementIfExist(reference.getType(), new ReplacementVisitor.CtVariableReferenceTypeReplaceListener(reference));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtFieldAccessVariableReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtFieldReference> {
		private final spoon.reflect.code.CtVariableAccess element;

		CtFieldAccessVariableReplaceListener(spoon.reflect.code.CtVariableAccess element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtFieldReference replace) {
			this.element.setVariable(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtFieldRead(final spoon.reflect.code.CtFieldRead<T> fieldRead) {
		replaceInListIfExist(fieldRead.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(fieldRead));
		replaceInListIfExist(fieldRead.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(fieldRead));
		replaceElementIfExist(fieldRead.getTarget(), new ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(fieldRead));
		replaceElementIfExist(fieldRead.getVariable(), new ReplacementVisitor.CtFieldAccessVariableReplaceListener(fieldRead));
		replaceInListIfExist(fieldRead.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(fieldRead));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtFieldWrite(final spoon.reflect.code.CtFieldWrite<T> fieldWrite) {
		replaceInListIfExist(fieldWrite.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(fieldWrite));
		replaceInListIfExist(fieldWrite.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(fieldWrite));
		replaceElementIfExist(fieldWrite.getTarget(), new ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(fieldWrite));
		replaceElementIfExist(fieldWrite.getVariable(), new ReplacementVisitor.CtFieldAccessVariableReplaceListener(fieldWrite));
		replaceInListIfExist(fieldWrite.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(fieldWrite));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	class CtSuperAccessTypeReplaceListener implements spoon.generating.replace.ReplaceListener<spoon.reflect.reference.CtTypeReference> {
		private final spoon.reflect.declaration.CtTypedElement element;

		CtSuperAccessTypeReplaceListener(spoon.reflect.declaration.CtTypedElement element) {
			.this.element = element;
		}

		@java.lang.Override
		public void set(spoon.reflect.reference.CtTypeReference replace) {
			this.element.setType(replace);
		}
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public <T> void visitCtSuperAccess(final spoon.reflect.code.CtSuperAccess<T> f) {
		replaceElementIfExist(f.getType(), new ReplacementVisitor.CtSuperAccessTypeReplaceListener(f));
		replaceInListIfExist(f.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(f));
		replaceInListIfExist(f.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(f));
		replaceInListIfExist(f.getTypeCasts(), new ReplacementVisitor.CtExpressionTypeCastsReplaceListener(f));
		replaceElementIfExist(f.getTarget(), new ReplacementVisitor.CtTargetedExpressionTargetReplaceListener(f));
		replaceElementIfExist(f.getVariable(), new ReplacementVisitor.CtVariableAccessVariableReplaceListener(f));
	}

	// auto-generated, see spoon.generating.ReplacementVisitorGenerator
	@java.lang.Override
	public void visitCtComment(final spoon.reflect.code.CtComment comment) {
		replaceInListIfExist(comment.getComments(), new ReplacementVisitor.CtElementCommentsReplaceListener(comment));
		replaceInListIfExist(comment.getAnnotations(), new ReplacementVisitor.CtElementAnnotationsReplaceListener(comment));
	}
}

