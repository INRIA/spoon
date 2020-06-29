package spoon.kotlin.compiler

import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.expressions.FirDelegatedConstructorCall
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.references.FirResolvedNamedReference
import org.jetbrains.kotlin.fir.symbols.CallableId
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import spoon.SpoonException
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.reflect.reference.*

internal class ReferenceBuilder(val firTreeBuilder: FirTreeBuilder) {
    private val msgCollector = PrintingMsgCollector()

    fun <T> buildTypeReference(typeRef: ConeClassLikeType) : CtTypeReference<T> {
        val ctRef = firTreeBuilder.factory.Core().createTypeReference<T>()
        typeRef.classId?.let<ClassId,CtTypeReference<T>> {
                ctRef.setPackage<CtTypeReference<T>>(getPackageReference(it.packageFqName))
                ctRef.setSimpleName(it.shortClassName.identifier)
             }
        return ctRef
    }


    fun <T> buildGenericTypeReference(typeRef: FirResolvedTypeRef) : CtTypeReference<T> {
        TODO()
    }

    fun <T> getNewDeclaringTypeReference(callableId: CallableId) : CtTypeReference<T>? {
        val ref = firTreeBuilder.factory.Core().createTypeReference<T>()
        if(callableId.className == null) return null
        ref.setSimpleName<CtTypeReference<*>>(callableId.className!!.shortName().identifier)
        ref.setPackage<CtTypeReference<T>>(getPackageReference(callableId.packageName))
        return ref
    }

    fun <T> getNewExecutableReference(d : FirDelegatedConstructorCall, declaringType : FirTypeRef): CtExecutableReference<T> {
        val execRef = firTreeBuilder.factory.Core().createExecutableReference<T>()
        execRef.setSimpleName<CtExecutableReference<T>>(CtExecutableReference.CONSTRUCTOR_NAME)
        execRef.setType<CtExecutableReference<T>>(getNewTypeReference(declaringType))
        execRef.setDeclaringType<CtExecutableReference<T>>(getNewTypeReference<CtTypeReference<T>>(declaringType))
        return execRef
    }

    fun <T> getNewExecutableReference(call : FirFunctionCall): CtExecutableReference<T> {
        val execRef = firTreeBuilder.factory.Core().createExecutableReference<T>()
        val callee = call.calleeReference
        val name: String
        if(callee is FirResolvedNamedReference && callee.resolvedSymbol is FirFunctionSymbol<*>) {

            // Safer to use resolved name if possible, as called name and callee might differ when invoke operator is involved
            name = callee.resolvedSymbol.callableId.callableName.identifier
            execRef.setDeclaringType<CtExecutableReference<T>>(getNewDeclaringTypeReference<T>(callee.resolvedSymbol.callableId))

        } else {
            name = callee.name.identifier
        }
        execRef.setSimpleName<CtExecutableReference<T>>(name)
        execRef.setType<CtExecutableReference<T>>(getNewTypeReference(call.typeRef))
        if(call.arguments.isNotEmpty()) {
            execRef.setParameters<CtExecutableReference<T>>(call.arguments.map { getNewTypeReference<Any>(it.typeRef) })
        }
        return execRef
    }

    fun <T> getNewTypeReference(coneClass: ConeClassLikeType) : CtTypeReference<T> {
        val ctRef = firTreeBuilder.factory.Core().createTypeReference<T>()
        val classId = coneClass.classId ?: throw SpoonException("Can't get classId for $coneClass")
        ctRef.setSimpleName<CtTypeReference<T>>(classId.shortClassName.identifier)
        ctRef.setPackage<CtTypeReference<T>>(getPackageReference(classId.packageFqName))
        ctRef.putMetadata<CtTypeReference<T>>(KtMetadataKeys.TYPE_REF_NULLABLE, coneClass.nullability.isNullable)
        return ctRef
    }

    fun <T> getNewTypeReference(typeRef: FirTypeRef) : CtTypeReference<T> {
        val coneType = typeRef.coneTypeSafe<ConeClassLikeType>()
        if(coneType != null ) {
            return getNewTypeReference(coneType)
        } else {
            throw RuntimeException("Can't get ConeType for TypeRef $typeRef")
        }
    }

    fun <T> getNewVariableReference(property: FirProperty) : CtVariableReference<T> {
        val varRef = if(property.isLocal) firTreeBuilder.factory.Core().createLocalVariableReference<T>()
        else firTreeBuilder.factory.Core().createFieldReference<T>().also {
            it.setDeclaringType<CtFieldReference<T>>(getNewDeclaringTypeReference<CtTypeReference<T>>(property.symbol.callableId))
        }
        varRef.setSimpleName<CtVariableReference<T>>(property.name.identifier)
        varRef.setType<CtVariableReference<T>>(getNewTypeReference(property.returnTypeRef))
        return varRef
    }

    fun <T> getNewVariableReference(param: FirValueParameter) : CtVariableReference<T> {
        val varRef = firTreeBuilder.factory.Core().createParameterReference<T>()
        varRef.setSimpleName<CtVariableReference<T>>(param.name.identifier)
        varRef.setType<CtVariableReference<T>>(getNewTypeReference(param.returnTypeRef))
        return varRef
    }

    fun <T> getNewSimpleTypeReference(classId: ClassId) : CtTypeReference<T> {
        return firTreeBuilder.factory.Core().createTypeReference<T>().apply {
            setSimpleName<CtTypeReference<T>>(classId.shortClassName.identifier)
            setPackage(getPackageReference(classId.packageFqName))
        }
    }

    fun <T> getNewSimpleTypeReference(packageFqName: String, className: String) : CtTypeReference<T> {
        return firTreeBuilder.factory.Core().createTypeReference<T>().apply {
            setSimpleName<CtTypeReference<T>>(className)
            setPackage(getPackageReference(packageFqName))
        }
    }

    fun <T> setPackageOrDeclaringType(ref : CtTypeReference<T>, declaring : CtReference) {
        when(declaring) {
            is CtPackageReference -> ref.setPackage<CtTypeReference<T>>(declaring)
            is CtTypeReference<*> -> ref.setDeclaringType<CtTypeReference<T>>(declaring)
        }
    }

    fun getPackageReference(fqName : FqName) : CtPackageReference {
        if(fqName.isRoot) {
            return firTreeBuilder.factory.Package().topLevel()
        }

        return firTreeBuilder.factory.Core().createPackageReference().apply {
            setSimpleName<CtPackageReference>(fqName.asString())
        }
    }

    fun getPackageReference(fqName : String) : CtPackageReference {
        if(fqName.isEmpty()) {
            return firTreeBuilder.factory.Package().topLevel()
        }

        return firTreeBuilder.factory.Core().createPackageReference().apply {
            setSimpleName<CtPackageReference>(fqName)
        }
    }

}