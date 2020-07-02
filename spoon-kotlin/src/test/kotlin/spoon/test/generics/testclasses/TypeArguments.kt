package spoon.test.generics.testclasses

class TypeArguments {
    val implicit = listOf(1,2,3)
    val explicit = listOf<Int>(4,5,6)

    fun <T> m() {
        val constr = ArrayList<T>()
        val invocation = emptyList<String>()
        val variableType: ArrayList<Boolean>
    }
}