package spoon.test.trycatch.testclasses

class MultipleCatch {
    fun m() {
        try {

        } catch (e: RuntimeException) {

        } catch (e: Throwable) {

        } finally {

        }
    }

    fun noFinally() {
        try {} catch (e: RuntimeException) {}
    }
}