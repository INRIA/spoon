package spoon.kotlin.compiler.fir

import org.jetbrains.kotlin.cli.jvm.compiler.NoScopeRecordCliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.expressions.impl.FirElseIfTrueCondition
import org.jetbrains.kotlin.fir.expressions.impl.FirNoReceiverExpression
import org.jetbrains.kotlin.fir.psi
import org.jetbrains.kotlin.fir.references.FirReference
import org.jetbrains.kotlin.fir.references.FirResolvedNamedReference
import org.jetbrains.kotlin.fir.resolve.toSymbol
import org.jetbrains.kotlin.fir.symbols.AbstractFirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.lexer.KtTokens.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.getPrevSiblingIgnoringWhitespaceAndComments
import org.jetbrains.kotlin.resolve.calls.CallTransformer
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.VariableAsFunctionResolvedCall
import spoon.SpoonException
import spoon.kotlin.compiler.SpoonKtEnvironment
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.kotlin.reflect.KtModifierKind
import spoon.reflect.code.CtCatchVariable
import spoon.reflect.code.UnaryOperatorKind
import spoon.kotlin.reflect.code.KtBinaryOperatorKind as KtOp
import spoon.reflect.declaration.CtModule
import spoon.reflect.declaration.CtType
import spoon.reflect.factory.Factory
import spoon.reflect.reference.CtReference
import spoon.reflect.reference.CtTypeReference


internal class FirTreeBuilderHelper(private val firTreeBuilder: FirTreeBuilder, private val spoonKtEnvironment: SpoonKtEnvironment) {

    private val analysisResult by lazy { // Don't want to analyze PSI module unless needed
        TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(spoonKtEnvironment.ktEnvironment.project,
            spoonKtEnvironment.ktEnvironment.getSourceFiles(),
            NoScopeRecordCliBindingTrace(),
            spoonKtEnvironment.config,
            spoonKtEnvironment.ktEnvironment::createPackagePartProvider)
    }

    fun createType(firClass: FirRegularClass): CtType<*> {
        val type: CtType<Any> = when (firClass.classKind) {
            ClassKind.CLASS -> firTreeBuilder.factory.Core().createClass<Any>()
            ClassKind.INTERFACE -> firTreeBuilder.factory.Core().createInterface()
            ClassKind.ENUM_CLASS -> firTreeBuilder.factory.Core().createEnum<Enum<*>>() as CtType<Any>
            ClassKind.ENUM_ENTRY -> TODO()
            ClassKind.ANNOTATION_CLASS -> TODO()
            ClassKind.OBJECT -> firTreeBuilder.factory.Core().createClass<Any>().apply {
                putMetadata<CtType<Any>>(KtMetadataKeys.CLASS_IS_OBJECT, true)
            }
        }
        type.setSimpleName<CtType<*>>(firClass.name.identifier)

        firTreeBuilder.addModifiersAsMetadata(type, KtModifierKind.fromClass(firClass))
        var didUseDelegateMap = false
        for (it in firClass.superConeTypes) {
            val ctSuperRef = firTreeBuilder.referenceBuilder.buildTypeReference<Any>(it)
            val symbol = it.lookupTag.toSymbol(firClass.session)?.fir
            if (symbol != null && symbol is FirRegularClass) {
                val delegateMap = firTreeBuilder.delegateMap[firClass.classId]
                if(delegateMap != null) {
                    val delegate = delegateMap[it.classId]
                    if(delegate != null) {
                        didUseDelegateMap = true
                        val ctDelegate = delegate.accept(firTreeBuilder,null).single
                        ctSuperRef.putMetadata<CtReference>(KtMetadataKeys.SUPER_TYPE_DELEGATE, ctDelegate)
                        ctDelegate.setParent(ctSuperRef)
                    }
                }
                when (symbol.classKind) {
                    ClassKind.CLASS -> {
                        type.setSuperclass<CtType<Any>>(ctSuperRef)
                    }
                    ClassKind.INTERFACE -> {
                        type.addSuperInterface<Any, CtType<Any>>(ctSuperRef)
                    }
                    else -> {
                        throw RuntimeException("Bad class kind for supertype: $symbol")
                    }
                }
            } else {
                if (symbol == null)
                    throw RuntimeException("Can't access class symbol")
                throw RuntimeException("Unknown symbol implementation: $symbol")
            }

        }

        return type
    }

