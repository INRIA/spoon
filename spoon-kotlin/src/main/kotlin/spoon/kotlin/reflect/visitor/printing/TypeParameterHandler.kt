package spoon.kotlin.reflect.visitor.printing

import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.kotlin.reflect.KtModifierKind
import spoon.reflect.declaration.CtFormalTypeDeclarer
import spoon.reflect.reference.CtIntersectionTypeReference
import spoon.reflect.reference.CtTypeReference

internal class TypeParameterHandler(
    private val entity: CtFormalTypeDeclarer,
    private val prettyPrinter: DefaultKotlinPrettyPrinter,
    allBoundsInWhereClause: Boolean = false) {
    private val printBoundInWhereClause: List<Boolean> = if(allBoundsInWhereClause) {
        entity.formalCtTypeParameters.map { it.superclass != null }
    } else {
        entity.formalCtTypeParameters.map { it.superclass is CtIntersectionTypeReference<*> }
    }
    private val hasWhereClause = printBoundInWhereClause.any { it }
    val isEmpty get() = entity.formalCtTypeParameters.isEmpty()

    fun generateTypeParamList(): List<String> {
        if(entity.formalCtTypeParameters.isEmpty()) return emptyList()
        val paramList = ArrayList<String>()

        for((i,tParam) in entity.formalCtTypeParameters.withIndex()) {
            val modifiers = tParam.getMetadata(KtMetadataKeys.KT_MODIFIERS) as? Set<KtModifierKind>?
            val modifiersString = modifiers?.toList()?.sorted()?.
                joinToString(separator = " ", postfix = " ", transform = { it.token }) ?: ""
            val s  = if(tParam.superclass != null && !printBoundInWhereClause[i]) {
                "${modifiersString}${tParam.simpleName}: ${prettyPrinter.printElement(tParam.superclass)}"
            } else {
                tParam.simpleName
            }
            paramList.add(s)
        }
        return paramList
    }

    fun generateTypeParamListString(): String {
        if(entity.formalCtTypeParameters.isEmpty()) return ""
        return generateTypeParamList().joinToString(prefix = " <", separator = ", ", postfix = ">")
    }


    fun generateWhereClauseString(): String {
        if(!hasWhereClause || entity.formalCtTypeParameters.isEmpty()) return ""
        return generateWhereClause().joinToString(prefix = " <", separator = ", ", postfix = ">")
    }

    fun generateWhereClause(): List<String> {
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