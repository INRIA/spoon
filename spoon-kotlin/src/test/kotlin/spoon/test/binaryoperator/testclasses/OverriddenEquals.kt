package spoon.test.binaryoperator.testclasses

class OverriddenOperatorEquals {
    override operator fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}

class OverriddenNonOperatorEquals {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}