    fun createCatchVariable(valueParam: FirValueParameter): CtCatchVariable<Throwable> =
        firTreeBuilder.factory.Core().createCatchVariable<Throwable>().apply {
            setSimpleName<CtCatchVariable<Throwable>>(valueParam.name.identifier)
            setType<CtCatchVariable<Throwable>>(firTreeBuilder.referenceBuilder.getNewTypeReference<Throwable>(valueParam.returnTypeRef))
        }


    fun getOrCreateModule(session: FirSession, factory: Factory): CtModule {
        val mname = session.moduleInfo?.name?.asString() ?: return factory.Module().unnamedModule
        return factory.Module().unnamedModule
        // return factory.Module().getOrCreate(mname)
    }

    fun getReceiver(qa: FirQualifiedAccess?): FirExpression? {
        if(qa == null) return null
        val explicitReceiver = qa.explicitReceiver
        val dispatchReceiver = qa.dispatchReceiver
        return if (explicitReceiver == null || explicitReceiver == FirNoReceiverExpression) {
            if (dispatchReceiver == FirNoReceiverExpression) {
                if(qa.extensionReceiver == FirNoReceiverExpression) null
                else qa.extensionReceiver
            }
            else dispatchReceiver
        } else {
            explicitReceiver
        }
    }

    fun resolveIfOperatorOrInvocation(firCall: FirFunctionCall): InvocationType {
        val source = firCall.source?.psi
        val receiver = getReceiver(firCall)

        if(firCall.arguments.size == 1 && firCall.arguments[0] is FirWhenSubjectExpression) {
            // We're at "in x -> {}" in a when-branch condition
            if (receiver == null) throw SpoonException("'in' operator in when condition without receiver")
            return InvocationType.BINARY_OPERATOR(
                firCall.arguments[0],
                tokenToBinaryOperatorKind(IN_KEYWORD),
                receiver,
                firCall
            )
        } else if(
            firCall.arguments.isEmpty() &&
            firCall.calleeReference.name.asString() == "not" &&
            receiver is FirFunctionCall) {

            if(receiver.arguments.size == 1 && receiver.arguments[0] is FirWhenSubjectExpression) {
                // We're at "!in x -> {}" in a when-branch condition
                val containsReceiver = getReceiver(receiver) ?: throw SpoonException("'!in' operator in when condition without receiver")
                return InvocationType.BINARY_OPERATOR(
                    receiver.arguments[0],
                    tokenToBinaryOperatorKind(NOT_IN),
                    containsReceiver,
                    firCall
                )
            }
        }

        return when (source) {
            is KtBinaryExpression -> {
                val opToken = source.operationToken
                if (receiver == null) throw SpoonException("Infix operator/function call without receiver")
                if (opToken == IDENTIFIER) InvocationType.INFIX_CALL(
                    receiver,
                    firCall,
                    firCall.arguments[0]
                )
                else orderBinaryOperands(opToken, receiver, firCall)
            }
            is KtPrefixExpression -> {
                val opToken = source.operationToken
                if (receiver == null) throw SpoonException("Prefix operator without receiver")
                InvocationType.PREFIX_OPERATOR(
                    tokenToUnaryOperatorKind(opToken),
                    receiver,
                    firCall
                )
            }
            is KtPostfixExpression -> {
                val opToken = source.operationToken
                if (receiver == null) throw SpoonException("Postfix operator without receiver")
                InvocationType.PREFIX_OPERATOR(
                    tokenToUnaryOperatorKind(opToken),
                    receiver,
                    firCall
                )
            }
            is KtArrayAccessExpression -> {
                val name = firCall.calleeReference.name.identifier
                if (receiver == null) throw SpoonException("Array access operator without receiver")
                when(name) {
                    "get" -> InvocationType.GET_OPERATOR(
                        receiver,
                        firCall.arguments,
                        firCall
                    )
                    "set" -> InvocationType.SET_OPERATOR(
                        receiver,
                        firCall.arguments.dropLast(1),
                        firCall.arguments.last(),
                        firCall
                    )
                    else -> throw SpoonException("Array access operator doesn't call get or set")
                }
            }
            else -> InvocationType.NORMAL_CALL(receiver, firCall)
        }
    }

