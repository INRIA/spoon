package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.calls.components.isVararg
import org.jetbrains.kotlin.resolve.source.getPsi
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.utils.addIfNotNull
import spoon.kotlin.reflect.KtModifierKind

object IrToModifierKind {
    fun fromClass(c: IrClass) = ArrayList<KtModifierKind>().apply {
        addIfNotNull(convertVisibility(c.visibility))
        addIfNotNull(convertModality(c.modality))
        if(c.isData)        add(KtModifierKind.DATA)
        if(c.isCompanion)   add(KtModifierKind.COMPANION)
        if(c.isInline)      add(KtModifierKind.INLINE)
        if(c.isInner)       add(KtModifierKind.INNER)
        if(c.kind == ClassKind.ANNOTATION_CLASS) add(KtModifierKind.ANNOTATION)
        sort()
    }

    fun fromProperty(p: IrProperty) = ArrayList<KtModifierKind>().apply {
        addIfNotNull(convertVisibility(p.visibility))
        addIfNotNull(convertModality(p.modality))
        if(p.descriptor.overriddenDescriptors.isNotEmpty())
            add(KtModifierKind.OVERRIDE)
        if(p.isConst)       add(KtModifierKind.CONST)
        if(p.isLateinit)    add(KtModifierKind.LATEINIT)
        if(p.isVar)         add(KtModifierKind.VAR)
        else                add(KtModifierKind.VAL)
        sort()
    }

    fun fromValueParameter(valueParameter: IrValueParameter) : List<KtModifierKind> =
        ArrayList<KtModifierKind>().apply {
            if(valueParameter.descriptor.isVararg)  add(KtModifierKind.VARARG)
            if(valueParameter.isNoinline)           add(KtModifierKind.NOINLINE)
            if(valueParameter.isCrossinline)        add(KtModifierKind.CROSSINLINE)
            // Val and var are not part of value param grammar
            sort()
        }

    fun fromFunctionDeclaration(f : IrFunction) = ArrayList<KtModifierKind>().apply {
        val d = f.descriptor
        val source = f.descriptor.source.getPsi()
        addIfNotNull(convertVisibility(f.visibility))
        addIfNotNull(convertModality(f.descriptor.modality))
        if(d.isInfix) add(KtModifierKind.INFIX)
        if(d.isInline)      add(KtModifierKind.INLINE)
        if(source != null && source is KtNamedFunction) {
            if(source.hasModifier(KtTokens.OPERATOR_KEYWORD)) add(KtModifierKind.OPERATOR)
        }
        else if(d.isOperator) add(KtModifierKind.OPERATOR)
        if(d.overriddenDescriptors.isNotEmpty())
            add(KtModifierKind.OVERRIDE)
        if(d.isTailrec)     add(KtModifierKind.TAILREC)
        if(d.isSuspend)     add(KtModifierKind.SUSPEND)
        sort()
    }

    fun fromVariable(variable: IrVariable): List<KtModifierKind> =
        ArrayList<KtModifierKind>().apply {
            if(variable.isConst) add(KtModifierKind.CONST)
            if(variable.isLateinit) add(KtModifierKind.LATEINIT)
            if(variable.isVar) add(KtModifierKind.VAR)
            else add(KtModifierKind.VAL)
            sort()
        }

    fun fromTypeVariable(typeParameter: IrTypeParameter) : List<KtModifierKind> =
        ArrayList<KtModifierKind>().apply {
            if(typeParameter.isReified) add(KtModifierKind.REIFIED)
            when(typeParameter.variance) {
                Variance.IN_VARIANCE -> add(KtModifierKind.TYPE_PROJECTION_IN)
                Variance.OUT_VARIANCE -> add(KtModifierKind.TYPE_PROJECTION_OUT)
                Variance.INVARIANT -> { /*Nothing*/ }
            }
        }

    fun fromTypeVariable(typeParameter: TypeProjection) : List<KtModifierKind> =
        ArrayList<KtModifierKind>().apply {
            when(typeParameter.projectionKind) {
                Variance.IN_VARIANCE -> add(KtModifierKind.TYPE_PROJECTION_IN)
                Variance.OUT_VARIANCE -> add(KtModifierKind.TYPE_PROJECTION_OUT)
                Variance.INVARIANT -> { /*Nothing*/ }
            }
        }

    fun convertModality(m : Modality?) : KtModifierKind? = when(m) {
        Modality.FINAL     -> KtModifierKind.FINAL
        Modality.SEALED    -> KtModifierKind.SEALED
        Modality.OPEN      -> KtModifierKind.OPEN
        Modality.ABSTRACT  -> KtModifierKind.ABSTRACT
        null -> null
    }

    fun convertVisibility(v : Visibility?) : KtModifierKind? = when (v?.internalDisplayName) {
        "private" -> KtModifierKind.PRIVATE
        "protected" -> KtModifierKind.PROTECTED
        "internal" -> KtModifierKind.INTERNAL
        "public" -> KtModifierKind.PUBLIC
        "local", "invisible_fake" -> null // Alternatively throw, this shouldn't be called with local variables
        "private/*private to this*/" -> KtModifierKind.PRIVATE
        else -> null
    }
}