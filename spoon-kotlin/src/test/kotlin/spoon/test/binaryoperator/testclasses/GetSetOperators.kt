package spoon.test.binaryoperator.testclasses

class GetSetOperators {
    val l = mutableListOf("1","2","3")
    val list = MultiParams()

    fun m() {
        val l0 = l[0]
        l[1] = l[2]
    }

    fun multiParam() {
        var i = 0
        i = list.get(0,"1")
        list.set(1,"2",15)

        i = list[2,"3"]
        list[3,"4"] = 30
    }
}

class MultiParams {
    operator fun get(i: Int, j: String): Int {
        return 0
    }

    operator fun set(i: Int, j: String, rhs: Int) {

    }
}