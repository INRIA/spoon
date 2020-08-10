package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.impl.LocalVariableDescriptor
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.declarations.IrValueDeclaration
import org.jetbrains.kotlin.ir.descriptors.IrTemporaryVariableDescriptor
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.types.impl.IrTypeProjectionImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.*
import org.jetbrains.kotlin.types.typeUtil.representativeUpperBound
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.reflect.reference.*

internal class IrReferenceBuilder(private val irTreeBuilder: IrTreeBuilder) {

    private val factory get() = irTreeBuilder.factory
    private val helper get() = irTreeBuilder.helper
    private fun Name.escaped() = if(this.isSpecial) this.asString() else helper.escapedIdentifier(this)

    private fun <T> getNewSimpleTypeReference(irType: IrSimpleType, resolveGenerics: Boolean): CtTypeReference<T> {
        if(irType.abbreviation != null) {
            return getNewAbbreviatedTypeReference(irType.abbreviation!!, resolveGenerics)
        }
        val ctRef = typeRefFromDescriptor(irType.classifier.descriptor, resolveGenerics)
        ctRef.setActualTypeArguments<CtTypeReference<*>>(irType.arguments.map { visitTypeArgument(it) })
        ctRef.putMetadata<CtReference>(KtMetadataKeys.TYPE_REF_NULLABLE, irType.hasQuestionMark)
        return ctRef as CtTypeReference<T>
    }

    /**
     * typealias A = Int
     * This will return only type ref to A
     */
    private fun <T> getNewAbbreviatedTypeReference(typeAbbreviation: IrTypeAbbreviation, resolveGenerics: Boolean): CtTypeReference<T> {
        val ctRef = typeRefFromDescriptor(typeAbbreviation.typeAlias.descriptor, resolveGenerics)
        ctRef.setActualTypeArguments<CtTypeReference<*>>(typeAbbreviation.arguments.map { visitTypeArgument(it) })
        ctRef.putMetadata<CtReference>(KtMetadataKeys.TYPE_REF_NULLABLE, typeAbbreviation.hasQuestionMark)
        return ctRef as CtTypeReference<T>
    }

    /**
     * typealias A = Int
     * This will return type ref to Int with A in it's metadata
     */
    private fun <T> getNewTypeReferenceWithAbbreviationAsMetadata(irSimpleType: IrSimpleType): CtTypeReference<T> {
        val ctRef = getNewTypeReference<T>(irSimpleType)
        val irAbbreviation = irSimpleType.abbreviation
        if(irAbbreviation != null) {
            val abbreviation = getNewAbbreviatedTypeReference<Any>(irAbbreviation, false)
            ctRef.putKtMetadata(KtMetadataKeys.TYPE_ALIAS, KtMetadata.wrap(abbreviation))
        }
        return ctRef
    }

    fun <T> getNewTypeReference(irType: IrType): CtTypeReference<T> = getNewTypeReference(irType, false)

    private fun <T> getNewTypeReference(irType: IrType, resolveGenerics: Boolean): CtTypeReference<T> = when(irType) {
        is IrSimpleType -> getNewSimpleTypeReference(irType, resolveGenerics)
        is IrErrorType -> TODO()
        is IrDynamicType -> TODO()
        else -> throw SpoonIrBuildException("Unexpected Ir type: ${irType::class.simpleName}")
    }


    fun <T> getNewTypeReference(classDescriptor: ClassDescriptor) =
        typeRefFromDescriptor(classDescriptor, false) as CtTypeReference<T>

    fun <T> getNewTypeReference(kotlinType: KotlinType): CtTypeReference<T> =
        getNewTypeReference(kotlinType, false)

    private fun <T> getNewTypeReference(kotlinType: KotlinType, resolveGenerics: Boolean): CtTypeReference<T> {
        val ctRef = when(kotlinType) {
            is AbbreviatedType -> typeRefFromDescriptor(kotlinType.abbreviation.constructor.declarationDescriptor!!, resolveGenerics)
            is WrappedType -> typeRefFromDescriptor(kotlinType.unwrap().constructor.declarationDescriptor!!, resolveGenerics)
            is SimpleType -> typeRefFromDescriptor(kotlinType.constructor.declarationDescriptor!!, resolveGenerics)
            is FlexibleType -> TODO()
        } as CtTypeReference<T>
        ctRef.putKtMetadata(KtMetadataKeys.TYPE_REF_NULLABLE, KtMetadata.wrap(kotlinType.isMarkedNullable))
        return ctRef
    }

