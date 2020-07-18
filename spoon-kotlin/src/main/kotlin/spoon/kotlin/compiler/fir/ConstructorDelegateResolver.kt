package spoon.kotlin.compiler.fir

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirConstructor
import org.jetbrains.kotlin.fir.declarations.impl.FirPrimaryConstructorImpl
import org.jetbrains.kotlin.fir.resolve.toSymbol
import org.jetbrains.kotlin.fir.resolve.transformers.firClassLike
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.ConeClassLikeType
import org.jetbrains.kotlin.fir.types.coneTypeSafe
import spoon.reflect.reference.CtExecutableReference

/**
 * There's a bug when resolving constructor delegates in the FIR. It appears when the class and its superclass don't
 * have a primary constructor:
 *
 * class B(..) {}
 * Case 1: class A(..) : B(..)
 * Case 2: class A : B(..)
 * Case 3:
 * class A() : B(..) {
 *   constructor(..) : this()
 * }
 * Primary constructor exists, explicit in case 1 and 3, implicit in case 2
 * Things work as expected.
 *
 * Case 4:
 * class B {
 *   constructor(..)
 * }
 * class A : B {
 *   constructor(..) : super(..) // 1
 *   constructor(..) : this(..)  // 2
 * }
 *
 * No primary constructor.
 * Delegated super constructor call (1) does not properly resolve constructor call. It has error node in callee
 * and constructed type Any. The error node is due to the resolved call goes to Any() which is nullary,
 * but it is called with 1 or more arguments.
 *
 * Case 5:
 * class B {
 *   constructor()
 * }
 * class A : B {
 *   constructor(..) : super()  // 1
 *   constructor(..) : this(..)   // 2
 * }
 * No primary constructor
 * Delegated super constructor call (1) does not properly resolve constructor call. It has callee Any()
 * constructor and constructed type Any. There is no error since the arguments match, but during runtime it will crash.
*/
object ConstructorDelegateResolver {
    fun <T> resolveSuperCallBug(treeBuilder: FirTreeBuilder, constructor: FirConstructor) : CtExecutableReference<T>? {
        val delegate = constructor.delegatedConstructor ?: return null
        var correctDelegateReturnType = delegate.constructedTypeRef
        if(!delegate.isThis) { // this() call, constructedTypeRef is correct

            val parentClass = // ReturnType is always correctly A
                (constructor.returnTypeRef.firClassLike(constructor.session) as FirClass<*>)
            val decls = parentClass.declarations

            // if parent has a primary constructor, constructedTypeRef is correct since delegate has to be this() call
            if (!decls.any { it is FirPrimaryConstructorImpl }) {

                val superType = parentClass.superTypeRefs.firstOrNull {
                    val c = it.coneTypeSafe<ConeClassLikeType>()?.lookupTag?.toSymbol(constructor.session)
                    if (c is FirRegularClassSymbol) c.fir.classKind == ClassKind.CLASS
                    else false
                } // this() call
                if(superType != null)
                    correctDelegateReturnType = superType
            }
        }
        return treeBuilder.referenceBuilder.getNewExecutableReference(delegate, correctDelegateReturnType)
    }
}