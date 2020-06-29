package spoon.test.invocation.testclasses

class Foo {
    fun f1() {

    }
    fun f2(i: Int): Bar { return Bar() }

    fun foo() {
        topLevel()
    }

}

class Bar {
    val foo = Foo()
    fun bar() {
        bar()
        foo.f1()
        foo.f2(1)
        foo.f2(2).foo.foo()
    }
}

fun topLevel() {}