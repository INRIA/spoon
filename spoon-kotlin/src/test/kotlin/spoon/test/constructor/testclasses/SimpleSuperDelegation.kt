package spoon.test.constructor.testclasses

class SimpleSuperDelegation : SimpleSuperDelegationBase {
    constructor() : super()
    constructor(s: String) : super()
}

open class SimpleSuperDelegationBase()