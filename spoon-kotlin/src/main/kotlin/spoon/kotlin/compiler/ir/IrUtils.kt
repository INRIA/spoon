package spoon.kotlin.compiler.ir

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
        fun wrap(b: Boolean): KtMetadata<Boolean> = KtMetadata(b)
        fun wrap(c: CtElement): KtMetadata<CtElement> = KtMetadata(c)
        fun wrap(s: String): KtMetadata<String> = KtMetadata(s)
        fun wrap(kind: KtBinaryOperatorKind): KtMetadata<KtBinaryOperatorKind> = KtMetadata(kind)
        fun wrap(modifiers: List<KtModifierKind>): KtMetadata<Set<KtModifierKind>> = wrap(modifiers.toMutableSet())
        fun wrap(modifiers: MutableSet<KtModifierKind>): KtMetadata<Set<KtModifierKind>> = KtMetadata(modifiers)
    }
}

internal fun <T: CtElement> T.putKtMetadata(s: String, d: KtMetadata<*>) {
    putMetadata<CtElement>(s, d.value)
}