package spoon.test.invocation.testclasses

class NullCheck {
    val nullable: Int? = null

    fun safeCall() {
        nullable?.compareTo(1)
    }

    fun assertNotNull() {
        nullable!!.compareTo(2)
    }
}