    private fun typeRefFromDescriptor(descriptor: ClassifierDescriptor, resolveGenerics: Boolean) = when(descriptor) {
        is TypeAliasDescriptor -> {
            irTreeBuilder.core.createTypeReference<Any>().apply {
                setSimpleName<CtReference>(descriptor.name.escaped())
                setPackageOrDeclaringType(getDeclaringReference(descriptor))
            }
        }
        is ClassDescriptor -> {
            irTreeBuilder.factory.Core().createTypeReference<Any>().apply {
                setSimpleName<CtReference>(descriptor.name.escaped())
                setPackageOrDeclaringType(getDeclaringReference(descriptor.containingDeclaration))
            }
        }
        is TypeParameterDescriptor -> {
            if(resolveGenerics) {
                getNewTypeReference<Any>(descriptor.representativeUpperBound, resolveGenerics)
            } else {
                irTreeBuilder.factory.Core().createTypeParameterReference().apply {
                    setSimpleName<CtReference>(descriptor.name.escaped())
                    // TODO Type params shouldn't have declaring type?
                    // setPackageOrDeclaringType(getDeclaringRef(descriptor.containingDeclaration))
                }
            }

        }
        else -> TODO()
    }

    private fun getDeclaringReference(descriptor: DeclarationDescriptor): CtReference {
        return when(descriptor) {
            is ClassDescriptor -> getDeclaringTypeReference(descriptor)
            is PackageFragmentDescriptor -> getPackageReference(descriptor.fqName)
            is FunctionDescriptor -> getDeclaringReference(descriptor.containingDeclaration)
            is TypeAliasDescriptor -> getDeclaringReference(descriptor.containingDeclaration)
            else -> TODO()
        }
    }

    fun getDeclaringTypeReference(descriptor: DeclarationDescriptor): CtTypeReference<Any>? =
        getDeclaringReference(descriptor) as? CtTypeReference<Any>?

    private fun getDeclaringTypeReference(classDescriptor: ClassDescriptor): CtTypeReference<*> {
        val ctRef = factory.Core().createTypeReference<Any>()
        ctRef.setSimpleName<CtReference>(classDescriptor.name.escaped())
        ctRef.setPackageOrDeclaringType(getDeclaringReference(classDescriptor.containingDeclaration))
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
        is IrSimpleType -> getNewSimpleTypeReference(typeArgument, false)
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
        ctRef.setPackageOrDeclaringType(getDeclaringReference(irTypeParam.descriptor.containingDeclaration))
        return ctRef
    }

    private fun <T> CtVariableReference<T>.setNameAndType(descriptor: ValueDescriptor) {
        setSimpleName<CtVariableReference<*>>(descriptor.name.escaped())
        setType<CtVariableReference<T>>(getNewTypeReference<T>(descriptor.type, false))
    }

    // ========================== VARIABLE ==========================

    fun <T> getNewVariableReference(localVar: LocalVariableDescriptor): CtLocalVariableReference<T> =
        irTreeBuilder.factory.Core().createLocalVariableReference<T>().also { it.setNameAndType(localVar) }


    fun <T> getNewVariableReference(property: PropertyDescriptor): CtFieldReference<T> =
        irTreeBuilder.factory.Core().createFieldReference<T>().also {
            it.setNameAndType(property)
            it.setDeclaringType<CtFieldReference<T>>(getDeclaringReference(property.containingDeclaration) as? CtTypeReference<*>)
        }

    private fun <T> getNewVariableReference(valueParam: ValueParameterDescriptor): CtParameterReference<T> {
        val paramRef = irTreeBuilder.factory.Core().createParameterReference<T>()
        paramRef.setSimpleName<CtVariableReference<T>>(valueParam.name.escaped())
        paramRef.setType<CtVariableReference<T>>(getNewTypeReference(valueParam.type, false))
        return paramRef
    }
    // TODO Needs cleanup and generalizing of all these different cases
    fun <T> getNewVariableReference(descriptor: ValueDescriptor) =
        when(descriptor) {
            is LocalVariableDescriptor -> getNewVariableReference<T>(descriptor)
            is PropertyDescriptor -> getNewVariableReference<T>(descriptor)
            is ValueParameterDescriptor -> getNewVariableReference<T>(descriptor)
            is IrTemporaryVariableDescriptor -> null
            else -> throw SpoonIrBuildException("Unexpected value descriptor ${descriptor::class.simpleName}")
        }

