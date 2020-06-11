package spoon.test.binaryoperator.testclasses

class ElvisOperator {
    var b: Int? = null
    var b1 = b ?: 0
    var b2 = b ?: if(b == null) 1 else b1
}

