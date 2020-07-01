package spoon.kotlin.reflect

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.utils.addIfNotNull


// Order matters! Ordinal is used for sorting
enum class KtModifierKind(val token : String) {
    // # Visibility

    PRIVATE("private"),

    PROTECTED("protected"),

    INTERNAL("internal"),

    PUBLIC("public"),

    // # Modality

    FINAL("final"),

    OPEN("open"),

    ABSTRACT("abstract"),

    SEALED("sealed"),

    CONST("const"), // Top-level property

    // # Extended declaration

    OVERRIDE("override"),

    LATEINIT("lateinit"),

    TAILREC("tailrec"),

    VARARG("vararg"),

    SUSPEND("suspend"),

    INNER("inner"),

    // Enum

    ANNOTATION("annotation"),

    COMPANION("companion"),

    INLINE("inline"),

    INFIX("infix"),

    OPERATOR("operator"),

    DATA("data"),


    // # Variable Mutability

    VAR("var"),

    VAL("val"),

    // # Value parameter
    NOINLINE("noinline"),

    CROSSINLINE("crossinline"),

    // # Type projection

    TYPE_PROJECTION_IN("in"),

    TYPE_PROJECTION_OUT("out"),

    STAR_PROJECTION("*"),

    // # Type param
    REIFIED("reified");

    internal companion object {
        fun fromClass(c : FirRegularClass) = ArrayList<KtModifierKind>().apply {
            addIfNotNull(convertVisibility(c.visibility))
            addIfNotNull(convertModality(c.modality))
            if(c.isData)        add(DATA)
            if(c.isCompanion)   add(COMPANION)
            if(c.isInline)      add(INLINE)
            if(c.isInner)       add(INNER)
            if(c.classKind == ClassKind.ANNOTATION_CLASS) add(ANNOTATION)
            sort()
        }

        fun fromFunctionDeclaration(f : FirMemberDeclaration) = ArrayList<KtModifierKind>().apply {
            addIfNotNull(convertVisibility(f.visibility))
            addIfNotNull(convertModality(f.modality))
            if(f.isInfix)       add(INFIX)
            if(f.isInline)      add(INLINE)
            if(f.isOperator)    add(OPERATOR)
            if(f.isOverride)    add(OVERRIDE)
            if(f.isSuspend)     add(SUSPEND)
            if(f.isTailRec)     add(TAILREC)
            sort()
        }

        fun fromProperty(p : FirProperty) = ArrayList<KtModifierKind>().apply {
            addIfNotNull(convertVisibility(p.visibility))
            addIfNotNull(convertModality(p.modality))
            if(p.isOverride)    add(OVERRIDE)
            if(p.isConst)       add(CONST)
            if(p.isLateInit)    add(LATEINIT)
            if(p.isVal)         add(VAL)
            if(p.isVar)         add(VAR)
            sort()
        }

        fun fromValueParameter(valueParameter: FirValueParameter) : List<KtModifierKind> =
            ArrayList<KtModifierKind>().apply {
                if(valueParameter.isVararg)         add(VARARG)
                if(valueParameter.isNoinline)       add(NOINLINE)
                if(valueParameter.isCrossinline)    add(CROSSINLINE)
                // Val and var are not part of value param grammar
                sort()
            }


        fun fromTypeVariable(typeParameter: FirTypeParameter) : List<KtModifierKind> =
            ArrayList<KtModifierKind>().apply {
                if(typeParameter.isReified) add(REIFIED)
                when(typeParameter.variance) {
                    Variance.IN_VARIANCE -> add(TYPE_PROJECTION_IN)
                    Variance.OUT_VARIANCE -> add(TYPE_PROJECTION_OUT)
                    Variance.INVARIANT -> { /*Nothing*/ }
                }
            }

        fun convertModality(m : Modality?) : KtModifierKind? = when(m) {
            Modality.FINAL     -> FINAL
            Modality.SEALED    -> SEALED
            Modality.OPEN      -> OPEN
            Modality.ABSTRACT  -> ABSTRACT
            null -> null
        }

        fun convertVisibility(v : Visibility?) : KtModifierKind? = when (v?.internalDisplayName) {
            "private" -> PRIVATE
            "protected" -> PROTECTED
            "internal" -> INTERNAL
            "public" -> PUBLIC
            "local", "invisible_fake" -> null // Alternatively throw, this shouldn't be called with local variables
            "private/*private to this*/" -> PRIVATE
            else -> null
        }

        fun fromPsiModifierList(modifierList: KtModifierList): List<KtModifierKind> =
            ArrayList<KtModifierKind>().apply {
                modifierList.allChildren.forEach {
                    when (it.node.elementType) {
                        KtTokens.PRIVATE_KEYWORD -> add(PRIVATE)
                        KtTokens.PROTECTED_KEYWORD -> add(PROTECTED)
                        KtTokens.INTERNAL_KEYWORD -> add(INTERNAL)
                        KtTokens.PUBLIC_KEYWORD -> add(PUBLIC)
                        KtTokens.FINAL_KEYWORD -> add(FINAL)
                        KtTokens.OPEN_KEYWORD -> add(OPEN)
                        KtTokens.ABSTRACT_KEYWORD -> add(ABSTRACT)
                        KtTokens.SEALED_KEYWORD -> add(SEALED)
                        KtTokens.CONST_KEYWORD -> add(CONST)
                        KtTokens.OVERRIDE_KEYWORD -> add(OVERRIDE)
                        KtTokens.LATEINIT_KEYWORD -> add(LATEINIT)
                        KtTokens.TAILREC_KEYWORD -> add(TAILREC)
                        KtTokens.VARARG_KEYWORD -> add(VARARG)
                        KtTokens.SUSPEND_KEYWORD -> add(SUSPEND)
                        KtTokens.INNER_KEYWORD -> add(INNER)
                        // Enum
                        KtTokens.ANNOTATION_KEYWORD -> add(ANNOTATION)
                        KtTokens.COMPANION_KEYWORD -> add(COMPANION)
                        KtTokens.INLINE_KEYWORD -> add(INLINE)
                        KtTokens.INFIX_KEYWORD -> add(INFIX)
                        KtTokens.OPERATOR_KEYWORD -> add(OPERATOR)
                        KtTokens.DATA_KEYWORD -> add(DATA)
                        KtTokens.VAR_KEYWORD -> add(VAR)
                        KtTokens.VAL_KEYWORD -> add(VAL)
                        /* Type param/type arg list. Keep for when generics are implemented
                            KtTokens.NOINLINE_KEYWORD   -> add(NOINLINE)
                            KtTokens.CROSSINLINE_KEYWORD -> add(CROSSINLINE)
                            KtTokens.IN_KEYWORD         -> add(TYPE_PROJECTION_IN)
                            KtTokens.OUT_KEYWORD        -> add(TYPE_PROJECTION_OUT)
                            KtTokens.MUL                -> add(STAR_PROJECTION)
                            KtTokens.REIFIED_KEYWORD    -> add(REIFIED)
                        */
                    }
                }
            }
    }
}

    /*
    Ignored:

    EXTERNAL  // JS


    // Multiplatform

    EXPECT,

    ACTUAL
    */

