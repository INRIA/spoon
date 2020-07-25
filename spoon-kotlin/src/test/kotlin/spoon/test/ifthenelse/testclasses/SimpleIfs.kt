package spoon.test.ifthenelse.testclasses

class SimpleIfs {
    val x = 2
    val y = 3
    fun m() {
        if(x == y) println(x) else println(y)
        if(x == y) println(x)
        val ifExpr = if(x == 2) "Yes" else "No"
        val ifExpr2 = if(2 == x) { 2 } else 3
    }
}