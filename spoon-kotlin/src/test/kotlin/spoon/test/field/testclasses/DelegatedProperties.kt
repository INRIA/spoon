package spoon.test.field.testclasses

class DelegatedProperties {
    val lazyInt by lazy { 123 }
    val lazyString by lazy(fun(): String { return "s" })

    fun f() {
        val x = lazyInt
    }
}