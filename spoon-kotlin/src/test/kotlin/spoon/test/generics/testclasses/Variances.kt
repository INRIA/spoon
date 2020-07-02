package spoon.test.generics.testclasses

class Variances<in T, out S> {
    inline fun <reified R> reified(r: R) {}

    fun star(a: Array<*>) {}
}