package spoon.kotlin.compiler.ir

import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.kotlin.reflect.KtModifierKind
import spoon.reflect.declaration.CtElement

fun CtElement.addModifiersAsMetadata(modifierList: List<KtModifierKind>) {
    putMetadata<CtElement>(KtMetadataKeys.KT_MODIFIERS, modifierList.toMutableSet())
}