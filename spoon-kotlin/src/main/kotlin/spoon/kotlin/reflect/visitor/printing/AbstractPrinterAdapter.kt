package spoon.kotlin.reflect.visitor.printing

abstract class AbstractPrinterAdapter(
        private val lineSeparator : String = System.getProperty("line.separator")
) {

    abstract infix fun write(c : Char) : AbstractPrinterAdapter

    var line = 1
        protected set

    var column = 1
        protected set

    open infix fun write(s : String) : AbstractPrinterAdapter {
        s.forEach { write(it) }
        return this
    }

    open infix fun writeSeparator(s: String) = write(" $s ")

    open fun newline() : AbstractPrinterAdapter = write(lineSeparator)

    open infix fun writeln(s : String) : AbstractPrinterAdapter {
        write(s)
        newline()
        return this
    }

    open infix fun writeln(c : Char) : AbstractPrinterAdapter {
        write(c)
        newline()
        return this
    }

    infix fun and(s : String) = write(s)
    infix fun and(c : Char) = write(c)

    abstract override fun toString() : String

}