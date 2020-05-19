package spoon.kotlin.reflect.visitor.printing

import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.reflect.code.CtLiteral
import spoon.reflect.code.LiteralBase

internal object LiteralToStringHelper {
    private fun getBasedString(value : Number, base : LiteralBase) : String {
        var radix = 10
        val prefix = when(base) {
            LiteralBase.BINARY -> {
                radix = 2
                "0b"
            }
            LiteralBase.HEXADECIMAL -> {
                radix = 16
                "0x"
            }
            else -> ""
        }
        val basedValue : String
        val suffix : String
        when(value) {
            is Long ->  {
                basedValue = value.toString(radix)
                suffix = "L"
            }
            is Float -> {
                basedValue = value.toString()
                suffix = "F"
            }
            is Int -> {
                basedValue = value.toString(radix)
                suffix = ""
            }
            else -> {
                basedValue = value.toString()
                suffix = ""
            }
        }
        return "${prefix}${basedValue}${suffix}"
    }

    fun <T> getLiteralToken(literal : CtLiteral<T>): String = when(val value = literal.value) {
        null -> "null"
        is Number -> getBasedString(
            value,
            literal.base
        )
        is Char -> "'${getCharLiteral(value)}'"
        is String -> {
            val multiline = literal.getMetadata(KtMetadataKeys.STRING_LITERAL_MULTILINE) as Boolean? ?: false
            if(multiline) "\"\"\"${getStringLiteral(value)}\"\"\""
            else "\"${getStringLiteral(value)}\""
        }
        is Class<*> -> value.name
        else -> value.toString()
    }

    private fun getCharLiteral(c : Char) = when (c) {
        '\b' -> "\\b" //$NON-NLS-1$
        '\t' -> "\\t" //$NON-NLS-1$
        '\n' -> "\\n" //$NON-NLS-1$
        '\r' -> "\\r" //$NON-NLS-1$
        '\"' -> "\\\"" //$NON-NLS-1$
        '\'' -> "\\'" //$NON-NLS-1$
        '\\' -> "\\\\" //$NON-NLS-1$
        else -> if(Character.isISOControl(c)) String.format("\\u%04x", c.toInt()) else c.toString()
    }

    private fun getStringLiteral(value : String) = with(StringBuilder()) {
        value.forEach { c -> append(
            getCharLiteral(
                c
            )
        ) }
        toString()
    }
}
