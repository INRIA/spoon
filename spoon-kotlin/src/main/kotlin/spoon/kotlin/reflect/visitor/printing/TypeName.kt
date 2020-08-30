package spoon.kotlin.reflect.visitor.printing

import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.reflect.reference.CtTypeReference
import spoon.reflect.reference.CtWildcardReference

/**
 * Convenience class for handling type names.
 */
internal data class TypeName(val packageName: String, val simpleName: String, val suffix: String) {
    private fun asFQString(ignoreNullability: Boolean): String {
        val name = if(packageName.isEmpty()) simpleName else "$packageName.$simpleName"
        return if(ignoreNullability) { name } else { "$name${suffix}" }
    }

    private fun asSimpleString(ignoreNullability: Boolean): String {
        return if(ignoreNullability) { simpleName } else { "${simpleName}${suffix}" }
    }

    val simpleNameWithNullability get() = asSimpleString(false)
    val simpleNameWithoutNullability get() = asSimpleString(true)
    val fQNameWithoutNullability get()= asFQString(true)
    val fQNameWithNullability get() = asFQString(false)

    override fun toString(): String {
        return fQNameWithNullability
    }

    companion object {
        fun build(type: CtTypeReference<*>): TypeName {
            val prefix = type.`package`?.qualifiedName ?: ""
            val nullable = type.getMetadata(KtMetadataKeys.TYPE_REF_NULLABLE) as? Boolean? ?: false
            val suffix = if(nullable) "?" else ""
            val simpleName = if(type is CtWildcardReference) "*" else type.simpleName.dropWhile { it.isDigit() }
            return TypeName(prefix, simpleName, suffix)
        }
    }
}