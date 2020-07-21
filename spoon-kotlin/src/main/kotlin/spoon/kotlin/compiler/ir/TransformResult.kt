package spoon.kotlin.compiler.ir

import spoon.reflect.declaration.CtElement

sealed class TransformResult<out T : Any>(protected val result: T?) {
    val isNothing get() = result == null
    val isDefinite get() = this is DefiniteTransformResult<*>
    val isMaybe get() = this is MaybeTransformResult<*>
    val resultUnsafe get() = result ?: throw RuntimeException("Empty TransformResult")
    val resultOrNull get() = result
    companion object {
        fun <T: Any> nothing() = MaybeTransformResult<T>(null)
    }
}

class DefiniteTransformResult<out T : Any>(result: T) : TransformResult<T>(result) {
    val resultSafe get() = result
}

class MaybeTransformResult<out T: Any>(result: T?) : TransformResult<T>(result)

inline fun <T: CtElement> T.definitely() = DefiniteTransformResult(this)
inline fun <T: CtElement> maybe(t: T?) = MaybeTransformResult(t)