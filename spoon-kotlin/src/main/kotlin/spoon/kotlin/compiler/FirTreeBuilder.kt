package spoon.kotlin.compiler

import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.expressions.impl.FirElseIfTrueCondition
import org.jetbrains.kotlin.fir.expressions.impl.FirNoReceiverExpression
import org.jetbrains.kotlin.fir.expressions.impl.FirSingleExpressionBlock
import org.jetbrains.kotlin.fir.psi
import org.jetbrains.kotlin.fir.references.FirErrorNamedReference
import org.jetbrains.kotlin.fir.references.FirResolvedNamedReference
import org.jetbrains.kotlin.fir.references.FirSuperReference
import org.jetbrains.kotlin.fir.references.impl.FirExplicitThisReference
import org.jetbrains.kotlin.fir.references.impl.FirImplicitThisReference
import org.jetbrains.kotlin.fir.references.impl.FirPropertyFromParameterResolvedNamedReference
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.visitors.CompositeTransformResult
import org.jetbrains.kotlin.fir.visitors.FirVisitor
import org.jetbrains.kotlin.lexer.KtToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import spoon.SpoonException
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.kotlin.reflect.KtModifierKind
import spoon.kotlin.reflect.KtStatementExpression
import spoon.kotlin.reflect.KtStatementExpressionImpl
import spoon.kotlin.reflect.code.KtBinaryOperatorKind
import spoon.reflect.code.*
import spoon.reflect.declaration.*
import spoon.reflect.factory.Factory
import spoon.reflect.reference.*
import spoon.support.reflect.code.CtLiteralImpl
import java.util.*
import kotlin.collections.ArrayList

class FirTreeBuilder(val factory : Factory, val session: FirSession) : FirVisitor<CompositeTransformResult<CtElement>, Nothing?>() {
    internal val referenceBuilder = ReferenceBuilder(this)
    internal val helper = FirTreeBuilderHelper(this)

    // Temporary printing, remove later
    private var msgCollector: MsgCollector = PrintingMsgCollector()
    internal constructor(factory : Factory, session: FirSession, m: MsgCollector) : this(factory, session) {
        msgCollector = m
    }
    fun report(m : Message) = msgCollector.report(m)
    fun report(s : String) = report(Message(s, MessageType.COMMON))
    fun warn(s : String) = report(Message(s, MessageType.WARN))

    override fun visitElement(element: FirElement, data: Nothing?): CompositeTransformResult<CtElement> {
        //throw SpoonException("Element type not implemented $element")
        return CtLiteralImpl<String>().setValue<CtLiteral<String>>("Unimplemented element $element").compose()
    }

    override fun visitErrorNamedReference(
        errorNamedReference: FirErrorNamedReference,
        data: Nothing?
    ): CompositeTransformResult<CtElement> {
        throw RuntimeException("Error, file contains compile errors: ${errorNamedReference.diagnostic.reason}")
    }

    fun addModifiersAsMetadata(element: CtElement, modifierList: List<KtModifierKind>) {
        element.putMetadata<CtElement>(KtMetadataKeys.KT_MODIFIERS, modifierList.toMutableSet())
    }

    override fun visitFile(file: FirFile, data: Nothing?): CompositeTransformResult<CtElement> {
        val module = helper.getOrCreateModule(file.session, factory)
        val compilationUnit = factory.CompilationUnit().getOrCreate(file.name)

        val pkg = if(file.packageFqName.isRoot) module.rootPackage else
            factory.Package().getOrCreate(file.packageFqName.asString(), module)

        compilationUnit.declaredPackage = pkg

        val ktFile = file.psi
        if(ktFile is KtFile) {
            compilationUnit.lineSeparatorPositions = with(ktFile) {
                if (hasNormalizedEOL()) lineSeparatorPositions() else lineSeparatorPositionsRaw()
            }
        }

        val transformedTopLvlDecl = file.declarations.map {
            it.accept(this, null)
        }
        transformedTopLvlDecl.forEach {
            val t = it.single
            t.setParent(compilationUnit)
            if(t is CtType<*>) {
                pkg.addType<CtPackage>(t)
                compilationUnit.addDeclaredType(t)
            }

        }

        return compilationUnit.compose()
    }

    override fun visitRegularClass(regularClass: FirRegularClass, data: Nothing?): CompositeTransformResult<CtElement> {
        val module = helper.getOrCreateModule(regularClass.session, factory)
        val pkg = if (regularClass.classId.packageFqName.isRoot) module.rootPackage else
            factory.Package().getOrCreate(regularClass.classId.packageFqName.asString(), module)
        val type = helper.createType(regularClass)
        pkg.addType<CtPackage>(type)

        // Modifiers
        val modifierList = KtModifierKind.fromClass(regularClass)
        addModifiersAsMetadata(type, modifierList)

        val decls = regularClass.declarations.map {
            it.accept(this, null).single.also { decl ->
                decl.setParent(type)
                when (decl) {
                    is CtField<*> -> type.addField(decl)
                    is CtMethod<*> -> {
                        if (regularClass.isInterface() && decl.body != null) {
                            decl.setDefaultMethod<Nothing>(true)
                        }
                        type.addMethod(decl)
                    }
                    is CtConstructor<*> -> {
                        if (type is CtClass<*>) {
                            (type as CtClass<Any>).addConstructor<CtClass<Any>>(decl as CtConstructor<Any>)
                        } else warn("Constructor without accompanying CtClass")
                    }
                }
            }
        }

        return type.compose()
    }

