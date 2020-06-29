package spoon.test.parameter.testclasses

class VarargParam {
    fun userTypeVararg(vararg p: A) {}
    fun stringVararg(vararg p: String) {}

    fun charVararg(vararg p: Char) {}
    fun booleanVararg(vararg p: Boolean) {}

    fun byteVararg(vararg p: Byte) {}
    fun shortVararg(vararg p: Short) {}
    fun intVararg(vararg p: Int) {}
    fun longVararg(vararg p: Long) {}
    fun floatVararg(vararg p: Float) {}
    fun doubleVararg(vararg p: Double) {}

    fun m(i: Int, vararg a: Char) {}
    fun m2(vararg i: A, a: Char) {}
}

class A {}