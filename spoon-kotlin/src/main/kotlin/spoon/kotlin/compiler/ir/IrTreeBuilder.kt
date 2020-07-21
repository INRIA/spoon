package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PropertyGetterDescriptor
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrValueParameterSymbol
import org.jetbrains.kotlin.ir.types.isNullableAny
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi2ir.PsiSourceManager
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.kotlin.reflect.KtModifierKind
import spoon.kotlin.reflect.KtStatementExpression
import spoon.kotlin.reflect.KtStatementExpressionImpl
import spoon.reflect.code.*
import spoon.reflect.declaration.*
import spoon.reflect.factory.Factory
import spoon.reflect.reference.*
import spoon.support.reflect.code.CtLiteralImpl

internal class IrTreeBuilder(
    val factory: Factory,
    val sourceManager: PsiSourceManager,
    private val detectInfix: Boolean = true
) : IrElementVisitor<TransformResult<CtElement>, ContextData?> {
    val referenceBuilder = IrReferenceBuilder(this)
    val helper = IrTreeBuilderHelper(this)
    private val core get() = factory.Core()
    private fun Name.escaped() = helper.escapedIdentifier(this)
    internal val toplvlClassName = "<top-level>"

    override fun visitElement(element: IrElement, data: ContextData?): TransformResult<CtElement> {
        //TODO("Not yet implemented")
        return CtLiteralImpl<String>().setValue<CtLiteral<String>>("Unimplemented element $element").definitely()
    }

    override fun visitFile(declaration: IrFile, data: ContextData?): DefiniteTransformResult<CtCompilationUnit> {
        val module = helper.getOrCreateModule()
        val compilationUnit = factory.CompilationUnit().getOrCreate(declaration.name)

        val pkg = if(declaration.packageFragmentDescriptor.fqName.isRoot) module.rootPackage else
            factory.Package().getOrCreate(declaration.packageFragmentDescriptor.fqName.asString(), module)

        compilationUnit.declaredPackage = pkg
        compilationUnit.lineSeparatorPositions = declaration.fileEntry.lineStartOffsets

        for(subDeclaration in declaration.declarations) {
            val ctDecl = subDeclaration.accept(this, Empty(declaration)).resultUnsafe
            when(ctDecl) {
                is CtType<*> -> {
                    pkg.addType<CtPackage>(ctDecl)
                    compilationUnit.addDeclaredType(ctDecl)
                }
                is CtTypeMember -> {
                    val topLvl = pkg.getType<CtType<Any>>(toplvlClassName) ?:
                    (core.createClass<Any>().also {
                        topLvlClass ->
                        topLvlClass.setImplicit<CtClass<*>>(true)
                        topLvlClass.setSimpleName<CtClass<*>>(toplvlClassName)
                        pkg.addType<CtPackage>(topLvlClass)
                        ctDecl.putKtMetadata<CtTypeMember>(KtMetadataKeys.TOP_LEVEL_DECLARING_CU, KtMetadata.wrap(compilationUnit))
                    })
                    topLvl.addTypeMember<CtClass<Any>>(ctDecl)
                }
            }
        }

        return compilationUnit.definitely()
    }

    override fun visitClass(declaration: IrClass, data: ContextData?): DefiniteTransformResult<CtElement> {
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
            type.setFormalCtTypeParameters<CtType<Any>>(
                declaration.typeParameters.map { visitTypeParameter(it, data).resultSafe })
        }

        for(decl in declaration.declarations) {
            if(decl.isFakeOverride) continue
            val ctDecl = decl.accept(this, data).resultUnsafe
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

        return type.definitely()
    }

    override fun visitAnonymousInitializer(
        declaration: IrAnonymousInitializer,
        data: ContextData?
    ): DefiniteTransformResult<CtElement> {
        val ctAnonExecutable = core.createAnonymousExecutable()
        val body = visitBody(declaration.body, data).resultSafe
        ctAnonExecutable.setBody<CtAnonymousExecutable>(body)
        return ctAnonExecutable.definitely()
    }

    override fun visitTypeParameter(declaration: IrTypeParameter, data: ContextData?):
            DefiniteTransformResult<CtTypeParameter> {
        val ctTypeParam = factory.Core().createTypeParameter()
        ctTypeParam.setSimpleName<CtTypeParameter>(declaration.name.escaped())
        // Don't include default upper bound ("Any?")
        val bounds = declaration.superTypes.filterNot { it.isNullableAny() }.map { referenceBuilder.getNewTypeReference<Any>(it) }
        if(bounds.size == 1) {
            ctTypeParam.setSuperclass<CtTypeParameter>(bounds[0])
        } else if(bounds.size > 1) {
            ctTypeParam.setSuperclass<CtTypeParameter>(
                factory.Type().createIntersectionTypeReferenceWithBounds<Any>(bounds)
            )
        }

        ctTypeParam.addModifiersAsMetadata(IrToModifierKind.fromTypeVariable(declaration))
        return ctTypeParam.definitely()
    }

    override fun <T> visitConst(expression: IrConst<T>, data: ContextData?): DefiniteTransformResult<CtLiteral<*>> {
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
        else {
            ctLiteral.setType<CtLiteral<T>>(referenceBuilder.getNewTypeReference(expression.type))
            if(value is Number) {
                ctLiteral.setBase<CtLiteral<T>>(helper.getBaseOfConst(expression as IrConst<Number>, data!!.file))
            }
        }
        return ctLiteral.definitely()
    }

    override fun visitProperty(declaration: IrProperty, data: ContextData?): DefiniteTransformResult<CtElement> {
        val ctField = core.createField<Any>()
        ctField.setSimpleName<CtField<*>>(declaration.name.escaped())

        // Initializer (if any) exists in backing field initializer
        val backingField = declaration.backingField
        val initializer = backingField?.initializer
        if(initializer != null) {
            val ctInitializer = visitExpressionBody(initializer, data).resultUnsafe

            if(backingField.origin == IrDeclarationOrigin.DELEGATE) {
                ctField.putKtMetadata<CtElement>(KtMetadataKeys.PROPERTY_DELEGATE, KtMetadata.wrap(ctInitializer))
                ctInitializer.setParent(ctField)
            } else {
                ctField.setDefaultExpression<CtField<Any>>(
                    expressionOrWrappedInStatementExpression(ctInitializer))
            }
        }

        // Modifiers
        ctField.addModifiersAsMetadata(IrToModifierKind.fromProperty(declaration))

        // Type
        val type = if(backingField != null) {
            referenceBuilder.getNewTypeReference<Any>(backingField.type)
        } else if(declaration.getter != null) {
            referenceBuilder.getNewTypeReference<Any>(declaration.getter!!.returnType)
        } else {
            throw SpoonIrBuildException("Unable to get IR type of property $declaration")
        }
        ctField.setType<CtField<*>>(type)

        // Mark implicit/explicit type
        // TODO

        // Check if property stems from constructor value parameter, then the property is implicit
        // TODO

        val getter = declaration.getter
        if(getter != null && getter.origin != IrDeclarationOrigin.DEFAULT_PROPERTY_ACCESSOR) {
            ctField.putKtMetadata<CtField<*>>(KtMetadataKeys.PROPERTY_GETTER,
                KtMetadata.wrap(createUnnamedFunction(getter, data)))
        }

        val setter = declaration.setter
        if(setter != null && setter.origin != IrDeclarationOrigin.DEFAULT_PROPERTY_ACCESSOR) {
            ctField.putKtMetadata<CtField<*>>(KtMetadataKeys.PROPERTY_SETTER,
                KtMetadata.wrap(createUnnamedFunction(setter, data)))
        }

        return ctField.definitely()
    }

    override fun visitLocalDelegatedProperty(
        declaration: IrLocalDelegatedProperty,
        data: ContextData?
    ): TransformResult<CtElement> {
        return super.visitLocalDelegatedProperty(declaration, data)
    }

    override fun visitVariable(declaration: IrVariable, data: ContextData?): TransformResult<CtLocalVariable<*>> {
        val ctLocalVar = core.createLocalVariable<Any>()
        ctLocalVar.setSimpleName<CtVariable<*>>(declaration.name.escaped())

        // Initializer
        val initializer = declaration.initializer?.accept(this, data)?.resultUnsafe
        if(initializer != null) {
            val initializerExpr = expressionOrWrappedInStatementExpression(initializer)
            ctLocalVar.setDefaultExpression<CtLocalVariable<Any>>(initializerExpr)
        }

        // Modifiers
        ctLocalVar.addModifiersAsMetadata(IrToModifierKind.fromVariable(declaration))

        // Type
        ctLocalVar.setType<CtLocalVariable<*>>(referenceBuilder.getNewTypeReference(declaration.type))

        // Implicit/explicit type marker
        // TODO

        return ctLocalVar.definitely()
    }

    override fun visitValueParameter(
        declaration: IrValueParameter,
        data: ContextData?
    ): DefiniteTransformResult<CtParameter<*>> {
        val ctParam = core.createParameter<Any>()
        ctParam.setSimpleName<CtParameter<Any>>(declaration.name.escaped())
        ctParam.setInferred<CtParameter<Any>>(false) // Not allowed

        // Modifiers
        val modifierList = IrToModifierKind.fromValueParameter(declaration)
        ctParam.addModifiersAsMetadata(modifierList)
        ctParam.setVarArgs<CtParameter<Any>>(KtModifierKind.VARARG in modifierList)

        val defaultValue = declaration.defaultValue?.let { visitExpressionBody(it, data) }?.resultUnsafe
        if(defaultValue != null) {
            ctParam.setDefaultExpression<CtParameter<Any>>(
                expressionOrWrappedInStatementExpression(defaultValue)
            )
        }

        // Type
        val type = if(declaration.isVararg) {
            declaration.varargElementType!!
        } else {
            declaration.type
        }
        ctParam.setType<CtParameter<Any>>(referenceBuilder.getNewTypeReference<Any>(type))

        // Mark implicit for "it" in lambda
        //TODO

        return ctParam.definitely()
    }

    private fun createUnnamedFunction(irFunction: IrFunction, data: ContextData?): CtMethod<*> {
        val ctMethod = core.createMethod<Any>()

        // Value params
        if(irFunction.valueParameters.isNotEmpty()) {
            ctMethod.setParameters<CtMethod<Any>>(
                irFunction.valueParameters.map { visitValueParameter(it, data).resultSafe }
            )
        }

        // Type params
        if(irFunction.typeParameters.isNotEmpty()) {
            ctMethod.setFormalCtTypeParameters<CtMethod<Any>>(
                irFunction.typeParameters.map { visitTypeParameter(it, data).resultSafe }
            )
        }

        // Return type
        ctMethod.setType<CtMethod<*>>(referenceBuilder.getNewTypeReference(irFunction.returnType))

        // Extension receiver
        val extensionReceiverRef = irFunction.extensionReceiverParameter?.type?.let {
            referenceBuilder.getNewTypeReference<Any>(it)
        }
        if(extensionReceiverRef != null) {
            extensionReceiverRef.setParent<CtReference>(extensionReceiverRef)
            ctMethod.putKtMetadata(KtMetadataKeys.EXTENSION_TYPE_REF,
                KtMetadata.wrap(extensionReceiverRef)
            )
        }

        // Body
        val body = irFunction.body
        if(body != null) {
            ctMethod.setBody<CtMethod<Any>>(visitBody(body, data).resultSafe)
        }
        return ctMethod
    }

    override fun visitSimpleFunction(
        declaration: IrSimpleFunction,
        data: ContextData?
    ): DefiniteTransformResult<CtMethod<*>> {
        return createUnnamedFunction(declaration, data).also {
            it.setSimpleName<CtMethod<*>>(declaration.name.escaped())
            it.addModifiersAsMetadata(IrToModifierKind.fromFunctionDeclaration(declaration))
        }.definitely()
    }

    override fun visitBody(body: IrBody, data: ContextData?): DefiniteTransformResult<CtBlock<*>> {
        val ctBlock = core.createBlock<Any>()
        val statements = ArrayList<CtStatement>()
        for(irStatement in body.statements) {
            if(irStatement is IrDeclaration && irStatement.isFakeOverride) continue
            statements.add(statementOrWrappedInImplicitReturn(irStatement.accept(this, data).resultUnsafe))
        }
        ctBlock.setStatements<CtBlock<*>>(statements)
        return ctBlock.definitely()
    }

    override fun visitCall(expression: IrCall, data: ContextData?): DefiniteTransformResult<CtElement> {
        if(expression.origin == IrStatementOrigin.GET_PROPERTY) {
            return visitPropertyAccess(expression, data!!).definitely()
        }
        val invocation = core.createInvocation<Any>()
        invocation.setExecutable<CtInvocation<Any>>(referenceBuilder.getNewExecutableReference(expression))

        val target = getReceiver(expression, data!!)
        if(target is CtExpression<*>) {
            invocation.setTarget<CtInvocation<Any>>(target)
        } else if(target != null) {
            throw RuntimeException("Function call target not CtExpression")
        }

        if(expression.valueArgumentsCount > 0) {
            invocation.setArguments<CtInvocation<Any>>(expression.symbol.descriptor.valueParameters.map {
                expressionOrWrappedInStatementExpression(
                    expression.getValueArgument(it.index)!!.accept(this, data).resultUnsafe
                )
            })
        }

        if(expression.typeArgumentsCount > 0) {
            invocation.setActualTypeArguments<CtInvocation<Any>>(
                expression.symbol.descriptor.typeParameters.map {
                    referenceBuilder.getNewTypeReference<Any>(expression.getTypeArgument(it.index)!!)
                }
            )
        }
        if(detectInfix) {
            invocation.putKtMetadata(
                KtMetadataKeys.INVOCATION_IS_INFIX,
                KtMetadata.wrap(helper.isInfixCall(expression, data))
            )
        }
        if(expression.origin == IrStatementOrigin.INVOKE) {
            invocation.putKtMetadata(
                KtMetadataKeys.INVOKE_AS_OPERATOR,
                KtMetadata.wrap(true)
            )
        }

        return invocation.definitely()
    }

    private fun visitPropertyAccess(irCall: IrCall, data: ContextData): CtVariableAccess<*> {
        val descriptor = irCall.symbol.descriptor as PropertyGetterDescriptor
        val variable = referenceBuilder.getNewVariableReference<Any>(descriptor.correspondingProperty)
        val target = getReceiver(irCall, data)
        val fieldRead = core.createFieldRead<Any>()
        fieldRead.setVariable<CtFieldRead<Any>>(variable)
        if(target != null) fieldRead.setTarget<CtFieldRead<Any>>(target as CtExpression<*>)
        return fieldRead
    }

    override fun visitExpressionBody(body: IrExpressionBody, data: ContextData?):
            TransformResult<CtElement> {
        return body.expression.accept(this, data)
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: ContextData?): TransformResult<CtElement> {
        if(expression.operator == IrTypeOperator.IMPLICIT_COERCION_TO_UNIT) {
            return expression.argument.accept(this, data)
        }
        return super.visitTypeOperator(expression, data)
    }

    override fun visitGetValue(expression: IrGetValue, data: ContextData?): TransformResult<CtElement> {
        val symbol = expression.symbol
        if(symbol is IrValueParameterSymbol) {
            val descriptor = symbol.descriptor
            if(descriptor is ReceiverParameterDescriptor) {
                return visitThisReceiver(expression, data!!).definitely()
            }
        }

        val variableRef = referenceBuilder.getNewVariableReference<Any>(expression)
        val varAccess = when(variableRef) {
            is CtFieldReference<*> -> {
                factory.Core().createFieldRead<Any>().also {
                    it.setVariable<CtVariableRead<Any>>(variableRef)
                }
            }
            is CtParameterReference<*> -> {
                factory.Core().createVariableRead<Any>().also {
                    it.setVariable<CtVariableRead<Any>>(variableRef)
                }
            }
            is CtLocalVariableReference<*> -> {
                factory.Core().createVariableRead<Any>().also {
                    it.setVariable<CtVariableRead<Any>>(variableRef)
                }
            }
            is CtSuperAccess<*> -> {
                variableRef
            }
            else -> throw SpoonIrBuildException("Unexpected access ${variableRef.simpleName}")
        }
        return maybe(varAccess)
    }

    private fun getReceiver(irCall: IrCall, data: ContextData): CtElement? {
        if(irCall.superQualifierSymbol != null) return visitSuperTarget(irCall.superQualifierSymbol!!)
        return helper.getReceiver(irCall)?.accept(this, data)?.resultOrNull
    }

    private fun visitThisReceiver(irGetValue: IrGetValue, data: ContextData): CtThisAccess<*> {
        val implicit = helper.isImplicitThis(irGetValue, data.file)
        return factory.Code().createThisAccess<Any>(
            referenceBuilder.getNewTypeReference(irGetValue.type),
            implicit
        )
    }

    private fun visitSuperTarget(symbol: IrClassSymbol): CtSuperAccess<*> {
        val superAccess = core.createSuperAccess<Any>()
        superAccess.setType<CtSuperAccess<*>>(referenceBuilder.getNewTypeReference(
            symbol.descriptor
        ))
        superAccess.setImplicit<CtSuperAccess<*>>(false)
        return superAccess
    }

    private fun <T> CtExpression<T>.wrapInImplicitReturn() : CtReturn<T> {
        val r = factory.Core().createReturn<T>()
        r.setReturnedExpression<CtReturn<T>>(this)
        r.setImplicit<CtReturn<T>>(true)
        return r
    }
    private fun <T> CtStatement.wrapInStatementExpression(type : CtTypeReference<T>) : KtStatementExpression<T> {
        val se = KtStatementExpressionImpl<T>(this)
        se.setType<KtStatementExpression<T>>(type)
        this.setParent(se)
        return se
    }

    private fun statementOrWrappedInImplicitReturn(e: CtElement): CtStatement = when(e) {
        is CtStatement -> e
        is CtExpression<*> -> e.wrapInImplicitReturn()
        else -> throw RuntimeException("Can't wrap ${e::class} in StatementExpression")
    }

    @Suppress("UNCHECKED_CAST")
    private fun expressionOrWrappedInStatementExpression(e: CtElement): CtExpression<Any> {
        val statementExpression: KtStatementExpression<*>
        when (e) {
            is CtExpression<*> -> return e as CtExpression<Any>
            is CtIf -> {
                val typeRef = e.getMetadata(KtMetadataKeys.KT_STATEMENT_TYPE) as CtTypeReference<Any>
                statementExpression = e.wrapInStatementExpression(typeRef)
                statementExpression.setImplicit(true)
            }
            is CtBlock<*> -> {
                if (e.statements.size == 1) {
                    val statement = e.statements[0]
                    if (e.isImplicit && statement is CtExpression<*>) {
                        return statement as CtExpression<Any>
                    } else {
                        e.setImplicit<CtBlock<*>>(true)
                    }
                }
                val typeRef = e.getMetadata(KtMetadataKeys.KT_STATEMENT_TYPE) as CtTypeReference<Any>
                statementExpression = e.wrapInStatementExpression(typeRef)
                statementExpression.setImplicit(true)
            }
            else -> throw RuntimeException("Can't wrap ${e::class.simpleName} in StatementExpression")
        }
        return statementExpression
    }
}