    override fun visitConstructor(constructor: FirConstructor, data: Nothing?): CompositeTransformResult.Single<CtConstructor<*>> {
        val ctConstructor = factory.Core().createConstructor<Any>()
        ctConstructor.setSimpleName<CtConstructor<*>>(constructor.name.asString())

        val modifierList = listOfNotNull(KtModifierKind.convertVisibility(constructor.visibility))
        ctConstructor.setImplicit<CtConstructor<Any>>(constructor.isPrimary &&
                constructor.valueParameters.isEmpty() &&
                constructor.body == null &&
                modifierList.filterNot { it == KtModifierKind.PUBLIC }.isEmpty() &&
                constructor.source?.psi !is KtPrimaryConstructor
        )

        ctConstructor.putMetadata<CtConstructor<*>>(KtMetadataKeys.CONSTRUCTOR_IS_PRIMARY, constructor.isPrimary)

        addModifiersAsMetadata(ctConstructor, modifierList)

        // Add body
        val body = constructor.body?.accept(this, null)?.single as? CtStatement?
        if(body != null) {
            ctConstructor.setBody<CtConstructor<*>>(body)
        }

        // Add params
        constructor.valueParameters.forEach {
            val p = it.accept(this,null).single as CtParameter<*>
            /*
            * Primary constructor property declaration creates implicit properties in the class. An implicit property is the
            * holder of the val/var modifier, not the parameter:
            * ClassName(var x = 2) >translates to> ClassName(x = 2) { var x = x }
            * To facilitate printing, we look in the PSI if the parameter has modifiers and add them to metadata.
            *
            * TODO: Perhaps add metadata mapping property <-> param?
            *  */
            if(constructor.isPrimary) {
                val pModifiers = (p.getMetadata(KtMetadataKeys.KT_MODIFIERS) as? MutableSet<KtModifierKind>?) ?:
                        mutableSetOf<KtModifierKind>()
                val qqq = it.source.psi?.getChildrenOfType<KtModifierList>()
                val psiModifiersList = it.source.psi?.getChildrenOfType<KtModifierList>()?.let { lists ->
                    if(lists.isNotEmpty()) { KtModifierKind.fromPsiModifierList(lists[0]) }
                    else emptyList()
                } ?: emptyList()
                pModifiers.addAll(psiModifiersList)
                // Var/val might be outside of modifier list
                val psiTokens = it.source.psi?.getChildrenOfType<LeafPsiElement>()
                if(psiTokens?.any { t -> t.elementType == KtTokens.VAL_KEYWORD } == true) {
                    pModifiers.add(KtModifierKind.VAL)
                } else if(psiTokens?.any { t -> t.elementType == KtTokens.VAR_KEYWORD } == true) {
                    pModifiers.add(KtModifierKind.VAR)
                }
                p.putMetadata<CtParameter<*>>(KtMetadataKeys.KT_MODIFIERS, pModifiers)
            }
            ctConstructor.addParameter(p) // Sets parent
        }

        // Add delegate call
        val delegatedConstr = constructor.delegatedConstructor
        if(delegatedConstr != null) {
            val invocation = factory.Core().createInvocation<Any>()
           // invocation.setExecutable<CtInvocation<Any>>(referenceBuilder.getNewExecutableReference(delegatedConstr,
           // constructor.returnTypeRef))
            invocation.setExecutable<CtInvocation<Any>>(ConstructorDelegateResolver.resolveSuperCallBug(
                this, constructor)) // Sets parent

            delegatedConstr.arguments.forEach {
                val arg = it.accept(this,null).single as CtExpression<*>
                invocation.addArgument(arg) // Sets parent
            }
            ctConstructor.putMetadata<CtConstructor<*>>(KtMetadataKeys.CONSTRUCTOR_DELEGATE_CALL, invocation)
            invocation.setParent(ctConstructor)

            if (invocation.executable.type.simpleName == "Any") {
                invocation.setImplicit<CtInvocation<Any>>(true)
                invocation.executable.setImplicit<CtExecutableReference<Any>>(true)
            }
        }

        return ctConstructor.compose()
    }

    private fun getReceiver(qa: FirQualifiedAccessExpression): CtElement? {
        val explicitReceiver = qa.explicitReceiver
        val dispatchReceiver = qa.dispatchReceiver
        val receiver = if(explicitReceiver == null || explicitReceiver == FirNoReceiverExpression) {
            if(dispatchReceiver == FirNoReceiverExpression) null
            else dispatchReceiver.accept(this,null)
        } else {
            explicitReceiver.accept(this,null)
        }
        if(receiver != null && receiver.isSingle) return receiver.single
        return null
    }

    override fun visitTypeOperatorCall(
        typeOperatorCall: FirTypeOperatorCall,
        data: Nothing?
    ): CompositeTransformResult<CtElement> {
        return when(typeOperatorCall.operation) {
            FirOperation.IS, FirOperation.NOT_IS -> visitIsTypeOperation(typeOperatorCall)
            FirOperation.AS, FirOperation.SAFE_AS -> visitTypeCast(typeOperatorCall)
            else -> throw SpoonException("${typeOperatorCall.operation} is not a type operator")
        }
    }

    override fun visitResolvedTypeRef(
        resolvedTypeRef: FirResolvedTypeRef,
        data: Nothing?
    ): CompositeTransformResult<CtElement> {
        val typeAccess = factory.Core().createTypeAccess<Any>()
        typeAccess.setAccessedType<CtTypeAccess<Any>>(referenceBuilder.getNewTypeReference(resolvedTypeRef))
        return typeAccess.compose()
    }

    private fun visitIsTypeOperation(typeOperatorCall: FirTypeOperatorCall):
            CompositeTransformResult.Single<CtBinaryOperator<*>> {
        val ctBinaryOperator = factory.Core().createBinaryOperator<Boolean>()
        ctBinaryOperator.putMetadata<CtBinaryOperator<*>>(
            KtMetadataKeys.KT_BINARY_OPERATOR_KIND, KtBinaryOperatorKind.fromFirOperation(typeOperatorCall.operation))
        ctBinaryOperator.setType<CtBinaryOperator<Boolean>>(referenceBuilder.getNewTypeReference(typeOperatorCall.typeRef))
        val lhs = typeOperatorCall.argument.accept(this,null).single as CtExpression<*>
        val rhs = typeOperatorCall.conversionTypeRef.accept(this,null).single as CtExpression<*>
        ctBinaryOperator.setLeftHandOperand<CtBinaryOperator<Boolean>>(lhs)
        ctBinaryOperator.setRightHandOperand<CtBinaryOperator<Boolean>>(rhs)
        return ctBinaryOperator.compose()
    }

