package spoon.test.`when`.testclasses

class SimpleWhen {
    var x = 0
    val l = listOf(1,2,3)

    fun whenStatement() {
        when(x) {
            1,2 -> {}
            in l, is Number -> {}
            !in l, !is Number -> {}
        }
    }

    fun whenExpression() {
        val whenResult = when(x) {
            1,2 -> 3
            else -> {
                99
            }
        }
    }

    fun whenWithoutSubject() {
        when {
            x < 10 || x == 14 -> {}
            else -> {}
        }
    }

    fun emptyWhen() {
        when {

        }
    }

    fun emptyWhenWithLocalSubject() {
        when(val x = 0) {

        }
    }

    fun nestedWhens() {
        when(val outer = 0) {
            1 -> when(val inner = "asd") {
                is String, in listOf(""), outer.toString() -> {}
            }
        }
    }

    fun whenWithBooleanSubject() {
        when(val b = true) {
            in listOf(false), b in listOf(false) || b is Any, is Any -> {}
        }
    }
}