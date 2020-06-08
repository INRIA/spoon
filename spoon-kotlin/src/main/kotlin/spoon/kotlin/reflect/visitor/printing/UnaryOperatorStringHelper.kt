package spoon.kotlin.reflect.visitor.printing

import spoon.SpoonException
import spoon.reflect.code.UnaryOperatorKind

object UnaryOperatorStringHelper {
    fun asToken(op: UnaryOperatorKind) = when(op) {
        UnaryOperatorKind.POS -> "+"
        UnaryOperatorKind.NEG -> "-"
        UnaryOperatorKind.NOT -> "!"
        UnaryOperatorKind.COMPL -> throw SpoonException("'~' is not a Kotlin operator")
        UnaryOperatorKind.PREINC, UnaryOperatorKind.POSTINC -> "++"
        UnaryOperatorKind.PREDEC, UnaryOperatorKind.POSTDEC -> "--"
    }
}