    private fun <T> getNewVariableReference(valueDeclaration: IrValueDeclaration): CtVariableReference<T>? {
        return if(valueDeclaration.origin == IrDeclarationOrigin.CATCH_PARAMETER) {
            irTreeBuilder.core.createCatchVariableReference<T>().also {
                it.setNameAndType(valueDeclaration.descriptor)
            }
        } else {
            getNewVariableReference<T>(valueDeclaration.descriptor)
        }
    }

    fun <T> getNewVariableReference(irGetValue: IrGetValue): CtVariableReference<T>? {
        return getNewVariableReference(irGetValue.symbol.owner)
    }


    // ========================== EXECUTABLE ==========================

    fun <T> getNewExecutableReference(irCall: IrFunctionAccessExpression): CtExecutableReference<T> {
        val executableReference = irTreeBuilder.core.createExecutableReference<T>()
        executableReference.setSimpleName<CtReference>(irCall.symbol.descriptor.name.escaped())
        executableReference.setType<CtExecutableReference<T>>(getNewTypeReference(irCall.type))
        executableReference.setDeclaringType<CtExecutableReference<T>>(getDeclaringTypeReference(
            irCall.symbol.descriptor.containingDeclaration
        ))
        if(irCall.valueArgumentsCount > 0) {
            val args = ArrayList<CtTypeReference<*>>()
            // Using getArguments() includes receiver parameter
            for(i in 0 until irCall.valueArgumentsCount) {
                val arg = irCall.getValueArgument(i) ?: continue
                args.add(getNewTypeReference<Any>(arg.type, true) )
            }
            executableReference.setParameters<CtExecutableReference<T>>(args)
        }
        return executableReference
    }

    // Does not set declaring type as that requires subtypes of FunctionAccessExpression
    private fun <T> getConstructorExecutableReferenceWithoutDeclaringType(
        constructorCall: IrFunctionAccessExpression): CtExecutableReference<T> {
        val executableReference = irTreeBuilder.core.createExecutableReference<T>()
        executableReference.setSimpleName<CtReference>(constructorCall.symbol.descriptor.name.asString())
        val descriptor = constructorCall.symbol.descriptor as ClassConstructorDescriptor
        executableReference.setType<CtExecutableReference<T>>(
            getNewTypeReference(descriptor.returnType, false))

        val valueArgs = ArrayList<CtTypeReference<*>>()
        for(i in 0 until constructorCall.valueArgumentsCount) {
            val arg = constructorCall.getValueArgument(i)!!
            valueArgs.add(getNewTypeReference<Any>(arg.type))
        }
        if(valueArgs.isNotEmpty()) {
            executableReference.setParameters<CtExecutableReference<T>>(valueArgs)
        }
        return executableReference
    }

    fun <T> getNewConstructorExecutableReference(constructorCall: IrEnumConstructorCall): CtExecutableReference<T> {
        val executableReference = getConstructorExecutableReferenceWithoutDeclaringType<T>(constructorCall)
        val declaringType = constructorCall.symbol.descriptor.containingDeclaration
        executableReference.setDeclaringType<CtExecutableReference<T>>(getDeclaringTypeReference(declaringType))
        return executableReference
    }

    fun <T> getNewConstructorExecutableReference(constructorCall: IrConstructorCall): CtExecutableReference<T> {
        val executableReference = getConstructorExecutableReferenceWithoutDeclaringType<T>(constructorCall)
        val declaringType = constructorCall.symbol.descriptor.containingDeclaration
        executableReference.setDeclaringType<CtExecutableReference<T>>(getDeclaringTypeReference(declaringType))
        return executableReference
    }

    fun <T> getNewDelegatingExecutableReference(constructorCall: IrDelegatingConstructorCall): CtExecutableReference<T> {
        val executableReference = getConstructorExecutableReferenceWithoutDeclaringType<T>(constructorCall)
        val declaringType = constructorCall.symbol.descriptor.containingDeclaration
        executableReference.setDeclaringType<CtExecutableReference<T>>(getDeclaringTypeReference(declaringType))
        return executableReference
    }

    fun getPackageReference(fqName : FqName) : CtPackageReference {
        if(fqName.isRoot) {
            return irTreeBuilder.factory.Package().topLevel()
        }

        return irTreeBuilder.core.createPackageReference().apply {
            setSimpleName<CtPackageReference>(fqName.asString())
        }
    }
}