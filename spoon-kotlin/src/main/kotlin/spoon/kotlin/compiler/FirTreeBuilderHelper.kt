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

    fun resolveIfOperatorOrInvocation(firCall: FirFunctionCall): InvocationType {
        val source = firCall.source?.psi
        val receiver = getReceiver(firCall)

        return when (source) {
            is KtBinaryExpression -> {
                val opToken = source.operationToken
                if (receiver == null) throw SpoonException("Infix operator/function call without receiver")
                if (opToken == IDENTIFIER) InvocationType.INFIX_CALL(receiver, firCall, firCall.arguments[0])
                else orderBinaryOperands(opToken, receiver, firCall)
            }
            is KtPrefixExpression -> {
                val opToken = source.operationToken
                if (receiver == null) throw SpoonException("Prefix operator without receiver")
                InvocationType.PREFIX_OPERATOR(tokenToUnaryOperatorKind(opToken), receiver, firCall)
            }
            is KtPostfixExpression -> {
                val opToken = source.operationToken
                if (receiver == null) throw SpoonException("Postfix operator without receiver")
                InvocationType.PREFIX_OPERATOR(tokenToUnaryOperatorKind(opToken), receiver, firCall)
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