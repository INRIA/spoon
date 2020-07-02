package spoon.test.generics.testclasses

class TypeArguments {
    fun <T> m() {
        val constr = ArrayList<T>()
        val invocation = emptyList<String>()
        val variableType: ArrayList<Boolean>
    }
}