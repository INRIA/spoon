package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.impl.LocalVariableDescriptor
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.types.impl.IrTypeProjectionImpl
import org.jetbrains.kotlin.ir.util.getArguments
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.FlexibleType
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.WrappedType
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.reflect.reference.*

internal class IrReferenceBuilder(private val irTreeBuilder: IrTreeBuilder) {

    private val factory get() = irTreeBuilder.factory
    private val helper get() = irTreeBuilder.helper
    private fun Name.escaped() = helper.escapedIdentifier(this)

    private fun <T> getNewSimpleTypeReference(irType: IrSimpleType): CtTypeReference<T> {
        val ctRef = typeRefFromDescriptor(irType.classifier.descriptor)
        ctRef.setActualTypeArguments<CtTypeReference<*>>(irType.arguments.map { visitTypeArgument(it) })
        ctRef.putMetadata<CtReference>(KtMetadataKeys.TYPE_REF_NULLABLE, irType.hasQuestionMark)
        return ctRef as CtTypeReference<T>
    }

    fun <T> getNewTypeReference(irType: IrType): CtTypeReference<T> = when(irType) {
        is IrSimpleType -> getNewSimpleTypeReference(irType)
        is IrErrorType -> TODO()
        is IrDynamicType -> TODO()
        else -> throw SpoonIrBuildException("Unexpected Ir type: ${irType::class.simpleName}")
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
                setSimpleName<CtReference>(descriptor.name.escaped())
                setPackageOrDeclaringType(getDeclaringRef(descriptor.containingDeclaration))
            }
        }
        is TypeParameterDescriptor -> {
            irTreeBuilder.factory.Core().createTypeParameterReference().apply {
                setSimpleName<CtReference>(descriptor.name.escaped())
                // TODO Type params shouldn't have declaring type?
               // setPackageOrDeclaringType(getDeclaringRef(descriptor.containingDeclaration))
            }
        }
        else -> TODO()
    }

    private fun getDeclaringRef(descriptor: DeclarationDescriptor): CtReference {
        return when(descriptor) {
            is ClassDescriptor -> getDeclaringTypeReference(descriptor)
            is PackageFragmentDescriptor -> getPackageReference(descriptor.fqName)
            is FunctionDescriptor -> getDeclaringRef(descriptor.containingDeclaration)
            else -> TODO()
        }
    }

    private fun getDeclaringTypeReference(classDescriptor: ClassDescriptor): CtTypeReference<*> {
        val ctRef = factory.Core().createTypeReference<Any>()
        ctRef.setSimpleName<CtReference>(classDescriptor.name.escaped())
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
        else -> throw SpoonIrBuildException("Unexpected IR type argument: ${typeArgument::class.simpleName}")
    }

    fun getNewTypeParameterReference(irTypeParam: IrTypeParameter): CtTypeParameterReference {
        val ctRef = irTreeBuilder.factory.Core().createTypeParameterReference()
        ctRef.setSimpleName<CtReference>(irTypeParam.name.escaped())
        ctRef.putMetadata<CtReference>(KtMetadataKeys.TYPE_REF_NULLABLE, irTypeParam.symbol.defaultType.isNullable())
        ctRef.addModifiersAsMetadata(IrToModifierKind.fromTypeVariable(irTypeParam))
        ctRef.setPackageOrDeclaringType(getDeclaringRef(irTypeParam.descriptor.containingDeclaration))
        return ctRef
    }

    private fun <T> CtVariableReference<T>.setNameAndType(descriptor: ValueDescriptor) {
        setSimpleName<CtVariableReference<*>>(descriptor.name.escaped())
        setType<CtVariableReference<T>>(getNewTypeReference<T>(descriptor.type))
    }

    fun <T> getNewVariableReference(localVar: LocalVariableDescriptor): CtLocalVariableReference<T> =
        irTreeBuilder.factory.Core().createLocalVariableReference<T>().also { it.setNameAndType(localVar) }


    fun <T> getNewVariableReference(property: PropertyDescriptor): CtFieldReference<T> =
        irTreeBuilder.factory.Core().createFieldReference<T>().also {
            it.setNameAndType(property)
            it.setDeclaringType<CtFieldReference<T>>(getDeclaringRef(property.containingDeclaration) as? CtTypeReference<*>)
        }

    private fun <T> getNewVariableReference(valueParam: ValueParameterDescriptor): CtParameterReference<T> {
        val paramRef = irTreeBuilder.factory.Core().createParameterReference<T>()
        paramRef.setSimpleName<CtVariableReference<T>>(valueParam.name.escaped())
        paramRef.setType<CtVariableReference<T>>(getNewTypeReference(valueParam.type))
        return paramRef
    }

    fun <T> getNewVariableReference(irGetValue: IrGetValue): CtVariableReference<T> =
        when(val descriptor = irGetValue.symbol.descriptor) {
            is LocalVariableDescriptor -> getNewVariableReference<T>(descriptor)
            is PropertyDescriptor -> getNewVariableReference<T>(descriptor)
            is ValueParameterDescriptor -> getNewVariableReference<T>(descriptor)
        else -> throw SpoonIrBuildException("Unexpected value descriptor ${descriptor::class.simpleName}")
    }

    // ========================== EXECUTABLE ==========================

    fun <T> getNewExecutableReference(irCall: IrCall): CtExecutableReference<T> {
        val executableReference = irTreeBuilder.factory.Core().createExecutableReference<T>()
        executableReference.setSimpleName<CtReference>(irCall.symbol.descriptor.name.escaped())
        executableReference.setType<CtExecutableReference<T>>(getNewTypeReference(irCall.type))
        if(irCall.valueArgumentsCount > 0) {
            executableReference.setParameters<CtExecutableReference<T>>(
                irCall.getArguments().map { getNewTypeReference<Any>(it.first.type) }
            )
        }
        return executableReference
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