    private fun visitTypeCast(typeOperatorCall: FirTypeOperatorCall): CompositeTransformResult.Single<CtExpression<*>> {
        val castedExpr = typeOperatorCall.argument.accept(this,null).single as CtExpression<Any>
        val conversionTypeRef = referenceBuilder.getNewTypeReference<Any>(typeOperatorCall.conversionTypeRef)
        castedExpr.addTypeCast<CtExpression<Any>>(conversionTypeRef)

        val safe = typeOperatorCall.operation == FirOperation.SAFE_AS
        conversionTypeRef.putMetadata<CtTypeReference<*>>(KtMetadataKeys.TYPE_CAST_AS_SAFE, safe)
        return castedExpr.compose()
    }

    override fun visitFunctionCall(functionCall: FirFunctionCall, data: Nothing?): CompositeTransformResult<CtElement> {
        val invocationType = helper.resolveIfOperatorOrInvocation(functionCall)
        return when(invocationType) {
            is InvocationType.NORMAL_CALL -> visitNormalFunctionCall(invocationType)
            is InvocationType.INFIX_CALL -> visitInfixFunctionCall(invocationType)
            is InvocationType.BINARY_OPERATOR -> visitBinaryOperatorViaFunctionCall(invocationType)
            is InvocationType.ASSIGNMENT_OPERATOR -> visitAssignmentOperatorViaFunctionCall(invocationType)
            is InvocationType.POSTFIX_OPERATOR -> TODO()
            is InvocationType.PREFIX_OPERATOR -> visitUnaryOperatorViaFunctionCall(invocationType)
            is InvocationType.UNKNOWN -> throw RuntimeException("Unknown invocation type ${functionCall.calleeReference.name.asString()}")
        }
    }

    private fun visitNormalFunctionCall(call: InvocationType.NORMAL_CALL):
            CompositeTransformResult<CtInvocation<*>> {
        val (firReceiver, functionCall) = call
        val invocation = factory.Core().createInvocation<Any>()
        invocation.setExecutable<CtInvocation<Any>>(referenceBuilder.getNewExecutableReference(functionCall))

        val nonSpecialTarget = firReceiver?.accept(this,null)
        if(nonSpecialTarget?.isEmpty == true) {
            return CompositeTransformResult.empty()
        }
        val target = nonSpecialTarget?.single
        if(target is CtExpression<*>) {
            invocation.setTarget<CtInvocation<Any>>(target)
        } else if(target != null) {
            throw RuntimeException("Function call target not CtExpression")
        }

        if(functionCall.arguments.isNotEmpty()) {
            invocation.setArguments<CtInvocation<Any>>(functionCall.arguments.map {
                expressionOrWrappedInStatementExpression(it.accept(this,null).single)
            })
        }
        return invocation.compose()
    }

    private fun visitAssignmentOperatorViaFunctionCall(assignmentType: InvocationType.ASSIGNMENT_OPERATOR):
            CompositeTransformResult.Single<CtOperatorAssignment<*,*>> {
        val (firLhs, kind, firRhs, opFunc) = assignmentType
        val ctAssignmentOp = factory.createOperatorAssignment<Any,Any>()
        ctAssignmentOp.setKind<CtOperatorAssignment<Any,Any>>(kind.toJavaAssignmentOperatorKind())
        val lhs = expressionOrWrappedInStatementExpression(firLhs.accept(this,null).single)
        val rhs = expressionOrWrappedInStatementExpression(firRhs.accept(this,null).single)
        ctAssignmentOp.setAssigned<CtOperatorAssignment<Any,Any>>(lhs as CtExpression<Any>)
        ctAssignmentOp.setAssignment<CtOperatorAssignment<Any,Any>>(rhs as CtExpression<Any>)
        ctAssignmentOp.setType<CtOperatorAssignment<Any,Any>>(referenceBuilder.getNewTypeReference(opFunc.typeRef))
        return ctAssignmentOp.compose()
    }

    private fun visitInfixFunctionCall(invocationType: InvocationType.INFIX_CALL):
        CompositeTransformResult.Single<CtInvocation<*>> {
        val (firLhs, originalFunc, firRhs) = invocationType
        val invocation = factory.Core().createInvocation<Any>()
        invocation.setExecutable<CtInvocation<Any>>(referenceBuilder.getNewExecutableReference(originalFunc))
        val lhs = firLhs.accept(this,null).single as CtExpression<*>
        val rhs = expressionOrWrappedInStatementExpression(firRhs.accept(this,null).single)
        invocation.setTarget<CtInvocation<Any>>(lhs)
        invocation.setArguments<CtInvocation<Any>>(listOf(rhs))
        invocation.putMetadata<CtInvocation<Any>>(KtMetadataKeys.INVOCATION_IS_INFIX, true)
        return invocation.compose()
    }

    private fun visitBinaryOperatorViaFunctionCall(binType: InvocationType.BINARY_OPERATOR):
            CompositeTransformResult.Single<CtBinaryOperator<*>> {
        val (firLhs, kind, firRhs, opFunc) = binType
        val ktOp = factory.Core().createBinaryOperator<Any>()
        val lhs = firLhs.accept(this,null).single
        val rhs = firRhs.accept(this,null).single
        ktOp.setLeftHandOperand<CtBinaryOperator<Any>>(lhs as CtExpression<*>)
        ktOp.setRightHandOperand<CtBinaryOperator<Any>>(rhs as CtExpression<*>)
        if(opFunc is FirResolvedNamedReference) {
            ktOp.setType<CtBinaryOperator<Any>>(referenceBuilder.getNewTypeReference(opFunc.typeRef))
        }
        ktOp.putMetadata<CtBinaryOperator<Any>>(KtMetadataKeys.KT_BINARY_OPERATOR_KIND, kind)
        return ktOp.compose()
    }

    private fun visitUnaryOperatorViaFunctionCall(invocationType: InvocationType.PREFIX_OPERATOR):
            CompositeTransformResult.Single<CtUnaryOperator<*>> {
        val (kind, operand, opFunc) = invocationType
        val ctOp = factory.Core().createUnaryOperator<Any>()
        val ctOperand = operand.accept(this,null).single as CtExpression<Any>
        ctOp.setOperand<CtUnaryOperator<*>>(ctOperand)
        ctOp.setKind<CtUnaryOperator<*>>(kind)
        ctOp.setType<CtUnaryOperator<*>>(referenceBuilder.getNewTypeReference(opFunc.typeRef))
        return ctOp.compose()
    }