    private fun orderBinaryOperands(
        token: IElementType,
        receiver: FirExpression,
        call: FirFunctionCall
    ): InvocationType {
        return when (token) {
            MUL, PLUS, MINUS, DIV, PERC, RANGE ->
                InvocationType.BINARY_OPERATOR(
                    receiver,
                    tokenToBinaryOperatorKind(token),
                    call.arguments[0],
                    call
                )
            MULTEQ, DIVEQ, PERCEQ, PLUSEQ, MINUSEQ ->
                InvocationType.ASSIGNMENT_OPERATOR(
                    receiver,
                    tokenToAssignmentOperatorKind(token),
                    call.arguments[0],
                    call
                )
            IN_KEYWORD -> InvocationType.BINARY_OPERATOR( // Reversed operand order
                call.arguments[0],
                tokenToBinaryOperatorKind(token),
                receiver,
                call
            )
            NOT_IN -> {
                val inExpression = receiver as? FirFunctionCall ?: throw RuntimeException("Unable to get 'contains' subtree of !in operator, $receiver")
                val inReceiver = getReceiver(inExpression)!!
                InvocationType.BINARY_OPERATOR( // Reversed operand order, ignore 'not' call (receiver)
                    inExpression.arguments[0],
                    tokenToBinaryOperatorKind(token),
                    inReceiver,
                    call
                )
            }
            else -> throw SpoonException("Unexpected operator for binary operator via function call $token")
        }
    }

    private fun tokenToAssignmentOperatorKind(token: IElementType) = when (token) {
        MULTEQ -> KtOp.MUL
        DIVEQ -> KtOp.DIV
        PERCEQ -> KtOp.MOD
        PLUSEQ -> KtOp.PLUS
        MINUSEQ -> KtOp.MINUS
        else -> throw SpoonException("Token ${token} is not an assignment operator")
    }

    private fun tokenToBinaryOperatorKind(token: IElementType) = when (token) {
        MUL -> KtOp.MUL
        PLUS -> KtOp.PLUS
        MINUS -> KtOp.MINUS
        DIV -> KtOp.DIV
        PERC -> KtOp.MOD
        RANGE -> KtOp.RANGE
        NOT_IN -> KtOp.NOT_IN
        IN_KEYWORD -> KtOp.IN
        else -> throw SpoonException("Unexpected token for binary operator via function call: $token")
    }

    private fun tokenToUnaryOperatorKind(token: IElementType) = when (token) {
        PLUS -> UnaryOperatorKind.POS
        MINUS -> UnaryOperatorKind.NEG
        EXCL -> UnaryOperatorKind.NOT
        else -> throw SpoonException("Unexpected token for unary operator via function call: $token")
    }

    fun isSingleExpressionBlock(block: FirBlock) = block.source?.psi !is KtBlockExpression

    fun hasExplicitTypeDeclaration(firTypedDeclaration: FirTypedDeclaration): Boolean? {
        val psi = firTypedDeclaration.psi ?: return null
        val typeRef = psi.getChildOfType<KtTypeReference>()
        return typeRef != null
    }

    private fun getResolvedSymbolOrNull(namedRef: FirReference): AbstractFirBasedSymbol<*>? {
        if(namedRef is FirResolvedNamedReference) {
            return namedRef.resolvedSymbol
        }
        return null
    }

    fun resolveIfInvokeOperatorCall(functionCall: FirFunctionCall): Boolean? {
        val callee = functionCall.calleeReference as? FirResolvedNamedReference ?: return false
        val actualFunction = callee.resolvedSymbol as? FirNamedFunctionSymbol   ?: return false
        val calledName = if(!callee.name.isSpecial) callee.name.identifier else return false
        if(actualFunction.fir.isOperator && actualFunction.fir.name.asString() == "invoke") {

            // Operator invoke is called, but we dont know if it's a() or a.invoke()
            if(calledName != "invoke") {
                // Easiest case "a()" has become "a.invoke()" during resolution
                return true
            }
            /*
            Else the receiver (e.g. variable holding a class with the invoke operator, or function) is named invoke.
            Ex.
            val invoke = ClassWithInvokeOperator()
            invoke()
            invoke.invoke()
            Tricky edge case, these 2, and other potential sequences, must be distinguished.
            */

            // As a last resort, analyze full PSI module and check the analysis result for an answer.
            val psi = functionCall.psi
            if(psi is KtCallExpression) {
                val call = psi.getResolvedCall(analysisResult.bindingContext)
                return call is VariableAsFunctionResolvedCall || call?.call is CallTransformer.CallForImplicitInvoke
            }
        }

        //Default to false
        return false
    }

