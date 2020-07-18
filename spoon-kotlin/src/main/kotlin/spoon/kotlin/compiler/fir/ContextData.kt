package spoon.kotlin.compiler.fir

import org.jetbrains.kotlin.fir.expressions.FirExpression

sealed class ContextData

data class For(val iterable: FirExpression): ContextData()
data class Destruct(val destructTarget: FirExpression): ContextData()


