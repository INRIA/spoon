package spoon.kotlin.compiler

import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.superConeTypes
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirQualifiedAccessExpression
import org.jetbrains.kotlin.fir.expressions.impl.FirNoReceiverExpression
import org.jetbrains.kotlin.fir.psi
import org.jetbrains.kotlin.fir.resolve.toSymbol
import org.jetbrains.kotlin.lexer.KtTokens.*
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtPostfixExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import spoon.SpoonException
import spoon.kotlin.reflect.KtModifierKind
import spoon.reflect.code.UnaryOperatorKind
import spoon.kotlin.reflect.code.KtBinaryOperatorKind as KtOp
import spoon.reflect.declaration.CtModule
import spoon.reflect.declaration.CtType
import spoon.reflect.factory.Factory


internal class FirTreeBuilderHelper(private val firTreeBuilder: FirTreeBuilder) {

    fun createType(firClass: FirRegularClass): CtType<*> {
        val type: CtType<Any> = when (firClass.classKind) {
            ClassKind.CLASS -> firTreeBuilder.factory.Core().createClass<Any>()
            ClassKind.INTERFACE -> firTreeBuilder.factory.Core().createInterface()
            ClassKind.ENUM_CLASS -> firTreeBuilder.factory.Core().createEnum<Enum<*>>() as CtType<Any>
            ClassKind.ENUM_ENTRY -> TODO()
            ClassKind.ANNOTATION_CLASS -> TODO()
            ClassKind.OBJECT -> TODO()
        }
        type.setSimpleName<CtType<*>>(firClass.name.identifier)

        firTreeBuilder.addModifiersAsMetadata(type, KtModifierKind.fromClass(firClass))

        firClass.superConeTypes.forEach {
            firTreeBuilder.referenceBuilder.buildTypeReference<Any>(it).apply {
                val symbol = it.lookupTag.toSymbol(firClass.session)?.fir
                if (symbol != null && symbol is FirRegularClass) {
                    when (symbol.classKind) {
                        ClassKind.CLASS -> {
                            type.setSuperclass<CtType<Any>>(this)
                        }
                        ClassKind.INTERFACE -> {
                            type.addSuperInterface<Any, CtType<Any>>(this)
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

        }
        return type
    }

    fun getOrCreateModule(session: FirSession, factory: Factory): CtModule {
        val mname = session.moduleInfo?.name?.asString() ?: return factory.Module().unnamedModule
        return factory.Module().unnamedModule
        // return factory.Module().getOrCreate(mname)
    }

    private fun getReceiver(qa: FirQualifiedAccessExpression): FirExpression? {
        val explicitReceiver = qa.explicitReceiver
        val dispatchReceiver = qa.dispatchReceiver
        return if (explicitReceiver == null || explicitReceiver == FirNoReceiverExpression) {
            if (dispatchReceiver == FirNoReceiverExpression) null
            else dispatchReceiver
        } else {
            explicitReceiver
        }
    }

    private val binaryTokenSet = setOf(
        IN_KEYWORD, NOT_IN, RANGE, MUL, PLUS, MINUS, DIV, PERC, // These are always function calls
        MULTEQ, DIVEQ, PERCEQ, PLUSEQ, MINUSEQ // These can be in variable assignment or function call depending on existence of opAssign
    )
    private val unaryTokenSet = setOf(PLUS, MINUS, EXCL)

    /*
     unhandled tokens: // Should be handled in other visits than function call

        IS_KEYWORD, NOT_IS, AS_KEYWORD, AS_SAFE, // TypeOperatorCall
        ANDAND, OROR, // BinaryLogicExpression
        PLUSPLUS, MINUSMINUS, // Handled in blocks (code already generated: { a++ => a0 = a; a0.inc(); a0})
        EQEQ, EXCLEQ, EXCLEXCL, EQEQEQ, EXCLEQEQEQ, LT, GT, LTEQ, GTEQ, // OperatorCall
        ELVIS,  // Handled in WhenExpression
        DOT, EQ, SAFE_ACCESS // N/A, safe access is handled in normal function call logic

     */
    fun resolveIfOperatorOrInvocation(firCall: FirFunctionCall): InvocationType {
        val source = firCall.source?.psi
        val receiver = getReceiver(firCall)

        val opToken = when (source) {
            is KtBinaryExpression -> source.operationToken
            is KtPrefixExpression -> source.operationToken
            is KtPostfixExpression -> source.operationToken
            else -> return InvocationType.NORMAL_CALL(receiver, firCall)
        }
        // Now in binary or unary call

        if (receiver == null) throw SpoonException("Infix/operator call without receiver")

        return when (opToken) {
            IDENTIFIER -> InvocationType.INFIX_CALL(receiver, firCall, firCall.arguments[0])
            in binaryTokenSet -> orderBinaryOperands(opToken, receiver, firCall)
            in unaryTokenSet -> InvocationType.PREFIX_OPERATOR(tokenToUnaryOperatorKind(opToken), receiver, firCall)
            else -> throw SpoonException("Unexpected operator for function call $opToken")
        }
    }

    private fun orderBinaryOperands(
        token: IElementType,
        receiver: FirExpression,
        call: FirFunctionCall
    ): InvocationType {
        return when (token) {
            MUL, PLUS, MINUS, DIV, PERC, RANGE ->
                InvocationType.BINARY_OPERATOR(receiver, tokenToBinaryOperatorKind(token), call.arguments[0], call)
            MULTEQ, DIVEQ, PERCEQ, PLUSEQ, MINUSEQ ->
                InvocationType.ASSIGNMENT_OPERATOR(receiver, tokenToAssignmentOperatorKind(token), call.arguments[0], call)
            IN_KEYWORD -> InvocationType.BINARY_OPERATOR( // Reversed operand order
                call.arguments[0],
                tokenToBinaryOperatorKind(token),
                receiver,
                call
            )
            NOT_IN -> {
                val inExpression = receiver as? FirFunctionCall ?: throw RuntimeException("Unable to get 'contains' subtree of !in operator, $receiver")
                val inReceiver = getReceiver(inExpression) as FirExpression
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
}