package spoon.test.function.testclasses

class LocalFunctions {
    fun outer() {
        fun inner1() {}
        fun inner2(i: Int) {}
    }
}