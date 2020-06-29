package spoon.kotlin.compiler

import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.expressions.impl.FirNoReceiverExpression
import org.jetbrains.kotlin.fir.psi
import org.jetbrains.kotlin.fir.references.FirReference
import org.jetbrains.kotlin.fir.references.FirResolvedNamedReference
import org.jetbrains.kotlin.fir.resolve.toSymbol
import org.jetbrains.kotlin.fir.symbols.AbstractFirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirVariableSymbol
import org.jetbrains.kotlin.lexer.KtTokens.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import spoon.SpoonException
import spoon.kotlin.reflect.KtModifierKind
import spoon.reflect.code.CtCatchVariable
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
            is KtArrayAccessExpression -> {
                val name = firCall.calleeReference.name.identifier
                if (receiver == null) throw SpoonException("Array access operator without receiver")
                when(name) {
                    "get" -> InvocationType.GET_OPERATOR(receiver, firCall.arguments, firCall)
                    "set" -> InvocationType.SET_OPERATOR(receiver, firCall.arguments.dropLast(1), firCall.arguments.last(), firCall)
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

    fun tokenToAssignmentOperatorKind(token: IElementType) = when (token) {
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
        val actualFunction = callee.resolvedSymbol as? FirNamedFunctionSymbol ?: return false
        val calledName = if(!callee.name.isSpecial) callee.name.identifier else return false
        if(actualFunction.fir.isOperator && actualFunction.fir.name.asString() == "invoke") {

            // Operator invoke is called, but we dont know if it's a() or a.invoke()
            if(calledName != "invoke") {
                // Easiest case "a()" has become "a.invoke()" during resolution
                return true
            } else {
                /*
                The receiver (e.g. variable holding a class with the invoke operator, or function) is named invoke.
                Ex.
                val invoke = ClassWithInvokeOperator()
                invoke()
                invoke.invoke()
                Tricky edge case, these 2, and other potential sequences, must be distinguished
                */
                // These can match some simple cases, but not when invoke calls are nested
                var psi = functionCall.psi
                while(psi != null && psi.parent != null &&
                    (psi.parent is KtCallExpression || psi.parent is KtQualifiedExpression)) {
                    psi = psi.parent
                }
                if(psi != null) {
                    val text = psi.text.replace("""\s|\n""".toRegex(),"")

                    if(text.matches("((.+[)][(].*[)]\\s*;?\\s*)|(this\\s*[(].*[)]\\s*;?))\$".toRegex()))
                        return true

                    if(text.matches(".+[.]invoke[(].*[)]\\s*;?\\s*\$".toRegex()))
                        return false

                    val receiver = getReceiver(functionCall)
                    if(receiver is FirQualifiedAccessExpression) {
                        when(getResolvedSymbolOrNull(receiver.calleeReference)) {
                            is FirPropertySymbol, is FirVariableSymbol<*> ->
                                if(text.matches("invoke[(].*[)]\\s*;?\\s*\$".toRegex())) return true
                        }
                    }

                }
            }
        }

        //Default to false
        return false
    }
}