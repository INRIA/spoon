package spoon.kotlin.reflect.visitor.printing

import spoon.kotlin.reflect.KtModifierKind

open class DefaultPrinterAdapter(
        private val ignoredModifiers : List<KtModifierKind> = listOf(
            KtModifierKind.PUBLIC, KtModifierKind.FINAL),
        open val LINE_SEPARATOR : String = System.getProperty("line.separator")
) : AbstractPrinterAdapter(LINE_SEPARATOR) {

    /**
    * Declaration type: fun foo(): Int, var bar: String
    * Of supertype: class A : B, fun <T : Number> foo()
    * Constructor delegation: constructor(x: Int) : this(x)
    * Object declaration: val o = object : T
    */
    enum class ColonContext { DECLARATION_TYPE, OF_SUPERTYPE, CONSTRUCTOR_DELEGATION, OBJECT_DECLARATION }

    val KT_FILE_EXTENSION = ".kt"
    val KDOC_START = "/**"
    val BLOCK_COMMENT_START = "/* "
    val BLOCK_COMMENT_END = "*/"
    val INLINE_COMMENT_START = "// "
    open val FQ_NAME_SEPARATOR = "."

    open val INDENT_UNIT = "    " // 4 spaces
    var indentCount = 0

    var onNewLine = true
        private set

    private val sb : StringBuilder = StringBuilder()

    private var lastCharWasCR = false
    private var verticalSpace = 0
    private var enforceSpace = false

    fun reset() {
        sb.clear()
        onNewLine = true
        lastCharWasCR = false
        verticalSpace = 0
        indentCount = 0
        column = 1
        line = 1
    }

    override infix fun write(c: Char) : DefaultPrinterAdapter {
        when(c) {
            '\r' -> {
                lastCharWasCR = true
                line++
                verticalSpace++
                column = 1
                onNewLine = true
            }
            '\n' -> {
                if(!lastCharWasCR) {
                    column = 1
                    line++
                    verticalSpace++
                    onNewLine = true
                }
                lastCharWasCR = false
            }
            else -> {
                lastCharWasCR = false
                if(onNewLine) {
                    onNewLine = false
                    verticalSpace = 0
                    writeIndent()
                } else {
                    if(enforceSpace) sb.append(' ')
                    column++
                }
            }
        }
        enforceSpace = false
        sb.append(c)
        return this
    }

    /**
     * Ensures that there are n empty lines (or equivalently, that there are n+1 linebreaks) since the last written character.
     * Does nothing if the adapter is on the first character. (Line = 1, Column = 1)
     *
     * n = 0 ensures that the adapter is on a new line.
     */
    fun ensureNEmptyLines(n: Int) {
        if(!(line == 1 && column == 1)) { // No vertical space for the first line
            while (verticalSpace < n+1) write(LINE_SEPARATOR)
        }
    }

    fun ensureSpaceOrNewlineBeforeNext() {
        enforceSpace = true
    }

    private fun writeIndent() = repeat(indentCount) { write(INDENT_UNIT) }

    override fun toString(): String = sb.toString()

    fun pushIndent() = indentCount++
    fun popIndent() = indentCount--

    inline fun withIndentDiff(indentDiff : Int = 1, action : (DefaultPrinterAdapter) -> Unit) {
        indentCount += indentDiff
        action(this)
        indentCount -= indentDiff
    }

    inline fun withIndent(indent : Int, action : (DefaultPrinterAdapter) -> Unit) {
        val oldIndentCount = indentCount
        indentCount = indent
        action(this)
        indentCount = oldIndentCount
    }

    fun writeAligned(strings: List<String>, from: Int = column, breakPoint: Int = 80) {
        if(strings.isEmpty()) return
        var commas = strings.size - 1
        var overrideBreakPoint = false
        for(s in strings) {
            if(column + s.length < breakPoint || overrideBreakPoint) {
                overrideBreakPoint = false
                write(s)
            }
            else {
                newline()
                overrideBreakPoint = true
                while(column < from) write(' ')
                write(s)
            }
            if(commas-- > 0) write(", ")
        }
    }

    /**
     * Writes a colon with or without a leading whitespace depending on the context.
     *
     * Declaration type: fun foo(): Int, var bar: String
     * Of supertype: class A : B, fun <T : Number> foo()
     * Constructor delegation: constructor(x: Int) : this(x)
     * Object declaration: val o = object : T
     */

    fun writeColon(context : ColonContext) = write(when(context) {
        ColonContext.DECLARATION_TYPE -> ": "
        else -> " : "
    })

    infix fun writeModifiers(modifierSet : Set<KtModifierKind>?) : DefaultPrinterAdapter = writeModifiers(modifierSet?.toList())

    infix fun writeModifiers(modifierList : List<KtModifierKind>?) : DefaultPrinterAdapter = this.apply {
        modifierList?.filterNot { it in ignoredModifiers }?.sorted()?.nullOrNotEmpty()?.
            joinToString(separator = " ", postfix = " ") { it.token }?.let { this.write(it) }
    }

    infix fun writeIdentifier(name: String): DefaultPrinterAdapter {
        if(name.isEmpty()) return this
        if(name.length < 3) {
            write(name)
            return this
        }
        if(name.startsWith('$') && (name.endsWith('$') || name.endsWith("$?"))) {
            write('`')
            if(name.endsWith('?')) {
                write(name.substring(1, name.length-2))
                write("`?")
            } else {
                write(name.substring(1, name.length-1))
                write('`')
            }
        } else {
            write(name)
        }
        return this
    }

    private fun <T> List<T>.nullOrNotEmpty() = if(this.isEmpty()) null else this
}