    override fun visitExpressionWithSmartcast(
        expressionWithSmartcast: FirExpressionWithSmartcast,
        data: Nothing?
    ): CompositeTransformResult<CtElement> {
        if(expressionWithSmartcast.originalExpression.source?.psi is KtUnaryExpression) return CompositeTransformResult.empty()
        val expr = expressionWithSmartcast.originalExpression.accept(this,null).single as CtTypedElement<*>
        val smartType = referenceBuilder.getNewTypeReference<Any>(expressionWithSmartcast.typeRef)
        if(expr.type != smartType)
            (expr as CtTypedElement<Any>).setType<CtTypedElement<Any>>(smartType)

        return expr.compose()
    }

    override fun visitWhenExpression(whenExpression: FirWhenExpression, data: Nothing?): CompositeTransformResult<CtElement> {
        if(whenExpression.isIf()) return visitIfExpression(whenExpression)
        val subjectVariable = whenExpression.subjectVariable
        if(subjectVariable?.name?.isSpecial == true) {
            if(subjectVariable.name.asString() == "<elvis>")
                return visitElvisOperator(whenExpression)
            else throw SpoonException(
                "Unexpected special in subject variable of when-expression: ${subjectVariable.name.asString()}")
        }


        return super.visitWhenExpression(whenExpression, data)
    }

    override fun visitWhenSubjectExpression(
        whenSubjectExpression: FirWhenSubjectExpression,
        data: Nothing?
    ): CompositeTransformResult<CtElement> {
        return whenSubjectExpression.whenSubject.whenExpression.subject!!.accept(this,null)
    }

    private fun visitElvisOperator(whenExpression: FirWhenExpression): CompositeTransformResult<CtBinaryOperator<*>> {
        val lhs = whenExpression.subjectVariable!!.initializer!!.accept(this,null).single as CtExpression<*>
        val rhs = whenExpression.branches.first { it.condition !is FirElseIfTrueCondition }.
            result.accept(this,null).single
        val ctOperator = factory.Core().createBinaryOperator<Any>()
        return ctOperator.apply {
            setLeftHandOperand<CtBinaryOperator<Any>>(expressionOrWrappedInStatementExpression(lhs))
            setRightHandOperand<CtBinaryOperator<Any>>(expressionOrWrappedInStatementExpression(rhs))
            putMetadata<CtBinaryOperator<*>>(KtMetadataKeys.KT_BINARY_OPERATOR_KIND, KtBinaryOperatorKind.ELVIS)
            setType<CtBinaryOperator<*>>(referenceBuilder.getNewTypeReference(whenExpression.typeRef))
        }.compose()
    }

    fun visitIfExpression(ifExpression : FirWhenExpression) : CompositeTransformResult.Single<CtIf> {
        // Sanity check
        if(ifExpression.branches.size > 2) {
            warn("WhenExpression misplaced as if")
        }

        val ctIf = factory.Core().createIf()
        val condition = ifExpression.branches[0].condition.accept(this,null).single
        ctIf.setCondition<CtIf>(condition as CtExpression<Boolean>)
        val thenBranch = ifExpression.branches[0].result.accept(this,null).single

        fun branchToStatement(e : CtElement) : CtStatement = when(e) {
            is CtStatement -> e
            is CtExpression<*> -> e.wrapInImplicitReturn()
            else -> throw RuntimeException("Branch is not statement nor expression")
        }

        ctIf.setThenStatement<CtIf>(branchToStatement(thenBranch))

        ifExpression.branches.getOrNull(1)?.let {
            ctIf.setElseStatement<CtIf>(branchToStatement(it.result.accept(this,null).single))
        }

        // Add type
        val type = referenceBuilder.getNewTypeReference<CtType<Any>>(ifExpression.typeRef)
        ctIf.putMetadata<CtIf>(KtMetadataKeys.KT_STATEMENT_TYPE, type)

        return ctIf.compose()
    }

    /*
       for(x in y) { ... }

       >is translated to>

       val <range> = y
       val <iterator> = <range>.iterator
       while(<iterator>.hasNext()) {
        val i = <iterator>.next()
        ...
       }
     */
    override fun visitBlock(block: FirBlock, data: Nothing?): CompositeTransformResult<CtElement> {
        if(block is FirSingleExpressionBlock) return visitSingleExpressionBlock(block)
        val ktBlock = factory.Core().createBlock<Any>()
        val statements = ArrayList<CtStatement>()
        val loopIterableStack = Stack<CtExpression<*>>()
        for(firStatement in block.statements) {
            if(firStatement is FirProperty && firStatement.isLocal && firStatement.name.isSpecial) {
                if(firStatement.name.asString() == "<range>")
                    loopIterableStack.push(firStatement.initializer!!.accept(this,null).single as CtExpression<*>)
                continue
            }
            val ctElementResult = firStatement.accept(this, null)
            if(ctElementResult.isEmpty) continue
            val ctElement = ctElementResult.single

            if(ctElement is CtForEach) {
                ctElement.setExpression<CtForEach>(loopIterableStack.pop())
            }

            statements.add(if(ctElement is CtExpression<*> && ctElement !is CtStatement) {
                ctElement.wrapInImplicitReturn()
            } else {
                ctElement as CtStatement
            })
        }
        ktBlock.setStatements<CtBlock<*>>(statements)

        ktBlock.putMetadata<CtBlock<*>>(KtMetadataKeys.KT_STATEMENT_TYPE,
            referenceBuilder.getNewTypeReference<CtBlock<*>>(block.typeRef))
        return ktBlock.compose()
    }

    private fun visitSingleExpressionBlock(firBlock: FirSingleExpressionBlock):
        CompositeTransformResult.Single<CtBlock<*>> {
        val ctStatement = statementOrWrappedInImplicitReturn(firBlock.statement.accept(this,null).single)
        if(ctStatement is CtBlock<*> && ctStatement.statements.size == 1) {
            ctStatement.setImplicit<CtBlock<*>>(true)
            return ctStatement.compose()
        }
        val ctBlock = factory.Core().createBlock<Any>()
        ctBlock.setStatements<CtBlock<*>>(listOf(ctStatement))
        ctBlock.putMetadata<CtBlock<*>>(KtMetadataKeys.KT_STATEMENT_TYPE,
            referenceBuilder.getNewTypeReference<CtBlock<*>>(firBlock.typeRef))
        ctBlock.setImplicit<CtBlock<*>>(true)
        return ctBlock.compose()
    }

