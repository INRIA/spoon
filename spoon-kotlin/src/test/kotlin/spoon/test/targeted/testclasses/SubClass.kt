package spoon.test.targeted.testclasses

open class SuperClass {
    open val x = 0
}

class SubClass : SuperClass() {
    override val x = 2
    val x2 = super.x

    override fun toString(): String {
        return super.toString()
    }
}