package spoon.kotlin.compiler

import spoon.kotlin.reflect.KtModifierKind
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.resolve.toSymbol
import spoon.reflect.declaration.CtModule
import spoon.reflect.declaration.CtType
import spoon.reflect.factory.Factory


internal class FirTreeBuilderHelper(private val firTreeBuilder: FirTreeBuilder) {

    fun createType(firClass : FirRegularClass) : CtType<*> {
        val type : CtType<Any> = when(firClass.classKind) {
            ClassKind.CLASS -> firTreeBuilder.factory.Core().createClass<Any>()
            ClassKind.INTERFACE -> firTreeBuilder.factory.Core().createInterface()
            ClassKind.ENUM_CLASS -> firTreeBuilder.factory.Core().createEnum<Enum<*>>() as CtType<Any>
            ClassKind.ENUM_ENTRY -> TODO()
            ClassKind.ANNOTATION_CLASS -> TODO()
            ClassKind.OBJECT -> TODO()
        }
        type.setSimpleName<CtType<*>>(firClass.name.identifier)

        firTreeBuilder.addModifiersAsMetadata(type, KtModifierKind.fromClass(firClass))

        firClass.superConeTypes.forEach {
            firTreeBuilder.referenceBuilder.buildTypeReference<Any>(it).apply {
                val symbol = it.lookupTag.toSymbol(firClass.session)?.fir
                if(symbol != null && symbol is FirRegularClass) {
                    when (symbol.classKind) {
                        ClassKind.CLASS -> {
                            type.setSuperclass<CtType<Any>>(this)
                        }
                        ClassKind.INTERFACE -> {
                            type.addSuperInterface<Any, CtType<Any>>(this)
                        }
                        else -> {
                            throw RuntimeException("Bad class kind for supertype: $symbol")
                        }
                    }
                }
                else {
                    if(symbol == null )
                        throw RuntimeException("Can't access class symbol")
                    throw RuntimeException("Unknown symbol implementation: $symbol")
                }
            }

        }
        return type
    }

    fun getOrCreateModule(session: FirSession, factory : Factory) : CtModule {
        val mname = session.moduleInfo?.name?.asString() ?: return factory.Module().unnamedModule
        return factory.Module().unnamedModule
       // return factory.Module().getOrCreate(mname)
    }


}