    override fun visitWhileLoop(whileLoop: FirWhileLoop, data: Nothing?): CompositeTransformResult<CtElement> {
        when(whileLoop.source?.psi) {
            null -> throw RuntimeException("Unknown source of while loop")
            is KtForExpression -> return visitForLoop(whileLoop,null)
        }


        return super.visitWhileLoop(whileLoop, data)
    }

    fun visitForLoop(forLoop: FirWhileLoop, data: Nothing?): CompositeTransformResult<CtElement> {
        val ctForEach = factory.Core().createForEach()
        val variable = forLoop.block.statements[0].accept(this,null).single as CtLocalVariable<*>
        // Remove initializer ( = next() )
        (variable as CtLocalVariable<Any>).setDefaultExpression<CtLocalVariable<Any>>(null)
        // Loop initialized local vars have no modifiers
        variable.putMetadata<CtLocalVariable<*>>(KtMetadataKeys.KT_MODIFIERS, null)
        ctForEach.setVariable<CtForEach>(variable)
        // Local loop variable has already been handled, remove before visiting
        (forLoop.block.statements as MutableList<FirStatement>).removeAt(0)
        val body = forLoop.block.accept(this,null).single as CtStatement
        ctForEach.setBody<CtForEach>(body)
        return ctForEach.compose()
    }

    override fun visitOperatorCall(operatorCall: FirOperatorCall, data: Nothing?): CompositeTransformResult<CtElement> {
        val op = factory.Core().createBinaryOperator<Any>()
        val kind = KtBinaryOperatorKind.fromFirOperation(operatorCall.operation)
        op.putMetadata<CtBinaryOperator<Any>>(KtMetadataKeys.KT_BINARY_OPERATOR_KIND, kind)

        if(operatorCall.arguments.size != 2) throw RuntimeException("Binary operator has ${operatorCall.arguments.size} argument(s)")
        val lhs = operatorCall.arguments[0].accept(this,null).single
        val rhs = operatorCall.arguments[1].accept(this,null).single

        op.setLeftHandOperand<CtBinaryOperator<Any>>(expressionOrWrappedInStatementExpression(lhs))
        op.setRightHandOperand<CtBinaryOperator<Any>>(expressionOrWrappedInStatementExpression(rhs))
        op.setType<CtBinaryOperator<Any>>(referenceBuilder.getNewTypeReference(operatorCall.typeRef))
        return op.compose()
    }

    override fun visitBinaryLogicExpression(
        binaryLogicExpression: FirBinaryLogicExpression,
        data: Nothing?
    ): CompositeTransformResult<CtElement> {
        val op = factory.Core().createBinaryOperator<Boolean>()
        val kind = KtBinaryOperatorKind.firLogicOperationToJavaBinOp(binaryLogicExpression.kind)
        op.setKind<CtBinaryOperator<Boolean>>(kind)

        val lhs = binaryLogicExpression.leftOperand.accept(this,null).single
        val rhs = binaryLogicExpression.rightOperand.accept(this,null).single

        op.setLeftHandOperand<CtBinaryOperator<Boolean>>(expressionOrWrappedInStatementExpression(lhs))
        op.setRightHandOperand<CtBinaryOperator<Boolean>>(expressionOrWrappedInStatementExpression(rhs))
        op.setType<CtBinaryOperator<Boolean>>(referenceBuilder.getNewTypeReference(binaryLogicExpression.typeRef))
        return op.compose()
    }

    override fun visitSimpleFunction(simpleFunction: FirSimpleFunction, data: Nothing?): CompositeTransformResult.Single<CtMethod<*>> {
        val ctMethod = factory.Core().createMethod<Any>()
        ctMethod.setSimpleName<CtMethod<Any>>(simpleFunction.name.identifier)

        // Add modifiers
        val modifiers = KtModifierKind.fromFunctionDeclaration(simpleFunction)
        addModifiersAsMetadata(ctMethod, modifiers)

        // Add params
        simpleFunction.valueParameters.forEach {
            val p = it.accept(this,null).single
            if(p !is CtParameter<*>) {
                warn("Transformed parameter is not CtParameter")
            }
            else ctMethod.addParameter<CtMethod<Any>>(p)
        }

        // Add body
        val body = simpleFunction.body
        if(body != null) {
            val ctBody = body.accept(this, null).single
            ctMethod.setBody<CtMethod<Any>>(ctBody as CtStatement)
        }

        // Set (return) type
        ctMethod.setType<CtMethod<*>>(referenceBuilder.getNewTypeReference<Any>(simpleFunction.returnTypeRef))
        val receiver = simpleFunction.receiverTypeRef?.accept(this,null)?.single
        receiver?.setParent<CtElement>(ctMethod)
        ctMethod.putMetadata<CtMethod<*>>(KtMetadataKeys.EXTENSION_TYPE_REF, receiver)
        return ctMethod.compose()
    }

    override fun visitThisReceiverExpression(
        thisReceiverExpression: FirThisReceiverExpression,
        data: Nothing?
    ): CompositeTransformResult.Single<CtThisAccess<*>> {
        val thisAccess = factory.Core().createThisAccess<Any>()
        thisAccess.setType<CtThisAccess<*>>(referenceBuilder.getNewTypeReference(thisReceiverExpression.typeRef))
        val implicit = when(thisReceiverExpression.calleeReference) {
            is FirExplicitThisReference -> false
            is FirImplicitThisReference -> true
            else -> false
        }
        thisAccess.setImplicit<CtThisAccess<*>>(implicit)

        return thisAccess.compose()
    }

    override fun visitSuperReference(
        superReference: FirSuperReference,
        data: Nothing?
    ): CompositeTransformResult.Single<CtSuperAccess<*>> {
        val superAccess = factory.Core().createSuperAccess<Any>()
        superAccess.setType<CtSuperAccess<Any>>(referenceBuilder.getNewTypeReference(superReference.superTypeRef))
        superAccess.setImplicit<CtSuperAccess<*>>(false)
        return superAccess.compose()
    }

