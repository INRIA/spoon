package spoon.test.constructor.testclasses

class PrimaryAndSecondaryConstructors() {
    constructor(n: Int) : this()
    constructor(k: String) : this(1)
}