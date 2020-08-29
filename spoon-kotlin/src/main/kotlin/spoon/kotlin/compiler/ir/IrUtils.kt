package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.builtins.functions.FunctionInvokeDescriptor
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.kotlin.reflect.KtModifierKind
import spoon.kotlin.reflect.code.KtBinaryOperatorKind
import spoon.reflect.declaration.CtElement

fun CtElement.addModifiersAsMetadata(modifierList: List<KtModifierKind>) {
    putMetadata<CtElement>(KtMetadataKeys.KT_MODIFIERS, modifierList.toMutableSet())
}


internal class KtMetadata<T> private constructor(val value: T) {
    // Used for type safety when putting metadata, Kt metadata is any of the wrapped types
    companion object {
        fun bool(b: Boolean): KtMetadata<Boolean> = KtMetadata(b)
        fun element(c: CtElement): KtMetadata<CtElement> = KtMetadata(c)
        fun string(s: String): KtMetadata<String> = KtMetadata(s)
        fun binOpKind(kind: KtBinaryOperatorKind): KtMetadata<KtBinaryOperatorKind> = KtMetadata(kind)
        fun modifierKind(modifiers: List<KtModifierKind>): KtMetadata<Set<KtModifierKind>> = modifierKind(modifiers.toMutableSet())
        fun modifierKind(modifiers: MutableSet<KtModifierKind>): KtMetadata<Set<KtModifierKind>> = KtMetadata(modifiers)
        fun elementList(list: List<CtElement>) = KtMetadata(list)
    }
}

internal fun <T: CtElement> T.putKtMetadata(s: String, d: KtMetadata<*>) {
    putMetadata<CtElement>(s, d.value)
}