    private fun visitUnaryExpression(assignment: FirVariableAssignment): CompositeTransformResult.Single<CtUnaryOperator<*>> {
        val ctUnaryOp = factory.Core().createUnaryOperator<Any>()
        val functionCall = assignment.rValue as FirFunctionCall
        val inc = functionCall.calleeReference.name.identifier == "inc"
        val kind = when(assignment.source?.psi) {
            is KtPostfixExpression -> if(inc) UnaryOperatorKind.POSTINC else UnaryOperatorKind.POSTDEC
            is KtPrefixExpression -> if(inc) UnaryOperatorKind.PREINC else UnaryOperatorKind.PREDEC
            else -> null
        }
        ctUnaryOp.setKind<CtUnaryOperator<*>>(kind)
        val target = helper.getReceiver(assignment)?.accept(this, null)?.single as CtExpression<*>?
        val lvalue = assignment.lValue.accept(this,null).single as CtReference
        val operand = createVariableWrite(target, lvalue)
        ctUnaryOp.setOperand<CtUnaryOperator<*>>(operand)
        ctUnaryOp.setType<CtUnaryOperator<*>>(referenceBuilder.getNewTypeReference(assignment.rValue.typeRef))
        return ctUnaryOp.compose()
    }

    private fun createVariableWrite(receiver: CtExpression<*>?, lvalue: CtReference) = when (lvalue) {
        is CtLocalVariableReference<*> ->
            factory.Core().createVariableWrite<Any>().also {
                it.setVariable<CtVariableAccess<Any>>(lvalue as CtLocalVariableReference<Any>)
            }
        is CtFieldReference<*> -> {
            factory.Core().createFieldWrite<Any>().also {
                it.setVariable<CtVariableAccess<Any>>(lvalue as CtFieldReference<Any>)
                it.setTarget<CtTargetedExpression<Any, CtExpression<*>>>(receiver)
            }
        }
        else -> throw SpoonException("Unexpected expression ${lvalue::class.simpleName}")
    }

    override fun visitVariableAssignment(
        variableAssignment: FirVariableAssignment,
        data: Nothing?
    ): CompositeTransformResult.Single<CtExpression<*>> {

        if(variableAssignment.source?.psi is KtUnaryExpression)
            return visitUnaryExpression(variableAssignment)
        val ctAssignment = factory.createAssignment<Any, Any>()

        val psiExp = (variableAssignment.source?.psi as? KtBinaryExpression) ?: throw SpoonException("No PSI for variable assignment")
        val opToken = psiExp.operationToken
        if(opToken != KtTokens.EQ)
            return visitAssignmentOperatorViaFunctionCall(
                helper.resolveIfOperatorOrInvocation(variableAssignment.rValue as FirFunctionCall) as InvocationType.ASSIGNMENT_OPERATOR
            )


        val assignmentExpr = variableAssignment.rValue.accept(this, null).single
        ctAssignment.setAssignment<CtAssignment<Any, Any>>(assignmentExpr as CtExpression<Any>)

        val lvalue = variableAssignment.lValue.accept(this, null).single as CtReference
        val target = helper.getReceiver(variableAssignment)?.accept(this,null)?.single as CtExpression<*>
        val ctWrite = createVariableWrite(target, lvalue)
        ctAssignment.setAssigned<CtAssignment<Any, Any>>(ctWrite)

        return ctAssignment.compose()
    }

    override fun visitResolvedNamedReference(
        resolvedNamedReference: FirResolvedNamedReference,
        data: Nothing?
    ): CompositeTransformResult<CtElement> {
        val fir = resolvedNamedReference.resolvedSymbol.fir
        val ctRef = when(fir) {
            is FirProperty -> {
                if(fir.name.isSpecial) return CompositeTransformResult.empty()
                referenceBuilder.getNewVariableReference<CtVariableReference<Any>>(fir)
            }
            is FirValueParameter -> {
                referenceBuilder.getNewVariableReference<CtVariableReference<Any>>(fir)
            }
            else -> null
        }
        if(ctRef != null) return ctRef.compose()
        return super.visitResolvedNamedReference(resolvedNamedReference, data)
    }

    override fun visitValueParameter(
        valueParameter: FirValueParameter,
        data: Nothing?
    ): CompositeTransformResult.Single<CtParameter<*>> {
        val ctParam = factory.Core().createParameter<Any>()
        ctParam.setSimpleName<CtParameter<Any>>(valueParameter.name.identifier)
        ctParam.setInferred<CtParameter<Any>>(false) // Not allowed

        // Modifiers
        val modifierList = KtModifierKind.fromValueParameter(valueParameter)
        addModifiersAsMetadata(ctParam, modifierList)
        ctParam.setVarArgs<CtParameter<Any>>(KtModifierKind.VARARG in modifierList)

        // Default value
        val defaultValue = valueParameter.defaultValue?.accept(this, null)?.single
        if(defaultValue != null) { // TODO Replace with setDefaultExpr
            ctParam.putMetadata<CtParameter<*>>(KtMetadataKeys.PARAMETER_DEFAULT_VALUE, defaultValue)
            defaultValue.setParent(ctParam)
        }

        // Type
        ctParam.setType<CtParameter<Any>>(referenceBuilder.getNewTypeReference<Any>(valueParameter.returnTypeRef))

        return ctParam.compose()
    }

