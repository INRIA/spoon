package spoon.test.invocation.testclasses

class NamedArgs {
    fun m(i: Int, k: String) {}
    fun m2() {
        m(1,"1")
        m(i = 2, k = "2")
        m(k = "3", i = 3)
        m(4, k="4")
    }
}