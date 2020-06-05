package spoon.test.examples.testclasses

class Example2(val n : Int, val i: AnInterface) : Base2() {
    val b = Base2()
    var q = 0
    var y = 0

    fun f(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    fun aFunction(q: Int?) : Int? {
        this.q = 1
        i.ival = 2
        b.x = 3
        x = 4
        super.x = 5
        f(x,2)
        super.bfunc("hello")
        return null
    }
}

interface AnInterface {
    var ival: Int
    val another: AnInterface
    fun hej()
}

open class Base2 {
    var x = 2
    fun bfunc(n: String) = n
}