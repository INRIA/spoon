package spoon.test.invocation.testclasses

class Invoker {
    val classWithInvokeOperator = ClassWithInvokeOperator()
    fun invoke(): ClassWithInvokeOperator = classWithInvokeOperator
    fun m() {
        this.invoke()
        invoke()
        classWithInvokeOperator()
        classWithInvokeOperator(1)
        classWithInvokeOperator.invoke()
        classWithInvokeOperator.invoke(2)
    }

    fun edgeCases() {
        invoke()()
        val invoke = classWithInvokeOperator
        invoke()
        invoke(1)
        invoke.invoke()
        // (fun (): ClassWithInvokeOperator { return ClassWithInvokeOperator() })() // Crashes compiler (1.3.72)
    }
}

class ClassWithInvokeOperator {
    operator fun invoke() {}

    operator fun invoke(i: Int) {}

    fun m() {
        this()
        this(1)
        this.invoke()
        this.invoke(2)
    }
}