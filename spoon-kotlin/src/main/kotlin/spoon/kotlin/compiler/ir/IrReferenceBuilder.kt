package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.types.impl.IrTypeProjectionImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.types.FlexibleType
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.WrappedType
import spoon.SpoonException
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.reflect.reference.CtPackageReference
import spoon.reflect.reference.CtReference
import spoon.reflect.reference.CtTypeParameterReference
import spoon.reflect.reference.CtTypeReference

class IrReferenceBuilder(private val irTreeBuilder: IrTreeBuilder) {

    private val factory get() = irTreeBuilder.factory

    private fun <T> getNewSimpleTypeReference(irType: IrSimpleType): CtTypeReference<T> {
        val ctRef = typeRefFromDescriptor(irType.classifier.descriptor)
        ctRef.setActualTypeArguments<CtTypeReference<*>>(irType.arguments.map { visitTypeArgument(it) })
        return ctRef as CtTypeReference<T>
    }

    fun <T> getNewTypeReference(irType: IrType): CtTypeReference<T> = when(irType) {
        is IrSimpleType -> getNewSimpleTypeReference(irType)
        is IrErrorType -> TODO()
        is IrDynamicType -> TODO()
        else -> throw SpoonException("Unexpected Ir type: ${irType::class.simpleName}")
    }

    fun <T> getNewTypeReference(classDescriptor: ClassDescriptor) =
        typeRefFromDescriptor(classDescriptor) as CtTypeReference<T>

    fun <T> getNewTypeReference(kotlinType: KotlinType): CtTypeReference<T> = when(kotlinType) {
        is WrappedType -> typeRefFromDescriptor(kotlinType.unwrap().constructor.declarationDescriptor!!)
        is SimpleType -> typeRefFromDescriptor(kotlinType.constructor.declarationDescriptor!!)
        is FlexibleType -> TODO()
    } as CtTypeReference<T>

    private fun typeRefFromDescriptor(descriptor: ClassifierDescriptor) = when(descriptor) {
        is ClassDescriptor -> {
            irTreeBuilder.factory.Core().createTypeReference<Any>().apply {
                setSimpleName<CtReference>(descriptor.name.identifier)
                setPackageOrDeclaringType(getDeclaringRef(descriptor.containingDeclaration))
            }
        }
        is TypeParameterDescriptor -> {
            irTreeBuilder.factory.Core().createTypeParameterReference().apply {
                setSimpleName<CtReference>(descriptor.name.identifier)
                setPackageOrDeclaringType(getDeclaringRef(descriptor.containingDeclaration))
            }
        }
        else -> TODO()
    }

    private fun getDeclaringRef(descriptor: DeclarationDescriptor): CtReference {
        return when(descriptor) {
            is ClassDescriptor -> getDeclaringTypeReference(descriptor)
            is PackageFragmentDescriptor -> getPackageReference(descriptor.fqName)
            else -> TODO()
        }
    }

    private fun getDeclaringTypeReference(classDescriptor: ClassDescriptor): CtTypeReference<*> {
        val ctRef = factory.Core().createTypeReference<Any>()
        ctRef.setSimpleName<CtReference>(classDescriptor.name.identifier)
        ctRef.setPackageOrDeclaringType(getDeclaringRef(classDescriptor.containingDeclaration))
        return ctRef
    }

    private fun <T> CtTypeReference<T>.setPackageOrDeclaringType(declaring: CtReference) {
        when(declaring) {
            is CtPackageReference -> setPackage<CtTypeReference<T>>(declaring)
            is CtTypeReference<*> -> setDeclaringType<CtTypeReference<T>>(declaring)
        }
    }

    private fun visitTypeArgument(typeArgument: IrTypeArgument): CtTypeReference<Any> = when(typeArgument) {
        is IrStarProjection -> irTreeBuilder.factory.createWildcardReference()
        is IrSimpleType -> getNewSimpleTypeReference(typeArgument)
        is IrTypeProjectionImpl -> getNewTypeReference(typeArgument.type)
        is IrDynamicType,
        is IrErrorType -> TODO()
        else -> throw SpoonException("Unexpected IR type argument: ${typeArgument::class.simpleName}")
    }

    fun getNewTypeParameterReference(irTypeParam: IrTypeParameter): CtTypeParameterReference {
        val ctRef = irTreeBuilder.factory.Core().createTypeParameterReference()
        ctRef.setSimpleName<CtReference>(irTypeParam.name.identifier)
        ctRef.putMetadata<CtReference>(KtMetadataKeys.TYPE_REF_NULLABLE, irTypeParam.symbol.defaultType.isNullable())
        ctRef.addModifiersAsMetadata(IrToModifierKind.fromTypeVariable(irTypeParam))
        ctRef.setPackageOrDeclaringType(getDeclaringRef(irTypeParam.descriptor.containingDeclaration))
        return ctRef
    }

    fun getPackageReference(fqName : FqName) : CtPackageReference {
        if(fqName.isRoot) {
            return irTreeBuilder.factory.Package().topLevel()
        }

        return irTreeBuilder.factory.Core().createPackageReference().apply {
            setSimpleName<CtPackageReference>(fqName.asString())
        }
    }
}