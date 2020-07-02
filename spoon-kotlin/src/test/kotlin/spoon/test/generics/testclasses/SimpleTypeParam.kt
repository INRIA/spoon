package spoon.test.generics.testclasses

class SimpleTypeParam<T>(ct: T) {
    val t1: T = ct
    val t2: T? = ct
    val nestedTypeArg = emptyList<Pair<*,Comparable<T>>>()

    fun <S> m(s: S): T {
        return t1
    }
}

