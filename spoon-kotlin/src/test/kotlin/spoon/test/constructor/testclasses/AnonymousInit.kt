package spoon.test.constructor.testclasses

class AnonymousInit(param: Int) {
    private var p: Int

    init {
        val i1 = "init1"
        p = param
    }

    val p2 = 2

    init {
        val i2 = "init2"
        p = 0
    }

    init {

    }
}