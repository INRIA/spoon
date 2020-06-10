package spoon.test.binaryoperator.testclasses

class TypeOperators {
    val x = Derived()
    val y = x as Base?
    val ySafe = x as? Base
    val z = x as Base as Derived
    val zSafe = x as? Base as? Derived
    val base: Base = x

    val i = x is Base
    val i2 = x !is Base?
}

open class Base
class Derived() : Base()