    override fun visitProperty(property: FirProperty, data: Nothing?): CompositeTransformResult<CtVariable<*>> {
        /*
            a++
            >translates to>
            <unary> = a     // check if we're here, then unary operator will be created in the next visit
            a = <unary>.inc()
            a
         */
        if(property.source?.psi is KtUnaryExpression)
            return CompositeTransformResult.empty()  // Context is responsible for handling this case
        if(property.isLocal)
            return visitLocalVariable(property, data)

        val ctProperty = factory.Core().createField<Any>()
        ctProperty.setSimpleName<CtField<*>>(property.name.identifier)

        // Visit and transform initializer
        val transformedExpression = property.initializer?.accept(this, null)
        if(transformedExpression != null && transformedExpression.isSingle) {
            val initializer = transformedExpression.single
            when (initializer) {
                is CtExpression<*> -> {
                    ctProperty.setDefaultExpression<CtField<Any>>(initializer as CtExpression<Any>)
                    initializer.setParent(ctProperty)
                }
                is CtIf -> {
                    val typeRef = initializer.getMetadata(KtMetadataKeys.KT_STATEMENT_TYPE) as CtTypeReference<Any>
                    val statementExpression = initializer.wrapInStatementExpression(typeRef)
                    statementExpression.setImplicit<CtStatement>(true)
                    ctProperty.setDefaultExpression<CtField<Any>>(statementExpression)
                    initializer.setParent(ctProperty)
                    statementExpression.setParent(ctProperty)
                }
                else -> warn("Property initializer not a CtExpression or if-statement: $initializer")
            }
        }


        // Transform and add delegate to metadata if it exists
        val delegate = property.delegate?.accept(this,null)
        if(delegate != null && delegate.isSingle) {
            val ctDelegate = delegate.single
            if(ctDelegate is CtExpression<*>) {
                ctProperty.putMetadata<CtElement>(KtMetadataKeys.PROPERTY_DELEGATE, ctDelegate)
                ctDelegate.setParent(ctProperty)
            }
        }

        // Add modifiers
        addModifiersAsMetadata(ctProperty, KtModifierKind.fromProperty(property))

        val returnType = property.returnTypeRef
        // Add type
        ctProperty.setType<CtField<*>>(referenceBuilder.getNewTypeReference(returnType))

        // Mark as implicit/explicit type
        val explicitType = (returnType is FirResolvedTypeRef && returnType.delegatedTypeRef != null)
        ctProperty.putMetadata<CtField<*>>(KtMetadataKeys.VARIABLE_EXPLICIT_TYPE, explicitType)

        // Check if property stems from primary constructor value parameter, in that case this property is implicit
        val initializer = property.initializer
        if(initializer is FirQualifiedAccessExpression &&
            initializer.calleeReference is FirPropertyFromParameterResolvedNamedReference) {
            ctProperty.setImplicit<CtField<*>>(true)
        }

        // TODO getter/setter

        return ctProperty.compose()
    }

    private fun getBaseOfConst(constExpression: FirConstExpression<Number>) : LiteralBase {
        val text = constExpression.source?.psi?.text
        if(text?.startsWith("0x", ignoreCase = true) == true) return LiteralBase.HEXADECIMAL
        if(text?.startsWith("0b", ignoreCase = true) == true) return LiteralBase.BINARY
        // No octal in Kotlin
        return LiteralBase.DECIMAL
    }

    override fun <T> visitConstExpression(constExpression: FirConstExpression<T>, data: Nothing?): CompositeTransformResult<CtLiteral<T>> {
        val value = when(constExpression.kind) {
            FirConstKind.Int -> (constExpression.value as Long).toInt()
            FirConstKind.Null -> null
            FirConstKind.Boolean -> constExpression.value as Boolean
            FirConstKind.Char -> constExpression.value as Char
            FirConstKind.Byte -> (constExpression.value as Long).toByte()
            FirConstKind.Short -> (constExpression.value as Long).toShort()
            FirConstKind.Long -> constExpression.value as Long
            FirConstKind.String -> constExpression.value as String
            FirConstKind.Float -> constExpression.value as Float
            FirConstKind.Double -> constExpression.value as Double
            FirConstKind.IntegerLiteral -> (constExpression.value as Long).toInt()
        }
        val l : CtLiteral<T> = factory.Core().createLiteral()
        l.setValue<CtLiteral<T>>(value as T)
        if(value == null)
            l.setType<CtLiteral<T>>(factory.Type().nullType() as CtTypeReference<T>)
        else
            l.setType<CtLiteral<T>>(referenceBuilder.getNewTypeReference(constExpression.typeRef))

        if(value is Number) {
            l.setBase<CtLiteral<T>>(getBaseOfConst(constExpression as FirConstExpression<Number>))
            if(constExpression.value is Float || constExpression.value is Double) {
                l.putMetadata<CtLiteral<T>>(KtMetadataKeys.FLOAT_LITERAL_SCIENTIFIC, constExpression.psi?.textContains('e') == true)
            }
        }
        if(constExpression.value is String) {
            // Check if it's multiline
            val openQuote = constExpression.source.psi?.getChildrenOfType<LeafPsiElement>()?.
                firstOrNull { it.node.elementType == KtTokens.OPEN_QUOTE }
            val multiline = "\"\"\"" == openQuote?.text
            l.putMetadata<CtLiteral<T>>(KtMetadataKeys.STRING_LITERAL_MULTILINE, multiline)
        }
        return l.compose()
    }

    fun visitLocalVariable(property: FirProperty, data: Nothing?) : CompositeTransformResult.Single<CtVariable<*>> {
        val localVar = factory.Core().createLocalVariable<Any>().also {
            it.setSimpleName<CtLocalVariable<Any>>(property.name.identifier)
        }

        val transformedExpression = property.initializer?.accept(this,null)
        if(transformedExpression != null && transformedExpression.isSingle) {
            when(val initializer = transformedExpression.single) {
                is CtExpression<*> -> {
                    localVar.setDefaultExpression<CtLocalVariable<Any>>(initializer as CtExpression<Any>)
                    initializer.setParent(localVar)
                }
                is CtIf -> {
                    val typeRef = initializer.getMetadata(KtMetadataKeys.KT_STATEMENT_TYPE) as CtTypeReference<Any>
                    val statementExpression = initializer.wrapInStatementExpression(typeRef)
                    statementExpression.setImplicit<CtStatement>(true)
                    localVar.setDefaultExpression<CtLocalVariable<Any>>(statementExpression)
                    initializer.setParent(localVar)
                    statementExpression.setParent(localVar)
                }
                else -> warn("Local variable initializer not a CtExpression or if-statement: $initializer")
            }
        }

        // Transform and add delegate to metadata if it exists
        val delegate = property.delegate?.accept(this,null)
        if(delegate != null && delegate.isSingle) {
            val ctDelegate = delegate.single
            if(ctDelegate is CtExpression<*>) {
                localVar.putMetadata<CtElement>(KtMetadataKeys.PROPERTY_DELEGATE, ctDelegate)
                ctDelegate.setParent(localVar)
            }
        }

        // Add modifiers
        addModifiersAsMetadata(localVar, KtModifierKind.fromProperty(property))

        val returnType = property.returnTypeRef
        // Add type
        localVar.setType<CtLocalVariable<*>>(referenceBuilder.getNewTypeReference(returnType))

        // Mark as implicit/explicit type
        val explicitType = (returnType is FirResolvedTypeRef && returnType.delegatedTypeRef != null)
        localVar.putMetadata<CtLocalVariable<*>>(KtMetadataKeys.VARIABLE_EXPLICIT_TYPE, explicitType)
        localVar.setInferred<CtLocalVariable<Any>>(!explicitType)

        return localVar.compose()
    }