    /*
     * Vararg type is already converted to array in the FIR tree.
     * Try to resolve what the actual type is
     */
    fun resolveVarargType(firValueParameter: FirValueParameter): CtTypeReference<Any> {
        assert(firValueParameter.isVararg)
        val arrayType = firValueParameter.returnTypeRef
        return if(arrayType is FirResolvedTypeRef) {
            if(arrayType.type.typeArguments.isEmpty()) { // Primitive array
                val coneArrayType = arrayType.type as? ConeClassLikeType ?: throw SpoonException("Can't get type of vararg parameter")
                val name = coneArrayType.lookupTag.classId.shortClassName.identifier
                val primitiveName = name.substring(0, name.indexOf("Array"))
                firTreeBuilder.referenceBuilder.getNewSimpleTypeReference<Any>("kotlin", primitiveName)
            } else {
                val coneTypeArgument = arrayType.type.typeArguments[0] as? ConeClassLikeType ?: throw SpoonException("Can't get type of vararg parameter")
                firTreeBuilder.referenceBuilder.getNewTypeReference<Any>(coneTypeArgument)
            }
        } else {
            firTreeBuilder.referenceBuilder.getNewTypeReference<Any>(firValueParameter.returnTypeRef)
        }

    }

    fun whenIsStatement(whenExpression: FirWhenExpression): Boolean {
        // Expression requires exhaustive branches,
        // No subject means no argument ==> must be statement
        if(!whenExpression.isExhaustive || whenExpression.subject == null) return true

        val psi = whenExpression.psi ?: return whenExpression.typeRef.isUnit // If no PSI, default to type is Unit (shouldn't happen)

        when(psi.context) {
            is KtProperty -> return false
            is KtFunction -> return false
        }

        var sibling: PsiElement? = psi.getPrevSiblingIgnoringWhitespaceAndComments()
        while(sibling != null) { // Go to left sibling, skip annotations and labels
            if(sibling is KtAnnotationEntry ||
                (sibling is KtContainerNode && psi.parent is KtLabeledExpression)) {
                sibling = sibling.getPrevSiblingIgnoringWhitespaceAndComments()
            }
            if(sibling is LeafPsiElement && sibling.text == "=") return false // Check if it's an assignment
            break
        }
        return true
    }

    fun resolveWhenBranchMultiCondition(whenBranch: FirWhenBranch): List<Pair<FirExpression,Boolean>> {
        val condition = whenBranch.condition
        if(condition is FirElseIfTrueCondition) return emptyList()

        val orderedExprs = ArrayList<Pair<FirExpression,Boolean>> ()
        visitExpressionsPreOrder(condition, false, orderedExprs)
        return orderedExprs
    }

    /**
     * Left pre order traversal of a branch condition, needed because
     * when(x) {
     *      a, is B, c -> {}
     *  }
     *  > translates to >
     *
     *  ((x == c) || x is B) || x == a
     *
     * There might be other EQ operations when subject is boolean.
     * Therefore, return any operation that is not OR, or EQ operations with 'x' (when-subject) as one of its operands.
     * Such EQ should only return the other operand.
     *
     * The result of the above example would be [a, x is B, c]
     *
     * The pair also holds a marker that indicates whether the LHS as implicit, which is needed for type operators and
     * EQ operations mentioned above.
     *
     * @param isRoot true if expression is the root of a condition.
     */
    private fun visitExpressionsPreOrder(expression: FirExpression, isRoot: Boolean, list: MutableList<Pair<FirExpression, Boolean>>) {
        when(expression) {
            is FirTypeOperatorCall -> {
                list.add(expression to true)
            }
            is FirFunctionCall -> {
                /*
                Look at isRoot to know whether we should mark LHS as implicit for 'in' calls that will be converted
                to binary operator. It is safe because the only possible case when isRoot is true is with a condition
                "in L". If the subject is boolean, the case below is legal

                when(b) {
                    in L -> {}  // Is actually (b in L)
                    b in L -> {} // Is actually (b == b in L)
                }
                 */
                list.add(expression to !isRoot)
            }
            is FirBinaryLogicExpression -> {
                if(expression.kind == LogicOperationKind.OR && !isRoot) {
                    visitExpressionsPreOrder(expression.leftOperand, false, list) // Keep traversing the LHS,
                    visitExpressionsPreOrder(expression.rightOperand, true, list) // break if we encounter another OR in the RHS.
                }
                else {
                    list.add(expression to false)
                }
            }
            is FirOperatorCall -> {
                // When-subject seems to always be in arguments[0] (LHS), but use any and filter to be sure
                if(expression.operation == FirOperation.EQ && expression.arguments.any { it is FirWhenSubjectExpression }) {
                    val rhs = expression.arguments.filterNot { it is FirWhenSubjectExpression }.first()
                    visitExpressionsPreOrder(rhs, true, list) // Visit the other operand, break on OR
                } else {
                    list.add(expression to false)
                }
            }
            else -> list.add(expression to false)
        }
    }
}