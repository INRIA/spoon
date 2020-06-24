package spoon.test.field.testclasses

abstract class Properties {
    public val f0 = 0
    protected var f1: Long = 1
    private val f2 = "private property"
    internal open var f3 = 3f
    abstract val f4: Double
    lateinit var f5: String
}