    override fun visitReturnExpression(
        returnExpression: FirReturnExpression,
        data: Nothing?
    ): CompositeTransformResult.Single<CtReturn<*>> {
        val ctReturn = factory.Core().createReturn<Any>()
        val ctExpr = returnExpression.result.accept(this,null).single

        when(ctExpr) {
            is CtExpression<*> -> {
                ctReturn.setReturnedExpression<CtReturn<Any>>(ctExpr as CtExpression<Any>)
                ctExpr.setParent(ctReturn)
            }
            is CtIf -> {
                val typeRef = ctExpr.getMetadata(KtMetadataKeys.KT_STATEMENT_TYPE) as CtTypeReference<Any>
                val statementExpression = ctExpr.wrapInStatementExpression(typeRef)
                statementExpression.setImplicit<CtStatement>(true)
                ctReturn.setReturnedExpression<CtReturn<Any>>(statementExpression)
                ctExpr.setParent(ctReturn)
                statementExpression.setParent(ctReturn)
            }
        }
        if(returnExpression.source == null)
            ctReturn.setImplicit<CtReturn<*>>(true)
        return ctReturn.compose()
    }

    @Suppress("UNCHECKED_CAST")
    override fun visitQualifiedAccessExpression(
        qualifiedAccessExpression: FirQualifiedAccessExpression,
        data: Nothing?
    ): CompositeTransformResult<CtElement> {
        val calleeRefRes = qualifiedAccessExpression.calleeReference.accept(this,null)
        if(calleeRefRes.isEmpty) return CompositeTransformResult.empty()
        val calleeRef = calleeRefRes.single
        val target: CtElement? = getReceiver(qualifiedAccessExpression)
        val varAccess = when(calleeRef) {
            is CtFieldReference<*> -> {
                factory.Core().createFieldRead<Any>().also {
                    it.setVariable<CtVariableRead<Any>>(calleeRef as CtVariableReference<Any>)
                }
            }
            is CtParameterReference<*> -> {
                factory.Core().createVariableRead<Any>().also {
                    it.setVariable<CtVariableRead<Any>>(calleeRef as CtVariableReference<Any>)
                }
            }
            is CtLocalVariableReference<*> -> {
                factory.Core().createVariableRead<Any>().also {
                    it.setVariable<CtVariableRead<Any>>(calleeRef as CtVariableReference<Any>)
                }
            }
            is CtSuperAccess<*> -> {
                calleeRef
            }
            else -> null
        }
        if(varAccess == null) {
            return super.visitQualifiedAccessExpression(qualifiedAccessExpression, data)
        }
        if(target != null && varAccess is CtFieldRead<*>) {
            (varAccess as CtFieldRead<Any>).setTarget<CtFieldRead<Any>>(target as CtExpression<*>)
        }

        return varAccess.compose()
    }

    private fun <T : CtElement> T.compose() = CompositeTransformResult.single(this)
    private fun <T : CtElement> List<CompositeTransformResult<T>>.composeManySingles() = CompositeTransformResult.many(this.map { it.single })
    private fun <T : CtElement> List<CompositeTransformResult<T>>.compose() = CompositeTransformResult.many(this)
    private fun FirClass<*>.isInterface() = this.classKind == ClassKind.INTERFACE
    private fun FirClass<*>.isClass() = this.classKind == ClassKind.CLASS
    private fun FirClass<*>.isObject() = this.classKind == ClassKind.OBJECT
    private fun FirClass<*>.isEnumClass() = this.classKind == ClassKind.ENUM_CLASS
    private fun FirWhenExpression.isIf() = this.subject == null &&
            this.subjectVariable?.apply { warn("Subject variable found: ${this}") } == null // Temporary warn, don't know what subject variable is
    private fun <T> CtExpression<T>.wrapInImplicitReturn() : CtReturn<T> {
        val r = factory.Core().createReturn<T>()
        r.setReturnedExpression<CtReturn<T>>(this)
        r.setImplicit<CtReturn<T>>(true)
        return r
    }
    private fun <T> CtStatement.wrapInStatementExpression(type : CtTypeReference<T>) : KtStatementExpression<T> {
        val se = KtStatementExpressionImpl<T>(this)
        se.setType<KtStatementExpression<T>>(type)
        return se
    }

    private fun statementOrWrappedInImplicitReturn(e: CtElement): CtStatement = when(e) {
        is CtStatement -> e
        is CtExpression<*> -> e.wrapInImplicitReturn()
        else -> throw RuntimeException("Can't wrap ${e::class} in StatementExpression")
    }
    private fun expressionOrWrappedInStatementExpression(e: CtElement): CtExpression<*> = when(e) {
        is CtExpression<*> -> e
        is CtIf -> {
            val typeRef = e.getMetadata(KtMetadataKeys.KT_STATEMENT_TYPE) as CtTypeReference<Any>
            val statementExpression = e.wrapInStatementExpression(typeRef)
            statementExpression.setImplicit(true)
        }
        is CtBlock<*> -> {
            val typeRef = e.getMetadata(KtMetadataKeys.KT_STATEMENT_TYPE) as CtTypeReference<Any>
            val statementExpression = e.wrapInStatementExpression(typeRef)
            statementExpression.setImplicit(true)
        }
        else -> throw RuntimeException("Can't wrap ${e::class.simpleName} in StatementExpression")
    }
}