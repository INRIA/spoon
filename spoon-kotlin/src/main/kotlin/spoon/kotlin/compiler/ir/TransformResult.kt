package spoon.kotlin.compiler.ir

import spoon.reflect.declaration.CtElement

sealed class TransformResult<out T : Any>(protected val result: T?) {
    val isNothing get() = result == null
    val isDefinite get() = this is DefiniteTransformResult<*>
    val isComposite get() = this is CompositeTransformResult<*>
    open val resultUnsafe get() = result ?: throw RuntimeException("Empty TransformResult")
    open val resultOrNull get() = result
    companion object {
        fun <T: Any> nothing() = MaybeTransformResult<T>(null)
    }
}
class CompositeTransformResult<out T: Any>(result: List<T>) : TransformResult<T>(null) {
    private val cResult: List<T> = result
    val compositeResultSafe get() = cResult
    override val resultOrNull: T?
        get() = throw RuntimeException("Composite result")
    override val resultUnsafe: T
        get() = throw RuntimeException("Composite result")
}

class DefiniteTransformResult<out T : Any>(result: T) : MaybeTransformResult<T>(result) {
    val resultSafe get() = result!!
}

open class MaybeTransformResult<out T: Any>(result: T?) : TransformResult<T>(result)

class EmptyTransformResult<out T: Any> : MaybeTransformResult<T>(null)

inline fun <T: CtElement> T.definite() = DefiniteTransformResult(this)
inline fun <T: CtElement> maybe(t: T?) = MaybeTransformResult(t)