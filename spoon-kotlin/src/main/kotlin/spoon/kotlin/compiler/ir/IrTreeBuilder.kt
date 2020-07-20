package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.fir.visitors.CompositeTransformResult
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstKind
import org.jetbrains.kotlin.ir.util.isInterface
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.reflect.code.CtLiteral
import spoon.reflect.declaration.*
import spoon.reflect.factory.Factory
import spoon.reflect.reference.CtTypeReference

class IrTreeBuilder(val factory: Factory): IrElementVisitor<CompositeTransformResult<CtElement>, ContextData> {
    val referenceBuilder = IrReferenceBuilder(this)
    val helper = IrTreeBuilderHelper(this)

    override fun visitElement(element: IrElement, data: ContextData): CompositeTransformResult<CtElement> {
        //TODO("Not yet implemented")
        return factory.Core().createContinue().compose()
    }

    override fun visitClass(declaration: IrClass, data: ContextData): CompositeTransformResult<CtElement> {
        val module = helper.getOrCreateModule()
        val type = helper.createType(declaration)
        val isObject = type.getMetadata(KtMetadataKeys.CLASS_IS_OBJECT) as Boolean? == true
        val containingDecl = declaration.descriptor.containingDeclaration
        if(containingDecl is PackageFragmentDescriptor) {
            val pkg = if (containingDecl.fqName.isRoot) module.rootPackage else
                factory.Package().getOrCreate(containingDecl.fqName.asString(), module)
            pkg.addType<CtPackage>(type)
        }

        // Modifiers
        val modifierList = IrToModifierKind.fromClass(declaration)
        type.addModifiersAsMetadata(modifierList)

        // Type params
        if(declaration.typeParameters.isNotEmpty()) {
            type.setFormalCtTypeParameters<CtType<*>>(
                declaration.typeParameters.map { visitTypeParameter(it, data).result() })
        }

        for(decl in declaration.declarations) {
            val ctDecl = decl.accept(this, data).single
            ctDecl.setParent(type)
            when(ctDecl) {
                is CtEnumValue<*> -> {
                    (type as CtEnum<Enum<*>>).addEnumValue<CtEnum<Enum<*>>>(ctDecl)
                }
                is CtField<*> -> type.addField(ctDecl)
                is CtMethod<*> -> {
                    if (declaration.isInterface && ctDecl.body != null) {
                        ctDecl.setDefaultMethod<Nothing>(true)
                    }
                    //if(decl.psi is KtClass) {
                     //   ctDecl.setImplicit<CtMethod<*>>(true)
                   // }
                    type.addMethod(ctDecl)
                }
                is CtConstructor<*> -> {
                    if (type is CtClass<*> && !isObject) {
                        (type as CtClass<Any>).addConstructor<CtClass<Any>>(ctDecl as CtConstructor<Any>)
                    }
                }
                is CtTypeMember -> {
                    type.addTypeMember(ctDecl)
                }
            }
        }

        return type.compose()
    }

    override fun visitTypeParameter(declaration: IrTypeParameter, data: ContextData):
            CompositeTransformResult.Single<CtTypeParameter> {
        val ctTypeParam = factory.Core().createTypeParameter()
        ctTypeParam.setSimpleName<CtTypeParameter>(declaration.name.identifier)
        val bounds = declaration.superTypes.map { referenceBuilder.getNewTypeReference<Any>(it) }
        if(bounds.size == 1) {
            ctTypeParam.setSuperclass<CtTypeParameter>(bounds[0])
        } else if(bounds.size > 1) {
            ctTypeParam.setSuperclass<CtTypeParameter>(
                factory.Type().createIntersectionTypeReferenceWithBounds<Any>(bounds)
            )
        }

        ctTypeParam.addModifiersAsMetadata(IrToModifierKind.fromTypeVariable(declaration))
        return ctTypeParam.compose()
    }

    override fun <T> visitConst(expression: IrConst<T>, data: ContextData): CompositeTransformResult<CtElement> {
        val value = when(expression.kind) {
            IrConstKind.Null -> null
            IrConstKind.Boolean -> expression.value as Boolean
            IrConstKind.Char -> expression.value as Char
            IrConstKind.Byte -> expression.value as Byte
            IrConstKind.Short -> expression.value as Short
            IrConstKind.Int -> expression.value as Int
            IrConstKind.Long -> expression.value as Long
            IrConstKind.String -> expression.value as String
            IrConstKind.Float -> expression.value as Float
            IrConstKind.Double -> expression.value as Double
        }
        val ctLiteral: CtLiteral<T> = factory.Core().createLiteral()
        ctLiteral.setValue<CtLiteral<T>>(value as T)
        if(value == null)
            ctLiteral.setType<CtLiteral<T>>(factory.Type().nullType() as CtTypeReference<T>)
        else
            ctLiteral.setType<CtLiteral<T>>(referenceBuilder.getNewTypeReference(expression.type))
        return ctLiteral.compose()
    }

    private fun <T: CtElement> T.compose() = CompositeTransformResult.Single(this)
    private fun <T: CtElement> CompositeTransformResult.Single<T>.result() = _single
}