package spoon.kotlin.reflect.visitor.printing

import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.kotlin.reflect.KtModifierKind
import spoon.reflect.declaration.CtFormalTypeDeclarer
import spoon.reflect.reference.CtIntersectionTypeReference
import spoon.reflect.reference.CtTypeReference

/**
 * Handles that FormalTypeDeclarers have a type parameter list in one place, and potentially a where clause in another
 * place. Ex:
 * fun <S, T : SomeClass> funName() where S: X, S: Y {}
 * class name<S>(..): SomeSuperclass, SomeInterface where S: X, S: Y {}
 *
 * Depending on the declarer, the list and where clause may be in different places.
 * Use by calling #generateTypeParamList for the first position, and #generateWhereClause in the second.
 *
 * Any type parameter with more than one superclass will have its superclasses in the where clause
 * Those with 1 superclass will have it in the first list unless #allBoundsInWhereClause is set.
 */

internal abstract class TypeParameterHandler {
    abstract fun generateTypeParamList(): List<String>
    abstract fun generateTypeParamListString(): String
    abstract fun generateWhereClauseString(): String
    abstract fun generateWhereClause(): List<String>
    abstract val isEmpty: Boolean
    companion object {
        fun create(ctFormalTypeDeclarer: CtFormalTypeDeclarer?,
                   prettyPrinter: DefaultKotlinPrettyPrinter,
                   allBoundsInWhereClause: Boolean = false
        ): TypeParameterHandler {
            if(ctFormalTypeDeclarer == null) return EmptyTypeParamHandler
            return TypeParameterHandlerImpl(ctFormalTypeDeclarer, prettyPrinter, allBoundsInWhereClause)
        }
    }
}

internal class TypeParameterHandlerImpl(
    private val entity: CtFormalTypeDeclarer,
    private val prettyPrinter: DefaultKotlinPrettyPrinter,
    allBoundsInWhereClause: Boolean = false
) : TypeParameterHandler() {
    private val printBoundInWhereClause: List<Boolean> = if(allBoundsInWhereClause) {
        entity.formalCtTypeParameters.map { it.superclass != null }
    } else {
        entity.formalCtTypeParameters.map { it.superclass is CtIntersectionTypeReference<*> }
    }
    private val hasWhereClause = printBoundInWhereClause.any { it }
    override val isEmpty get() = entity.formalCtTypeParameters.isEmpty()

    override fun generateTypeParamList(): List<String> {
        if(entity.formalCtTypeParameters.isEmpty()) return emptyList()
        val paramList = ArrayList<String>()

        for((i,typeParam) in entity.formalCtTypeParameters.withIndex()) {
            val modifiers = typeParam.getMetadata(KtMetadataKeys.KT_MODIFIERS) as? Set<KtModifierKind>?
            val modifiersString = if(modifiers != null && modifiers.isNotEmpty()) {
                modifiers.toList().sorted().joinToString(separator = " ", postfix = " ", transform = { it.token })
            } else {
                ""
            }

            val s  = if(typeParam.superclass != null && !printBoundInWhereClause[i]) {
                "${modifiersString}${typeParam.simpleName}: ${prettyPrinter.printElement(typeParam.superclass)}"
            } else {
                "${modifiersString}${typeParam.simpleName}"
            }
            paramList.add(s)
        }
        return paramList
    }

    override fun generateTypeParamListString(): String {
        if(entity.formalCtTypeParameters.isEmpty()) return ""
        return generateTypeParamList().joinToString(prefix = " <", separator = ", ", postfix = ">")
    }


    override fun generateWhereClauseString(): String {
        if(!hasWhereClause || entity.formalCtTypeParameters.isEmpty()) return ""
        return generateWhereClause().joinToString(prefix = " where ", separator = ", ")
    }

    override fun generateWhereClause(): List<String> {
        if(!hasWhereClause || entity.formalCtTypeParameters.isEmpty()) return emptyList()

        val params = ArrayList<String>()

        for((i,tParam) in entity.formalCtTypeParameters.withIndex()) {
            if(printBoundInWhereClause[i])
                addUpperBound(tParam.simpleName, tParam.superclass, params)
        }
        return params
    }

    private fun addUpperBound(name: String, boundRef: CtTypeReference<*>, paramList: ArrayList<String>) {
        if(boundRef is CtIntersectionTypeReference<*>) {
            if(boundRef.bounds.isEmpty()) return
            boundRef.bounds.forEach { addUpperBound(name, it, paramList) }
        } else {
            paramList.add("$name: ${prettyPrinter.printElement(boundRef)}")
        }
    }
}

internal object EmptyTypeParamHandler : TypeParameterHandler() {
    override val isEmpty: Boolean = true
    override fun generateTypeParamList(): List<String> = emptyList()

    override fun generateTypeParamListString(): String = ""

    override fun generateWhereClauseString(): String = ""

    override fun generateWhereClause(): List<String> = emptyList()
}
