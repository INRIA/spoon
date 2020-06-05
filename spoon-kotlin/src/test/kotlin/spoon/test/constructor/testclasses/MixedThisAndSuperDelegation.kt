package spoon.test.constructor.testclasses

class MixedThisAndSuperDelegation : MixedThisAndSuperDelegationBase {
    constructor() : super()
    constructor(s: String) : super(s)
    constructor(n: Int) : this(n.toString())
    constructor(n: Byte) : this()
    constructor(n: Short) : super()
}

open class MixedThisAndSuperDelegationBase() {
    constructor(s: String) : this()
}