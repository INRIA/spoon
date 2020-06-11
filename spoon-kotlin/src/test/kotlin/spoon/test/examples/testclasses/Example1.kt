package spoon.test.examples.testclasses

abstract class Example1 internal constructor(val n : Int) : Base(9) {
    val s: String = "s"
    val i = if(true) 1 else { 0xabc123 }

    constructor() : this(4)

    fun aFunction(n: Int): Long {
        return if(false) 0b1101 else 3L
    }
}

open class Base(val b: Int)