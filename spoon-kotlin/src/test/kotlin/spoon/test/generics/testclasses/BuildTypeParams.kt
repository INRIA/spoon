package spoon.test.generics.testclasses

class BuildTypeParams<T>(a: T) {
    val t: T = a

    fun returnT(): T {
        return t
    }

    fun <S: Comparable<T>> m2(): Variances<Int,T> {
        return Variances<Int,T>()
    }

    fun <W,R : Comparable<Variances<W,*>>> m() {

    }
}


