package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.descriptors.IrBuiltInOperator
import org.jetbrains.kotlin.ir.descriptors.IrTemporaryVariableDescriptor
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrBlockBodyImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrElseBranchImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrIfThenElseImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrWhenImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrValueParameterSymbol
import org.jetbrains.kotlin.ir.symbols.IrVariableSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.isNullableAny
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.load.java.descriptors.JavaMethodDescriptor
import org.jetbrains.kotlin.load.java.descriptors.JavaPropertyDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.psi2ir.PsiSourceManager
import org.jetbrains.kotlin.psi2ir.generators.AUGMENTED_ASSIGNMENTS
import org.jetbrains.kotlin.psi2ir.generators.GeneratorContext
import org.jetbrains.kotlin.psi2ir.generators.INCREMENT_DECREMENT_OPERATORS
import org.jetbrains.kotlin.resolve.calls.tower.isSynthesized
import org.jetbrains.kotlin.resolve.scopes.receivers.ExtensionReceiver
import org.jetbrains.kotlin.resolve.scopes.receivers.ImplicitClassReceiver
import org.jetbrains.kotlin.resolve.source.getPsi
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull
import spoon.SpoonException
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.kotlin.reflect.KtModifierKind
import spoon.kotlin.reflect.KtStatementExpression
import spoon.kotlin.reflect.KtStatementExpressionImpl
import spoon.kotlin.reflect.code.KtBinaryOperatorKind
import spoon.reflect.code.*
import spoon.reflect.declaration.*
import spoon.reflect.factory.Factory
import spoon.reflect.reference.*
import spoon.support.reflect.cu.position.SourcePositionImpl

internal class IrTreeBuilder(
    val factory: Factory,
    private val context: GeneratorContext,
    private val detectImplicitTypes: Boolean = true,
    private val detectInfix: Boolean = true
) : IrElementVisitor<TransformResult<CtElement>, ContextData> {
    val referenceBuilder = IrReferenceBuilder(this)
    val helper = IrTreeBuilderHelper(this)
    val sourceManager: PsiSourceManager = context.sourceManager
    private lateinit var sourceHelper: PsiSourceHelper

    fun getSourceHelper(contextData: ContextData): PsiSourceHelper {
        if(!this::sourceHelper.isInitialized)
            sourceHelper = PsiSourceHelper(sourceManager, contextData.file)
        return sourceHelper
    }

    val core get() = factory.Core()
    private fun Name.escaped() = helper.escapedIdentifier(this)
    internal val toplvlClassName = "<top-level>"

    private fun checkLabelOfStatement(irElement: IrElement, ctElement: CtElement, data: ContextData) {
        val label = getSourceHelper(data).getLabelOrNull(irElement)
        if(label != null) {
            val isSet: Boolean = if(ctElement is CtStatement) {
                ctElement.setLabel<CtStatement>(label)
                ctElement.label != null // Some statements have unsettable labels
            }
            else false
            if(!isSet){
                ctElement.putKtMetadata(
                    KtMetadataKeys.LABEL,
                    KtMetadata.string(label)
                )
            }
        }
    }

    override fun visitElement(element: IrElement, data: ContextData): TransformResult<CtElement> {
        TODO("${element::class.simpleName} not implemented")
      //  return CtLiteralImpl<String>().setValue<CtLiteral<String>>("Unimplemented element $element").definite()
    }

    override fun visitFile(declaration: IrFile, data: ContextData): DefiniteTransformResult<CtCompilationUnit> {
        sourceHelper = PsiSourceHelper(sourceManager, data.file) // FIXME
        val module = helper.getOrCreateModule()
        val compilationUnit = factory.CompilationUnit().getOrCreate(declaration.path)

        val pkg = if(declaration.packageFragmentDescriptor.fqName.isRoot) module.rootPackage else
            factory.Package().getOrCreate(declaration.packageFragmentDescriptor.fqName.asString(), module)

        val lineSeparatorPositions = declaration.fileEntry.lineStartOffsets
        compilationUnit.declaredPackage = pkg
        compilationUnit.lineSeparatorPositions = lineSeparatorPositions
        compilationUnit.transformAndAddAnnotations(declaration, data)
        for(subDeclaration in declaration.declarations) {
            val ctDecl = subDeclaration.accept(this, Empty(declaration)).resultUnsafe
            ctDecl.setPosition<CtType<*>>(SourcePositionImpl(
                compilationUnit,
                subDeclaration.startOffset,
                subDeclaration.endOffset,
                lineSeparatorPositions)
            )
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
                        ctDecl.putKtMetadata<CtTypeMember>(KtMetadataKeys.TOP_LEVEL_DECLARING_CU, KtMetadata.element(compilationUnit))
                    })
                    topLvl.addTypeMember<CtClass<Any>>(ctDecl)
                }
            }
        }

        return compilationUnit.definite()
    }

    private fun filterModifiers(ctElement: CtElement, predicate: (KtModifierKind) -> Boolean) {
        val modifiers = ctElement.getMetadata(KtMetadataKeys.KT_MODIFIERS) as Set<KtModifierKind>? ?: return
        val newModifiers = modifiers.filter { predicate(it) }
        ctElement.addModifiersAsMetadata(newModifiers)
    }

    override fun visitClass(declaration: IrClass, data: ContextData): DefiniteTransformResult<CtType<*>> {
        val module = helper.getOrCreateModule()
        val type = helper.createType(declaration, data)
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

        for(decl in declaration.declarations) {
            if(
                decl.isFakeOverride ||
                decl.origin == IrDeclarationOrigin.ENUM_CLASS_SPECIAL_MEMBER ||
                decl.origin == IrDeclarationOrigin.GENERATED_DATA_CLASS_MEMBER
            ) {
                continue
            }
            val ctDecl = decl.accept(this, data).resultUnsafe
            ctDecl.setParent(type)
            when(ctDecl) {
                is CtEnumValue<*> -> {
                    (type as CtEnum<Enum<*>>).addEnumValue<CtEnum<Enum<*>>>(ctDecl)
                }
                is CtField<*> -> {
                    if(isObject) { // Ir object members are protected, but that's illegal in syntax
                        filterModifiers(ctDecl) { it != KtModifierKind.PROTECTED }
                    }
                    type.addField(ctDecl)
                }
                is CtMethod<*> -> {
                    if (declaration.isInterface && ctDecl.body != null) {
                        ctDecl.setDefaultMethod<Nothing>(true)
                    }
                    if(isObject) {
                        filterModifiers(ctDecl) { it != KtModifierKind.PROTECTED }
                    }
                    type.addMethod(ctDecl)
                }
                is CtConstructor<*> -> {
                    if (type is CtClass<*>) {
                        (type as CtClass<Any>).addConstructor<CtClass<Any>>(ctDecl as CtConstructor<Any>)
                        if(isObject)
                            ctDecl.setImplicit<CtElement>(true)
                    } else if (type is CtAnnotationType<*>) {
                        (type as CtAnnotationType<Annotation>).addTypeMember<CtAnnotationType<Annotation>>(ctDecl as CtConstructor<Any>)
                    }
                }
                is CtTypeMember -> {
                    type.addTypeMember(ctDecl)
                }
            }
        }

        return type.definite()
    }

    override fun visitAnonymousInitializer(
        declaration: IrAnonymousInitializer,
        data: ContextData
    ): DefiniteTransformResult<CtElement> {
        val ctAnonExecutable = core.createAnonymousExecutable()
        val body = visitBody(declaration.body, data).resultSafe
        ctAnonExecutable.setBody<CtAnonymousExecutable>(body)
        ctAnonExecutable.transformAndAddAnnotations(declaration, data)
        return ctAnonExecutable.definite()
    }

    override fun visitGetClass(expression: IrGetClass, data: ContextData): DefiniteTransformResult<CtElement> {
        val target = expression.argument.accept(this, data).resultUnsafe
        target.putKtMetadata(KtMetadataKeys.IS_CLASS_REFERENCE, KtMetadata.bool(true))
        return target.definite()
    }

    override fun visitClassReference(expression: IrClassReference, data: ContextData): DefiniteTransformResult<CtElement> {
        val access = createTypeAccess(expression.classType)
        access.putKtMetadata(KtMetadataKeys.IS_CLASS_REFERENCE, KtMetadata.bool(true))
        return access.definite()
    }

    override fun visitTypeParameter(declaration: IrTypeParameter, data: ContextData):
            DefiniteTransformResult<CtTypeParameter> {
        val ctTypeParam = factory.Core().createTypeParameter()
        ctTypeParam.setSimpleName<CtTypeParameter>(declaration.name.escaped())
        ctTypeParam.transformAndAddAnnotations(declaration, data)
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
        return ctTypeParam.definite()
    }

    override fun <T> visitConst(expression: IrConst<T>, data: ContextData): DefiniteTransformResult<CtLiteral<*>> {
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
                ctLiteral.setBase<CtLiteral<T>>(helper.getBaseOfConst(expression as IrConst<Number>, data.file))
            } else if (value is String) {
                var quotes = 0
                var i = expression.startOffset
                val text = getSourceHelper(data).sourceText
                while(quotes < 3 && i > kotlin.math.max(0, expression.startOffset - 5)) {
                    if(text[i] == '"') {
                        quotes += 1
                    } else if(quotes > 0) {
                        break
                    }
                    i -= 1
                }
                val isMultiLine = quotes == 3
                ctLiteral.putKtMetadata(KtMetadataKeys.STRING_LITERAL_MULTILINE, KtMetadata.bool(isMultiLine))
            }
        }
        return ctLiteral.definite()
    }

    override fun visitStringConcatenation(
        expression: IrStringConcatenation,
        data: ContextData
    ): DefiniteTransformResult<CtNewArray<*>> {
        val ctPlaceholder = core.createNewArray<Any>()
        val args = expression.arguments.map { it.accept(this, data).resultUnsafe.apply { setParent(ctPlaceholder) } }
        val isMultiLine = getSourceHelper(data).sourceTextIs(expression) { it.startsWith("\"\"\"") }
        ctPlaceholder.putKtMetadata(KtMetadataKeys.STRING_LITERAL_MULTILINE, KtMetadata.bool(isMultiLine))
        ctPlaceholder.putKtMetadata(KtMetadataKeys.STRING_CONCAT_ELEMENTS, KtMetadata.elementList(args))
        return ctPlaceholder.definite()
    }

    /**
     * val l = listOf(Pair(1,2)) or any other data class type
     * l.forEach { (k,v) -> ... }
     * > translates to >
     * l.forEach { <name for destructuring parameter 0> ->
     *      val k = <name for destructuring parameter 0>.component1
     *      val v = <name for destructuring parameter 0>.component2
     *      ...
     * }
     */
    private fun visitLambdaWithDestructuredValueParam(ctLambda: CtLambda<Any>, function: IrFunction, data: ContextData): CtLambda<*> {
        val m = mutableMapOf<String, CtParameter<Any>>()
        val body = ArrayList<IrStatement>()
        for(stmt in function.body!!.statements) {
            if(stmt is IrVariable
                && stmt.initializer is IrCall
                && (stmt.initializer as IrCall).origin is IrStatementOrigin.COMPONENT_N) {
                val initializer = stmt.initializer as IrCall
                val receiverOrTypeOperator = helper.getReceiver(initializer)
                val receiver = if(receiverOrTypeOperator is IrTypeOperatorCall) {
                    receiverOrTypeOperator.argument
                } else {
                    receiverOrTypeOperator
                }
                val name = (receiver as IrGetValue).symbol.descriptor.name.asString()
                val placeholder = m.getOrPut(name) {
                    core.createParameter<Any>().also {
                        it.setSimpleName<CtParameter<*>>(name)
                        it.setType<CtParameter<*>>(referenceBuilder.getNewTypeReference(stmt.type))
                        it.setImplicit<CtParameter<*>>(true)
                        it.putKtMetadata(KtMetadataKeys.IS_DESTRUCTURED, KtMetadata.bool(true))
                        it.putKtMetadata(KtMetadataKeys.COMPONENTS, KtMetadata.elementList(ArrayList<CtLocalVariable<*>>()))
                    }
                }
                val components = placeholder.getMetadata(KtMetadataKeys.COMPONENTS) as ArrayList<CtLocalVariable<*>>
                val component = visitVariable(stmt, data).resultSafe
                components.add(component)
            } else {
                body.add(stmt)
            }
        }

        val newBody = IrBlockBodyImpl(
            function.body!!.startOffset,
            function.body!!.endOffset,
            body
            )
        ctLambda.setBody<CtLambda<*>>(visitBody(newBody, data).resultSafe)

        fun addPotentialWildcards(placeholder: CtParameter<*>, param: IrValueParameter) {
            val components = placeholder.getMetadata(KtMetadataKeys.COMPONENTS) as ArrayList<CtLocalVariable<*>>
            val allComponents = sourceHelper.destructuredNames(param)
            if("_" in allComponents) {
                val newComponents = ArrayList<CtLocalVariable<*>>(components.size)
                var oldComponentIndex = 0
                for(c in allComponents) {
                    if(c == "_") newComponents.add(createWildcardVariable())
                    else newComponents.add(components[oldComponentIndex++])
                }
                placeholder.putKtMetadata(KtMetadataKeys.COMPONENTS, KtMetadata.elementList(newComponents))
            }
        }

        val params = ArrayList<CtParameter<*>>()
        for(param in function.valueParameters) {
            val placeholder = m[param.name.asString()]
            if(placeholder == null) {
                params.add(visitValueParameter(param, data).resultSafe)
            } else {
                addPotentialWildcards(placeholder, param)
                params.add(placeholder)
            }
        }
        ctLambda.setParameters<CtLambda<Any>>(params)
        return ctLambda
    }

    override fun visitFunctionExpression(
        expression: IrFunctionExpression,
        data: ContextData
    ): DefiniteTransformResult<CtLambda<*>> {
        val ctLambda = core.createLambda<Any>()
        if(expression.function.valueParameters.any { it.descriptor is ValueParameterDescriptorImpl.WithDestructuringDeclaration }) {
            visitLambdaWithDestructuredValueParam(ctLambda, expression.function, data)
        } else {
            ctLambda.setBody<CtLambda<*>>(visitBody(expression.function.body!!,data).resultSafe)
            ctLambda.setParameters<CtLambda<Any>>(expression.function.valueParameters.map {
                visitValueParameter(it, data).resultSafe
            })
        }
        ctLambda.setType<CtLambda<*>>(referenceBuilder.getNewTypeReference(expression.function.returnType))
        ctLambda.putKtMetadata(KtMetadataKeys.LAMBDA_AS_ANONYMOUS_FUNCTION,
            KtMetadata.bool(expression.origin == IrStatementOrigin.ANONYMOUS_FUNCTION))
        return ctLambda.definite()
    }

    override fun visitGetField(expression: IrGetField, data: ContextData): TransformResult<CtElement> {
        val propertyRef = referenceBuilder.getNewVariableReference<Any>(expression.symbol.descriptor) as CtFieldReference<*>
        val read = createVariableRead(propertyRef)
        val descriptor = expression.symbol.descriptor
        if(descriptor is JavaPropertyDescriptor) {
            val receiver = expression.receiver?.accept(this, data)?.resultUnsafe
            if(receiver != null) {
                (read as CtFieldRead<Any>).setTarget<CtFieldRead<Any>>(expressionOrWrappedInStatementExpression(receiver))
            }
        } else {
            val isActualField = helper.isActualField(expression, data.file)
            propertyRef.putKtMetadata(KtMetadataKeys.IS_ACTUAL_FIELD, KtMetadata.bool(isActualField))
        }

        return read.definite()
    }

    override fun visitSetField(expression: IrSetField, data: ContextData): TransformResult<CtElement> {
        val propertyRef = referenceBuilder.getNewVariableReference<Any>(expression.symbol.descriptor) as CtFieldReference<*>
        val isActualField = helper.isActualField(expression, data.file)
        propertyRef.putKtMetadata(KtMetadataKeys.IS_ACTUAL_FIELD, KtMetadata.bool(isActualField))
        val write = createVariableWrite(null, propertyRef)
        val rhs = expression.value.accept(this, data).resultUnsafe
        return createAssignment(write, expressionOrWrappedInStatementExpression(rhs)).definite()
    }

    override fun visitPropertyReference(
        expression: IrPropertyReference,
        data: ContextData
    ): DefiniteTransformResult<CtFieldRead<*>> {
        val field = referenceBuilder.getNewVariableReference<Any>(expression.symbol.descriptor)
        val read = createVariableRead(field) as CtFieldRead<*>
        read.putKtMetadata(KtMetadataKeys.IS_PROPERTY_REFERENCE, KtMetadata.bool(true))
        return read.definite()
    }

    override fun visitProperty(declaration: IrProperty, data: ContextData): DefiniteTransformResult<CtElement> {
        val ctField = core.createField<Any>()
        ctField.setSimpleName<CtField<*>>(declaration.name.escaped())
        ctField.transformAndAddAnnotations(declaration, data)
        // Initializer (if any) exists in backing field initializer
        val backingField = declaration.backingField
        val initializer = backingField?.initializer
        if(initializer != null) {
            val ctInitializer = visitExpressionBody(initializer, data).resultUnsafe

            if(backingField.origin == IrDeclarationOrigin.DELEGATE) {
                ctField.putKtMetadata<CtElement>(KtMetadataKeys.PROPERTY_DELEGATE, KtMetadata.element(ctInitializer))
                ctInitializer.setParent(ctField)
            } else {
                ctField.setDefaultExpression<CtField<Any>>(
                    expressionOrWrappedInStatementExpression(ctInitializer))
            }

            // Check if property stems from primary constructor parameter
            val getVal = initializer.expression
            if(getVal is IrValueAccessExpression &&
                getVal.origin == IrStatementOrigin.INITIALIZE_PROPERTY_FROM_PARAMETER) {
                ctField.setImplicit<CtField<*>>(true)
            }
        }

        // Modifiers
        ctField.addModifiersAsMetadata(IrToModifierKind.fromProperty(declaration))

        // Type
        val type = referenceBuilder.getNewTypeReference<Any>(declaration.getter!!.returnType)

        // Mark implicit/explicit type
        val implicitType = detectImplicitTypes &&
                !getSourceHelper(data).hasExplicitType(declaration.descriptor.source.getPsi() as? KtProperty?)
        type.setImplicit<CtTypeReference<*>>(implicitType)
        ctField.setType<CtField<*>>(type)

        if(!declaration.isDelegated) { // Custom getter/setter is illegal for delegated properties
            val getter = declaration.getter
            if(getter != null && getter.origin != IrDeclarationOrigin.DEFAULT_PROPERTY_ACCESSOR) {
                val getterFunction = createUnnamedFunction(getter, data)
                ctField.putKtMetadata<CtField<*>>(KtMetadataKeys.PROPERTY_GETTER,
                    KtMetadata.element(getterFunction))
                getterFunction.setParent(ctField)
            }

            val setter = declaration.setter
            if(setter != null && setter.origin != IrDeclarationOrigin.DEFAULT_PROPERTY_ACCESSOR) {
                val setterFunction = createUnnamedFunction(setter, data)
                ctField.putKtMetadata<CtField<*>>(KtMetadataKeys.PROPERTY_SETTER,
                    KtMetadata.element(setterFunction))
                setterFunction.setParent(ctField)
            }
        }

        return ctField.definite()
    }

    override fun visitDelegatingConstructorCall(
        expression: IrDelegatingConstructorCall,
        data: ContextData
    ): DefiniteTransformResult<CtElement> {
        val ctConstructorCall = core.createConstructorCall<Any>()
        ctConstructorCall.setExecutable<CtConstructorCall<Any>>(referenceBuilder.getNewDelegatingExecutableReference(expression))

        val valueArgs = ArrayList<CtExpression<*>>(expression.valueArgumentsCount)
        for(i in 0 until expression.valueArgumentsCount) {
            val ctExpr = expression.getValueArgument(i)!!.accept(this, data).resultUnsafe
            valueArgs.add(expressionOrWrappedInStatementExpression(ctExpr))
        }
        if(valueArgs.isNotEmpty()) {
            ctConstructorCall.setArguments<CtConstructorCall<Any>>(valueArgs)
        }
        return ctConstructorCall.definite()
    }

    override fun visitConstructor(declaration: IrConstructor, data: ContextData): DefiniteTransformResult<CtElement> {
        val ctConstructor = core.createConstructor<Any>()
        ctConstructor.setSimpleName<CtConstructor<*>>(declaration.name.asString())
        ctConstructor.transformAndAddAnnotations(declaration, data)
        val modifierList = listOfNotNull(KtModifierKind.convertVisibility(declaration.visibility))
        ctConstructor.setImplicit<CtConstructor<Any>>(declaration.isPrimary &&
                declaration.valueParameters.isEmpty() &&
                declaration.descriptor.source.getPsi() !is KtPrimaryConstructor &&
                modifierList.filterNot { it == KtModifierKind.PUBLIC }.isEmpty()
        )
        ctConstructor.putKtMetadata(
            KtMetadataKeys.KT_MODIFIERS,
            KtMetadata.modifierKind(modifierList))
        ctConstructor.putKtMetadata(
            KtMetadataKeys.CONSTRUCTOR_IS_PRIMARY,
            KtMetadata.bool(declaration.isPrimary))

        // Add body
        val body = declaration.body?.accept(this, data)?.resultOrNull as CtStatement?
        if(body != null) {
            ctConstructor.setBody<CtConstructor<*>>(body)
        }

        for(valueParam in declaration.valueParameters) {
            val ctParam = visitValueParameter(valueParam, data).resultSafe

            /*
            * Primary constructor property declaration creates implicit properties in the class. An implicit property is the
            * holder of the val/var modifier, not the parameter:
            * ClassName(var x = 2) >translates to> ClassName(x = 2) { var x = x }
            * To facilitate printing, we look in the PSI if the parameter has modifiers and add them to metadata.
            *
            * TODO: Perhaps add metadata mapping property <-> param?
            *  */
            if(declaration.isPrimary) {
                val pModifiers = (ctParam.getMetadata(KtMetadataKeys.KT_MODIFIERS) as MutableSet<KtModifierKind>?) ?:
                mutableSetOf<KtModifierKind>()

                val psiModifierList = valueParam.descriptor.source.getPsi()?.getChildOfType<KtModifierList>()?.let {
                        list -> KtModifierKind.fromPsiModifierList(list)
                    } ?: emptyList()

                pModifiers.addAll(psiModifierList)
                // Var/val might be outside of modifier list
                val psiTokens = valueParam.descriptor.source.getPsi()?.getChildrenOfType<LeafPsiElement>()
                if(psiTokens != null) {
                    for(token in psiTokens)
                        if(token.elementType == KtTokens.VAL_KEYWORD) {
                            pModifiers.add(KtModifierKind.VAL)
                        } else if(token.elementType == KtTokens.VAR_KEYWORD) {
                            pModifiers.add(KtModifierKind.VAR)
                        }
                }
                ctParam.putMetadata<CtParameter<*>>(KtMetadataKeys.KT_MODIFIERS, pModifiers)
            }
            ctConstructor.addParameter<CtConstructor<Any>>(ctParam)
        }
        return ctConstructor.definite()
    }

    override fun visitInstanceInitializerCall(
        expression: IrInstanceInitializerCall,
        data: ContextData
    ): EmptyTransformResult<CtElement> {
        return EmptyTransformResult()
    }


    override fun visitVariable(declaration: IrVariable, data: ContextData): DefiniteTransformResult<CtLocalVariable<*>> {
        val ctLocalVar = core.createLocalVariable<Any>()
        ctLocalVar.setSimpleName<CtVariable<*>>(declaration.name.escaped())
        ctLocalVar.transformAndAddAnnotations(declaration, data)
        // Initializer
        if(declaration.origin != IrDeclarationOrigin.FOR_LOOP_VARIABLE &&
                data !is Destruct) {
            val initializer = declaration.initializer?.accept(this, data)?.resultUnsafe
            if (initializer != null) {
                val initializerExpr = expressionOrWrappedInStatementExpression(initializer)
                ctLocalVar.setDefaultExpression<CtLocalVariable<Any>>(initializerExpr)
            }

            // Modifiers
            ctLocalVar.addModifiersAsMetadata(IrToModifierKind.fromVariable(declaration))
        }

        // Type
        val type = referenceBuilder.getNewTypeReference<Any>(declaration.type)
        ctLocalVar.setType<CtLocalVariable<*>>(type)

        // Mark implicit/explicit type
        val implicitType = detectImplicitTypes &&
                !getSourceHelper(data).hasExplicitType(declaration.descriptor.source.getPsi() as? KtProperty?)
        type.setImplicit<CtTypeReference<*>>(implicitType)
        ctLocalVar.setType<CtVariable<*>>(type)
        ctLocalVar.setInferred<CtLocalVariable<Any>>(implicitType)

        return ctLocalVar.definite()
    }

    override fun visitFunctionReference(
        expression: IrFunctionReference,
        data: ContextData
    ): DefiniteTransformResult<CtExecutableReferenceExpression<*,CtExpression<*>>> {
        val ctExpr = core.createExecutableReferenceExpression<Any, CtExpression<*>>()
        ctExpr.setExecutable<CtExecutableReferenceExpression<Any, CtExpression<*>>>(
            referenceBuilder.getNewExecutableReference<Any>(expression)
        )
        val receiver = expression.extensionReceiver ?: expression.dispatchReceiver
        val target = receiver?.accept(this, data)?.resultUnsafe ?:
            factory.Code().createTypeAccess(
                referenceBuilder.getDeclaringTypeReference(
                    expression.symbol.descriptor.containingDeclaration),
                false)

        ctExpr.setTarget<CtExecutableReferenceExpression<Any,CtExpression<*>>>(
            expressionOrWrappedInStatementExpression(target))
        return ctExpr.definite()
    }

    fun visitAnnotation(constructorCall: IrConstructorCall, data: ContextData): DefiniteTransformResult<CtAnnotation<*>> {
        val annotation = factory.Code().createAnnotation(referenceBuilder.getNewTypeReference(constructorCall.type))
        if(constructorCall.valueArgumentsCount > 0) {
            val m = mutableMapOf<String, CtExpression<*>>()
            var n = 0
            for(i in 0 until constructorCall.valueArgumentsCount) {
                val arg = constructorCall.getValueArgument(i)!!
                if(arg is IrVararg) {
                    val vs = visitVararg(arg, data).compositeResultSafe
                    for(v in vs) {
                        m[n.toString()] = v
                        n++
                    }
                } else {
                    val v = expressionOrWrappedInStatementExpression(arg.accept(this, data).resultUnsafe)
                    m[n.toString()] = v
                    n++
                }
            }
            annotation.setValues<CtAnnotation<Annotation>>(m)
        }
        return annotation.definite()
    }

    override fun visitValueParameter(
        declaration: IrValueParameter,
        data: ContextData
    ): DefiniteTransformResult<CtParameter<*>> {
        val ctParam = core.createParameter<Any>()
        ctParam.setSimpleName<CtParameter<Any>>(declaration.name.escaped())
        ctParam.setInferred<CtParameter<Any>>(false) // Not allowed
        ctParam.transformAndAddAnnotations(declaration, data)

        // Modifiers
        val modifierList = IrToModifierKind.fromValueParameter(declaration)
        ctParam.addModifiersAsMetadata(modifierList)
        ctParam.setVarArgs<CtParameter<Any>>(KtModifierKind.VARARG in modifierList)

        val defaultValue = declaration.defaultValue?.let { visitExpressionBody(it, data) }?.resultUnsafe
        if(defaultValue != null) {
            ctParam.putKtMetadata(KtMetadataKeys.PARAMETER_DEFAULT_VALUE,
               KtMetadata.element(defaultValue))
            defaultValue.setParent(ctParam)
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

        return ctParam.definite()
    }

    private fun CtElement.transformAndAddAnnotations(irElement: IrAnnotationContainer, data: ContextData) {
        setAnnotations<CtElement>(irElement.annotations.map { visitAnnotation(it, data).resultSafe })
    }

    private fun createUnnamedFunction(irFunction: IrFunction, data: ContextData): CtMethod<*> {
        val ctMethod = core.createMethod<Any>()

        ctMethod.transformAndAddAnnotations(irFunction, data)

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
        val type = referenceBuilder.getNewTypeReference<Any>(irFunction.returnType)
        if(detectImplicitTypes && !getSourceHelper(data).hasExplicitType(
                irFunction.descriptor.source.getPsi() as? KtFunction?)) {
            type.setImplicit<CtTypeReference<*>>(true)
        }
        ctMethod.setType<CtMethod<*>>(type)

        // Extension receiver
        val extensionReceiverRef = irFunction.extensionReceiverParameter?.type?.let {
            createTypeAccess(it)
        }
        if(extensionReceiverRef != null) {
            extensionReceiverRef.setParent<CtElement>(ctMethod)
            ctMethod.putKtMetadata(KtMetadataKeys.EXTENSION_TYPE_REF,
                KtMetadata.element(extensionReceiverRef)
            )
        }

        // Body
        val body = irFunction.body
        if(body != null) {
            val ctBody = visitBody(body, data).resultSafe
            if(ctBody.statements.size == 1) {
                val expr = body.statements[0]
                if(expr is IrReturn && expr.endOffset - expr.startOffset <= 1) {
                    ctBody.setImplicit<CtBlock<*>>(true)
                }
            }
            ctMethod.setBody<CtMethod<Any>>(ctBody)
        }
        return ctMethod
    }

    override fun visitSimpleFunction(
        declaration: IrSimpleFunction,
        data: ContextData
    ): DefiniteTransformResult<CtMethod<*>> {
        return createUnnamedFunction(declaration, data).also {
            it.setSimpleName<CtMethod<*>>(declaration.name.escaped())
            it.addModifiersAsMetadata(IrToModifierKind.fromFunctionDeclaration(declaration))
        }.definite()
    }

    override fun visitBody(body: IrBody, data: ContextData): DefiniteTransformResult<CtBlock<*>> {
        val ctBlock = core.createBlock<Any>()
        val statements = ArrayList<CtStatement>()
        for(irStatement in body.statements) {
            if(irStatement is IrDeclaration && irStatement.isFakeOverride) continue
            val result = irStatement.accept(this, data)
            if(result.isNothing) continue
            val ctResult = result.resultUnsafe
            val ctStatement = if(ctResult is CtMethod<*>) ctResult.wrapLocalMethod() else
                statementOrWrappedInImplicitReturn(ctResult)
            checkLabelOfStatement(irStatement, ctStatement, data)
            statements.add(ctStatement)
        }
        ctBlock.setStatements<CtBlock<*>>(statements)
        return ctBlock.definite()
    }

    private fun createAssignment(lhs: CtExpression<Any>, rhs: CtExpression<Any>): CtAssignment<Any, Any> {
        val ctAssignment = core.createAssignment<Any,Any>()
        ctAssignment.setAssigned<CtAssignment<Any, Any>>(lhs)
        ctAssignment.setAssignment<CtAssignment<Any, Any>>(rhs)
        return ctAssignment
    }

    private fun createAssignment(irCall: IrCall, data: ContextData): TransformResult<CtElement> {
        val callDescriptor = irCall.symbol.descriptor as PropertySetterDescriptor
        val receiver = getReceiver(irCall, data) as CtExpression<*>?
        val lhs = createVariableWrite(receiver, referenceBuilder.getNewVariableReference<Any>(
            callDescriptor.correspondingProperty))
        val rhs = irCall.getValueArgument(0)!!.accept(this, data).resultUnsafe
        return createAssignment(lhs, expressionOrWrappedInStatementExpression(rhs)).also {
            it.setType<CtExpression<*>>(referenceBuilder.getNewTypeReference(irCall.type))
        }.definite()
    }


    private fun createCheckNotNullAccess(call: IrCall, data: ContextData): TransformResult<CtElement> {
        val access = call.getValueArgument(0)!!.accept(this, data)
        return access.apply {
            resultUnsafe.putKtMetadata(KtMetadataKeys.ACCESS_IS_CHECK_NOT_NULL, KtMetadata.bool(true))
        }
    }

    private fun specialInvocation(irCall: IrCall, data: ContextData): TransformResult<CtElement> {
        val callDescriptor = irCall.symbol.descriptor
        if(callDescriptor is PropertyGetterDescriptor) {
            return visitPropertyAccess(irCall, data)
        }
        if((irCall.origin == IrStatementOrigin.EQ || irCall.origin in AUGMENTED_ASSIGNMENTS)
            && irCall.symbol.descriptor.name.asString() == "set"
            && irCall.symbol.descriptor.isOperator
        ) {
            // Can't be in 'when' below because of multiple criteria. Will block potential matches
            val operator = if(irCall.origin == IrStatementOrigin.EQ) null else
                OperatorHelper.originToBinaryOperatorKind(irCall.origin!!)
            return createSetOperator(irCall, data, operator)
        }
        when(irCall.origin) {
            IrStatementOrigin.EXCLEXCL -> return createCheckNotNullAccess(irCall, data)
            IrStatementOrigin.GET_PROPERTY -> return visitPropertyAccess(irCall, data)
            IrStatementOrigin.GET_ARRAY_ELEMENT -> return createGetOperator(irCall, data)
            IrStatementOrigin.FOR_LOOP_ITERATOR -> {
                return createInvocation(irCall, data).resultSafe.target.definite()
            }
            in AUGMENTED_ASSIGNMENTS -> return createAugmentedAssignmentOperator(irCall, irCall.origin!!, data).definite()
            IrStatementOrigin.EQ -> {
                return if(callDescriptor is PropertySetterDescriptor) {
                    createAssignment(irCall, data)
                } else {
                    createInvocation(irCall, data).also { it.resultSafe.putKtMetadata(
                        KtMetadataKeys.SET_AS_OPERATOR,
                        KtMetadata.bool(true)
                    ) }
                }
            }
            in INCREMENT_DECREMENT_OPERATORS -> {
                val receiver = getReceiver(irCall, data) as CtExpression<*>?
                val variable = referenceBuilder.getNewVariableReference<Any>(
                    (callDescriptor as PropertySetterDescriptor).correspondingProperty)
                return createVariableWrite(receiver, variable).definite()
            }
        }
        if(OperatorHelper.isUnaryOperator(irCall.origin)) {
            return visitUnaryOperator(irCall, data)
        }
        if(OperatorHelper.isBinaryOperator(irCall.origin)) {
            return visitBinaryOperator(irCall, data)
        }
        return TransformResult.nothing()
    }

    private fun createOrOperator(expr: IrIfThenElseImpl, data: ContextData): DefiniteTransformResult<CtBinaryOperator<Boolean>> {
        val irLhs = expr.branches.first { it !is IrElseBranchImpl }.condition
        val irRhs = expr.branches.firstIsInstance<IrElseBranchImpl>().result
        val lhs = irLhs.accept(this, data).resultUnsafe
        val rhs = irRhs.accept(this, data).resultUnsafe
        val ctOp = core.createBinaryOperator<Boolean>()
        ctOp.putKtMetadata(KtMetadataKeys.KT_BINARY_OPERATOR_KIND,
            KtMetadata.binOpKind(KtBinaryOperatorKind.OR))
        ctOp.setLeftHandOperand<CtBinaryOperator<Boolean>>(expressionOrWrappedInStatementExpression(lhs))
        ctOp.setRightHandOperand<CtBinaryOperator<Boolean>>(expressionOrWrappedInStatementExpression(rhs))
        ctOp.setType<CtBinaryOperator<Boolean>>(referenceBuilder.getNewTypeReference(expr.type))
        return ctOp.definite()
    }

    private fun createAndOperator(expr: IrIfThenElseImpl, data: ContextData): DefiniteTransformResult<CtBinaryOperator<Boolean>> {
        val branch = expr.branches.first { it !is IrElseBranchImpl }
        val irLhs = branch.condition
        val irRhs = branch.result
        val lhs = irLhs.accept(this, data).resultUnsafe
        val rhs = irRhs.accept(this, data).resultUnsafe
        val ctOp = core.createBinaryOperator<Boolean>()
        ctOp.putKtMetadata(KtMetadataKeys.KT_BINARY_OPERATOR_KIND,
            KtMetadata.binOpKind(KtBinaryOperatorKind.AND))
        ctOp.setLeftHandOperand<CtBinaryOperator<Boolean>>(expressionOrWrappedInStatementExpression(lhs))
        ctOp.setRightHandOperand<CtBinaryOperator<Boolean>>(expressionOrWrappedInStatementExpression(rhs))
        ctOp.setType<CtBinaryOperator<Boolean>>(referenceBuilder.getNewTypeReference(expr.type))
        return ctOp.definite()
    }

    override fun visitWhen(expression: IrWhen, data: ContextData): DefiniteTransformResult<CtElement> {
        if(expression is IrIfThenElseImpl) {
            if(expression.origin == IrStatementOrigin.OROR) {
                return createOrOperator(expression, data)
            }
            if(expression.origin == IrStatementOrigin.ANDAND) {
                return createAndOperator(expression, data)
            }
            return visitIfThenElse(expression, data)
        }

        // Use only when as expression
        val ctSwitch = core.createSwitchExpression<Any,Any>()
        val context: ContextData
        if(data is When) { // Coming from a block that declares a subject
            context = data
            val subject = helper.getWhenSubjectVarDeclaration(context.subject)?.accept(this, data)?.resultUnsafe
            if(subject != null) {
                ctSwitch.setSelector<CtSwitchExpression<*,Any>>(expressionOrWrappedInStatementExpression(subject))
            }
        } else { // No subject
            context = When(data, null)
        }

        ctSwitch.setCases<CtSwitchExpression<*,Any>>(
            expression.branches.mapNotNull { visitBranch(it, context).resultOrNull }
        )

        ctSwitch.setType<CtExpression<*>>(referenceBuilder.getNewTypeReference(expression.type))

        return ctSwitch.definite()
    }

    override fun visitBranch(branch: IrBranch, data: ContextData): MaybeTransformResult<CtCase<Any>> {
        if(branch is IrElseBranch) {
            val result = branch.result
            if(result is IrCall && result.symbol.owner is IrBuiltInOperator) {
                return EmptyTransformResult()
            }
        }

        fun markImplicitLHS(expr: CtElement) {
            when(expr) {
                is CtBinaryOperator<*> -> {
                    when(expr.getMetadata(KtMetadataKeys.KT_BINARY_OPERATOR_KIND) as KtBinaryOperatorKind?) {
                        KtBinaryOperatorKind.IS,
                        KtBinaryOperatorKind.IS_NOT,
                        KtBinaryOperatorKind.IN,
                        KtBinaryOperatorKind.NOT_IN -> expr.leftHandOperand?.setImplicit<CtElement>(true)
                        else -> { /* Nothing */ }
                    }
                }
            }
        }

        val case = core.createCase<Any>()
        case.setCaseKind<CtCase<Any>>(CaseKind.ARROW)
        val context = Empty(data.file)  // Reset context, when-subject is tied to this level
        case.setCaseExpressions<CtCase<Any>>(
            helper.resolveBranchMultiCondition(branch, (data as When).subject).map {
                expressionOrWrappedInStatementExpression(it.first.accept(this, context).resultUnsafe).also {
                    result ->
                    result.setParent(case)
                    if(it.second) markImplicitLHS(result)
                }
            }
        )

        val result = branch.result.accept(this, context).resultUnsafe.blockOrSingleStatementBlock()
        case.addStatement<CtCase<Any>>(result)
        return case.definite()
    }

    fun visitIfThenElse(ifThenElse: IrIfThenElseImpl, data: ContextData): DefiniteTransformResult<CtIf> {
        val ctIf = core.createIf()
        val thenBranch = ifThenElse.branches.first { it !is IrElseBranch }
        val elseBranch = ifThenElse.branches.firstIsInstanceOrNull<IrElseBranch>()
        val condition = thenBranch.condition.accept(this, data).resultUnsafe
        val thenResult = thenBranch.result.accept(this, data).resultUnsafe

        ctIf.setCondition<CtIf>(condition as CtExpression<Boolean>)
        ctIf.setThenStatement<CtIf>(statementOrWrappedInImplicitReturn(thenResult))
        if(elseBranch != null) {
            val elseResult = elseBranch.result.accept(this,data).resultUnsafe
            ctIf.setElseStatement<CtIf>(statementOrWrappedInImplicitReturn(elseResult))
        }

        val type = referenceBuilder.getNewTypeReference<Any>(ifThenElse.type)
        ctIf.putKtMetadata(KtMetadataKeys.KT_STATEMENT_TYPE, KtMetadata.element(type))

        return ctIf.definite()
    }

    private fun getPossibleJavaReceiver(irCall: IrFunctionAccessExpression): CtTypeAccess<*>? {
        val descriptor = irCall.symbol.descriptor
        if(descriptor is JavaMethodDescriptor) {
            val t = referenceBuilder.getDeclaringTypeReference(descriptor.containingDeclaration)
            return t?.let { createTypeAccess(it) }
        }
        if(descriptor is SimpleFunctionDescriptor) {
            if(descriptor.isSynthesized
                && descriptor.containingDeclaration is ClassDescriptor
                && (descriptor.containingDeclaration as ClassDescriptor).kind == ClassKind.ENUM_CLASS
            ) {
                val t = referenceBuilder.getDeclaringTypeReference(descriptor.containingDeclaration)
                return t?.let { createTypeAccess(it) }
            }
        }
        return null
    }

    private fun createInvocation(irCall: IrFunctionAccessExpression, data: ContextData, namedArgs: List<Pair<String?,IrExpression>>? = null)
            : DefiniteTransformResult<CtInvocation<*>> {
        val invocation = core.createInvocation<Any>()
        invocation.setExecutable<CtInvocation<Any>>(referenceBuilder.getNewExecutableReference(irCall))


        val target = getReceiver(irCall, data) ?: getPossibleJavaReceiver(irCall)
        if (target is CtExpression<*>) {
            invocation.setTarget<CtInvocation<Any>>(target)
        } else if (target != null) {
            throw RuntimeException("Function call target not CtExpression")
        }

        val arguments = ArrayList<CtExpression<*>>()
        if(namedArgs != null) {
            for(arg in namedArgs) {
                val irExpr = arg.second
                val ctExpr: CtExpression<*>
                ctExpr = if(irExpr is IrVararg) {
                    val spread = visitVararg(irExpr, data).compositeResultSafe
                    assert(spread.size == 1)
                    spread[0]
                } else {
                    expressionOrWrappedInStatementExpression(irExpr.accept(this, data).resultUnsafe)
                }
                if(arg.first != null) {
                    ctExpr.putKtMetadata(KtMetadataKeys.NAMED_ARGUMENT, KtMetadata.string(arg.first!!))
                }
                arguments.add(ctExpr)
            }
            invocation.setArguments<CtInvocation<Any>>(arguments)
        } else {
            if(irCall.valueArgumentsCount > 0) {
                for(i in 0 until irCall.valueArgumentsCount) {
                    val irExpr = irCall.getValueArgument(i) ?: continue
                    if(irExpr is IrVararg) {
                        arguments.addAll(visitVararg(irExpr, data).compositeResultSafe)
                    } else {
                        val ctExpr = irExpr.accept(this, data).resultUnsafe
                        val name = getSourceHelper(data).getNamedArgumentIfAny(irExpr)
                        if(name != null) {
                            ctExpr.putKtMetadata(KtMetadataKeys.NAMED_ARGUMENT, KtMetadata.string(name))
                        }
                        arguments.add(expressionOrWrappedInStatementExpression(ctExpr))
                    }
                }
                invocation.setArguments<CtInvocation<Any>>(arguments)
            }
        }

        if(irCall.typeArgumentsCount > 0) {
            val implicitTypeArguments = detectImplicitTypes && !getSourceHelper(data).sourceElementIs(irCall) { call ->
                call.children.any { it is KtTypeArgumentList }
            }
            invocation.setActualTypeArguments<CtInvocation<Any>>(
                irCall.symbol.descriptor.typeParameters.map {
                    referenceBuilder.getNewTypeReference<Any>(irCall.getTypeArgument(it.index)!!).also {
                            res -> res.setImplicit(implicitTypeArguments)
                    }
                }
            )
        }
        if(detectInfix && irCall is IrCall) {
            invocation.putKtMetadata(
                KtMetadataKeys.INVOCATION_IS_INFIX,
                KtMetadata.bool(helper.isInfixCall(irCall, data))
            )
        }
        if(irCall.origin == IrStatementOrigin.INVOKE) {
            invocation.putKtMetadata(
                KtMetadataKeys.INVOKE_AS_OPERATOR,
                KtMetadata.bool(true)
            )
        }

        return invocation.definite()
    }

    override fun visitCall(expression: IrCall, data: ContextData): DefiniteTransformResult<CtElement> {
        val nonInvocationResult = specialInvocation(expression, data)
        if(nonInvocationResult.isDefinite) return nonInvocationResult as DefiniteTransformResult<CtElement>
        return createInvocation(expression, data)
    }

    override fun visitConstructorCall(expression: IrConstructorCall, data: ContextData): DefiniteTransformResult<CtInvocation<*>> {
        return createInvocation(expression, data)
    }

    override fun visitEnumConstructorCall(
        expression: IrEnumConstructorCall,
        data: ContextData
    ): DefiniteTransformResult<CtInvocation<*>> {
        return createInvocation(expression, data)
    }

    override fun visitTypeAlias(declaration: IrTypeAlias, data: ContextData): DefiniteTransformResult<CtClass<*>> {
        val ctClass = core.createClass<Any>()
        ctClass.setSimpleName<CtClass<*>>(declaration.name.escaped())
        ctClass.setFormalCtTypeParameters<CtClass<*>>(declaration.typeParameters.map {
            visitTypeParameter(it, data).resultSafe
        })
        ctClass.transformAndAddAnnotations(declaration, data)
        ctClass.addModifiersAsMetadata(listOfNotNull(IrToModifierKind.convertVisibility(declaration.visibility)))
        ctClass.putKtMetadata(KtMetadataKeys.TYPE_ALIAS, KtMetadata.element(referenceBuilder.getNewTypeReference<Any>(declaration.expandedType)))
        return ctClass.definite()
    }

    override fun visitGetEnumValue(expression: IrGetEnumValue, data: ContextData): DefiniteTransformResult<CtFieldRead<*>> {
        val fieldRead = core.createFieldRead<Any>()
        fieldRead.setTarget<CtFieldRead<Any>>(createTypeAccess(expression.type))
        fieldRead.setVariable<CtFieldRead<Any>>(
            referenceBuilder.getNewVariableReference<Any>(expression)
        )
        return fieldRead.definite()
    }

    override fun visitEnumEntry(declaration: IrEnumEntry, data: ContextData): DefiniteTransformResult<CtEnumValue<*>> {
        val ctEnum = core.createEnumValue<Any>()
        ctEnum.setSimpleName<CtEnumValue<*>>(declaration.name.escaped())
        ctEnum.transformAndAddAnnotations(declaration, data)
        val constructorCall = declaration.initializerExpression!! as IrEnumConstructorCall
        val anonClass = declaration.correspondingClass
        if(anonClass == null) {
            ctEnum.setDefaultExpression<CtEnumValue<Any>>(
                visitEnumConstructorCall(constructorCall, data).resultSafe as CtInvocation<Any>
            )
        } else {
            val ctAnon = core.createNewClass<Any>()
            ctAnon.setAnonymousClass<CtNewClass<*>>(visitClass(anonClass, data).resultSafe as CtClass<*>)
            ctAnon.setExecutable<CtNewClass<Any>>(referenceBuilder.getNewConstructorExecutableReference(
                declaration.initializerExpression as IrEnumConstructorCall
            ))
            ctEnum.setDefaultExpression<CtEnumValue<Any>>(ctAnon)
        }
        ctEnum.setType<CtTypedElement<*>>(referenceBuilder.getNewTypeReference(declaration.parentAsClass.descriptor))
        return ctEnum.definite()
    }

    private fun visitBinaryOperator(irCall: IrCall, data: ContextData): DefiniteTransformResult<CtBinaryOperator<*>> {
        val tempLhs: IrExpression
        val tempRhs: IrExpression
        if(irCall.valueArgumentsCount == 2) {
            tempLhs = irCall.getValueArgument(0)!!
            tempRhs = irCall.getValueArgument(1)!!
        } else if(irCall.valueArgumentsCount == 1) {
            tempLhs = irCall.dispatchReceiver ?: irCall.extensionReceiver!!
            tempRhs = irCall.getValueArgument(0)!!
        } else {
            val receiver = irCall.dispatchReceiver
            if(receiver is IrCall)
                return visitBinaryOperator(receiver, data)
            else
                throw SpoonIrBuildException("Unable to get operands of binary operator call")
        }
        val opKind = OperatorHelper.originToBinaryOperatorKind(irCall.origin!!)
        val (irLhs, irRhs) = OperatorHelper.getOrderedBinaryOperands(tempLhs, tempRhs, opKind)
        val lhs = irLhs.accept(this, data).resultOrNull
        val rhs = irRhs.accept(this, data).resultUnsafe

        val ctOp = core.createBinaryOperator<Any>()

        if(lhs != null) {
            ctOp.setLeftHandOperand<CtBinaryOperator<Any>>(expressionOrWrappedInStatementExpression(lhs))
        }
        ctOp.setRightHandOperand<CtBinaryOperator<Any>>(expressionOrWrappedInStatementExpression(rhs))
        ctOp.setType<CtBinaryOperator<Any>>(referenceBuilder.getNewTypeReference(irCall.type))

        ctOp.putKtMetadata(
            KtMetadataKeys.KT_BINARY_OPERATOR_KIND,
            KtMetadata.binOpKind(opKind)
        )
        return ctOp.definite()
    }

    private fun visitUnaryOperator(irCall: IrCall, data: ContextData): DefiniteTransformResult<CtUnaryOperator<*>> {
        val ctOp = core.createUnaryOperator<Any>()
        val operand = irCall.dispatchReceiver!!.accept(this, data).resultUnsafe
        ctOp.setOperand<CtUnaryOperator<*>>(operand as CtExpression<Any>)
        ctOp.setKind<CtUnaryOperator<*>>(OperatorHelper.originToUnaryOperatorKind(irCall.origin!!))
        ctOp.setType<CtUnaryOperator<*>>(referenceBuilder.getNewTypeReference(irCall.type))
        return ctOp.definite()
    }

    private fun visitForLoop(outerBlock: IrBlock, data: ContextData): DefiniteTransformResult<CtForEach> {
        val ctForEach = core.createForEach()
        val iterable = (outerBlock.statements[0] as IrVariable).initializer!!.accept(this, data).resultUnsafe
        val whileLoop = (outerBlock.statements[1] as IrWhileLoop)
        val innerBlock = whileLoop.body as IrBlock
        val variables = innerBlock.statements.takeWhile { it is IrVariable }
        val context = Destruct(data)
        val variable = if(variables.size > 1) {
            val components = variables.drop(1).map { visitVariable(it as IrVariable, context).resultSafe }
            components.toDestructuredVariable()
        } else {
            visitVariable(variables[0] as IrVariable, data).resultSafe
        }

        val body = innerBlock.statements[variables.size].accept(this, data).resultUnsafe
        ctForEach.setVariable<CtForEach>(variable)
        ctForEach.setExpression<CtForEach>(iterable as CtExpression<*>)
        ctForEach.setBody<CtForEach>(body.blockOrSingleStatementBlock())
        ctForEach.setLabel<CtForEach>(whileLoop.label)
        return ctForEach.definite()
    }

    private fun visitElvisOperator(block: IrBlock, data: ContextData): DefiniteTransformResult<CtBinaryOperator<Any>> {
        val rhsIf = block.statements.firstIsInstance<IrIfThenElseImpl>()
        val lhs = (block.statements[0] as IrVariable).initializer!!.accept(this, data).resultUnsafe
        val rhs = rhsIf.branches[0].result.accept(this, data).resultUnsafe
        val ctOperator = core.createBinaryOperator<Any>()
        ctOperator.setLeftHandOperand<CtBinaryOperator<Any>>(expressionOrWrappedInStatementExpression(lhs))
        ctOperator.setRightHandOperand<CtBinaryOperator<Any>>(expressionOrWrappedInStatementExpression(rhs))
        ctOperator.setType<CtBinaryOperator<Any>>(referenceBuilder.getNewTypeReference(rhsIf.type))
        ctOperator.putKtMetadata(KtMetadataKeys.KT_BINARY_OPERATOR_KIND, KtMetadata.binOpKind(KtBinaryOperatorKind.ELVIS))
        return ctOperator.definite()
    }

    private fun createSafeCall(block: IrBlock, data: ContextData): TransformResult<CtElement> {
        val safeReceiver = block.statements.firstIsInstance<IrVariable>().initializer!!.accept(this, data).resultUnsafe as CtExpression<Any>
        val rhs = block.statements.firstIsInstance<IrIfThenElseImpl>().branches
            .firstIsInstance<IrElseBranch>().result.accept(this, data).resultUnsafe
        when (rhs) {
            is CtAssignment<*,*> -> {
                (rhs.assigned as CtTargetedExpression<Any,CtExpression<Any>>).setTarget<CtTargetedExpression<Any,CtExpression<Any>>>(safeReceiver)
                rhs.assigned.putKtMetadata(KtMetadataKeys.ACCESS_IS_SAFE, KtMetadata.bool(true))
            }
            is CtTargetedExpression<*,*> -> {
                (rhs as CtTargetedExpression<Any,CtExpression<Any>>).setTarget<CtTargetedExpression<Any,CtExpression<Any>>>(safeReceiver)
                rhs.putKtMetadata(KtMetadataKeys.ACCESS_IS_SAFE, KtMetadata.bool(true))
            }
            else -> {
                throw SpoonIrBuildException("Unexpected target of safe call: ${rhs::class.simpleName}")
            }
        }
        return rhs.definite()
    }

    private fun checkForCompositeElement(block: IrBlock, data: ContextData): TransformResult<CtElement> {
        if(block.statements.size == 1 && block.statements[0] is IrDoWhileLoop) {
            return visitDoWhileLoop(block.statements[0] as IrDoWhileLoop, data)
        }
        when(block.origin) {
            null -> return TransformResult.nothing()
            IrStatementOrigin.FOR_LOOP -> {
                return visitForLoop(block, data)
            }
            IrStatementOrigin.ARGUMENTS_REORDERING_FOR_CALL -> {
                val map = helper.getNamedArgumentsMap(block, data)
                val call = block.statements.first { it is IrCall || it is IrConstructorCall } as IrFunctionAccessExpression
                return createInvocation(call, data, map)
            }
            IrStatementOrigin.ELVIS -> {
                return visitElvisOperator(block, data)
            }
            IrStatementOrigin.SAFE_CALL -> {
                return createSafeCall(block, data)
            }
            IrStatementOrigin.WHEN -> {
                val subjectExpr = block.statements.firstIsInstanceOrNull<IrVariable>()
                val whenExpr = block.statements.firstIsInstanceOrNull<IrWhen>() ?:
                        IrWhenImpl(block.startOffset, block.endOffset, context.irBuiltIns.unitType)
                val context = When(data, subjectExpr)
                return visitWhen(whenExpr, context)
            }
            IrStatementOrigin.OBJECT_LITERAL -> {
                val ctAnonClass = core.createNewClass<Any>()
                val theAnonClass = visitClass(block.statements[0] as IrClass, data).resultSafe
                ctAnonClass.setAnonymousClass<CtNewClass<Any>>(theAnonClass as CtClass<*>)
                ctAnonClass.setExecutable<CtNewClass<Any>>(referenceBuilder.getNewConstructorExecutableReference(
                    block.statements[1] as IrConstructorCall)
                )
                ctAnonClass.putKtMetadata(KtMetadataKeys.CLASS_IS_OBJECT, KtMetadata.bool(true))
                theAnonClass.putKtMetadata(KtMetadataKeys.CLASS_IS_OBJECT, KtMetadata.bool(true))
                return ctAnonClass.definite()
            }
            in INCREMENT_DECREMENT_OPERATORS -> {
                val setVar = block.statements.firstIsInstanceOrNull<IrSetVariable>()?.symbol
                val operand: CtExpression<Any> = if(setVar == null) {
                    val call = block.statements.firstIsInstance<IrBlock>().
                    statements.firstIsInstance<IrCall>()
                    specialInvocation(call, data).resultUnsafe as CtExpression<Any>
                } else {
                    createVariableWrite(null,
                        referenceBuilder.getNewVariableReference<Any>(setVar.descriptor)!!)
                }
                val ctUnaryOp = core.createUnaryOperator<Any>()
                ctUnaryOp.setOperand<CtUnaryOperator<*>>(operand)
                ctUnaryOp.setKind<CtUnaryOperator<*>>(OperatorHelper.originToUnaryOperatorKind(block.origin!!))
                ctUnaryOp.setType<CtUnaryOperator<*>>(referenceBuilder.getNewTypeReference(block.type))
                return ctUnaryOp.definite()
            }
            in AUGMENTED_ASSIGNMENTS -> {
                val call = block.statements.firstIsInstanceOrNull<IrCall>()
                if(call != null) {
                    val assignmentFromCall = specialInvocation(call, data).resultOrNull
                    if(assignmentFromCall != null && assignmentFromCall is CtAssignment<*,*>) {
                        return assignmentFromCall.definite()
                    }
                }

                return createAugmentedAssignmentOperator(block, block.origin!!, data).definite()
            }
        }
        return TransformResult.nothing()
    }

    override fun visitWhileLoop(loop: IrWhileLoop, data: ContextData): DefiniteTransformResult<CtWhile> {
        val ctWhile = core.createWhile()
        val condition = loop.condition.accept(this, data).resultUnsafe as CtExpression<Boolean>
        val body = loop.body!!.accept(this,data).resultUnsafe.blockOrSingleStatementBlock()
        ctWhile.setLoopingExpression<CtWhile>(condition)
        ctWhile.setBody<CtWhile>(body)
        ctWhile.setLabel<CtWhile>(loop.label)
        return ctWhile.definite()
    }

    override fun visitDoWhileLoop(loop: IrDoWhileLoop, data: ContextData): DefiniteTransformResult<CtElement> {
        val ctDo = factory.Core().createDo()
        val condition = loop.condition.accept(this, data).resultUnsafe as CtExpression<Boolean>

        val body = if(loop.body is IrComposite) { // Body of do-while is sometimes composite
            visitBlockInternal(loop.body as IrComposite, data).resultSafe
        } else {
            loop.body!!.accept(this, data).resultUnsafe.blockOrSingleStatementBlock()
        }
        ctDo.setLoopingExpression<CtDo>(condition)
        ctDo.setBody<CtDo>(body)
        ctDo.setLabel<CtDo>(loop.label)
        return ctDo.definite()
    }


    private fun visitBlockInternal(expression: IrContainerExpression, data: ContextData): DefiniteTransformResult<CtBlock<*>> {
        val statements = ArrayList<CtStatement>()
        for((i, statement) in expression.statements.withIndex()) {
            val ctElement = statement.accept(this, data).resultOrNull ?: continue
            val ctStmt: CtStatement = when(ctElement) {
                is CtMethod<*> -> {
                    ctElement.wrapLocalMethod()
                }
                is CtStatement -> ctElement
                else -> statementOrWrappedInImplicitReturn(ctElement)
            }
            statements.add(ctStmt)
        }

        val ctBlock = core.createBlock<Any>()
        ctBlock.setStatements<CtBlock<*>>(statements)
        ctBlock.putKtMetadata(KtMetadataKeys.KT_STATEMENT_TYPE,
            KtMetadata.element(referenceBuilder.getNewTypeReference<CtBlock<*>>(expression.type)))
        return ctBlock.definite()

    }

    override fun visitBlock(expression: IrBlock, data: ContextData): DefiniteTransformResult<CtElement> {
        val composite = checkForCompositeElement(expression, data).resultOrNull
        if(composite != null) return composite.definite()
        return visitBlockInternal(expression, data)
    }

    private fun createAugmentedAssignmentOperator(
            expression: IrExpression,
            origin: IrStatementOrigin,
            data: ContextData
    ): CtOperatorAssignment<*,*> {
        val (irLhs, irRhs) = OperatorHelper.getAugmentedAssignmentOperands(expression)
        val ctAssignmentOp = core.createOperatorAssignment<Any,Any>()
        val lhs = expressionOrWrappedInStatementExpression(irLhs.accept(this, data).resultUnsafe)
        val rhs = expressionOrWrappedInStatementExpression(irRhs.accept(this, data).resultUnsafe)
        ctAssignmentOp.setKind<CtOperatorAssignment<Any,Any>>(
            OperatorHelper.originToBinaryOperatorKind(origin).toJavaAssignmentOperatorKind()
        )
        ctAssignmentOp.setAssigned<CtOperatorAssignment<Any,Any>>(lhs)
        ctAssignmentOp.setAssignment<CtOperatorAssignment<Any,Any>>(rhs)
        ctAssignmentOp.setType<CtOperatorAssignment<Any,Any>>(referenceBuilder.getNewTypeReference(expression.type))
        return ctAssignmentOp
    }

    override fun visitSetVariable(expression: IrSetVariable, data: ContextData): TransformResult<CtElement> {
        if(expression.origin in AUGMENTED_ASSIGNMENTS) {
            return createAugmentedAssignmentOperator(expression, expression.origin!!, data).definite()
        }

        if(expression.origin != IrStatementOrigin.EQ) TODO()
        val lhs = createVariableWrite(null, referenceBuilder.getNewVariableReference<Any>(expression.symbol.descriptor)!!)
        val rhs = expression.value.accept(this, data).resultUnsafe as CtExpression<Any>
        return createAssignment(lhs, rhs).also {
            it.setType<CtExpression<*>>(referenceBuilder.getNewTypeReference(expression.type))
        }.definite()
    }

    @Suppress("UNCHECKED_CAST")
    private fun createVariableWrite(receiver: CtExpression<*>?, variableRef: CtReference) = when (variableRef) {
        is CtLocalVariableReference<*> ->
            factory.Core().createVariableWrite<Any>().also {
                it.setVariable<CtVariableAccess<Any>>(variableRef as CtLocalVariableReference<Any>)
            }
        is CtFieldReference<*> -> {
            factory.Core().createFieldWrite<Any>().also {
                it.setVariable<CtVariableAccess<Any>>(variableRef as CtFieldReference<Any>)
                it.setTarget<CtTargetedExpression<Any, CtExpression<*>>>(receiver)
            }
        }
        is CtParameterReference<*> -> {
            factory.Core().createVariableWrite<Any>().also {
                it.setVariable<CtVariableAccess<Any>>(variableRef as CtParameterReference<Any>)
            }
        }
        else -> throw SpoonException("Unexpected expression ${variableRef::class.simpleName}")
    }

    @Suppress("UNCHECKED_CAST")
    private fun createVariableRead(variableRef: CtReference) = when(variableRef) {
        is CtFieldReference<*> -> {
            factory.Core().createFieldRead<Any>().also {
                it.setVariable<CtVariableRead<Any>>(variableRef as CtVariableReference<Any>)
            }
        }
        is CtParameterReference<*> -> {
            factory.Core().createVariableRead<Any>().also {
                it.setVariable<CtVariableRead<Any>>(variableRef as CtVariableReference<Any>)
            }
        }
        is CtLocalVariableReference<*>, is CtCatchVariableReference<*> -> {
            factory.Core().createVariableRead<Any>().also {
                it.setVariable<CtVariableRead<Any>>(variableRef as CtVariableReference<Any>)
            }
        }
        is CtSuperAccess<*> -> {
            variableRef
        }
        else -> throw SpoonIrBuildException("Unexpected reference for variable read ${variableRef::class.simpleName}")
    }


    private fun visitPropertyAccess(irCall: IrCall, data: ContextData): DefiniteTransformResult<CtElement> {
        if(irCall.symbol.descriptor is JavaMethodDescriptor)
            return createInvocation(irCall, data)
        val descriptor = irCall.symbol.descriptor as PropertyGetterDescriptor

        val variable = referenceBuilder.getNewVariableReference<Any>(descriptor.correspondingProperty)
        val target = getReceiver(irCall, data)
        val fieldRead = core.createFieldRead<Any>()
        fieldRead.setVariable<CtFieldRead<Any>>(variable)
        if(target != null) fieldRead.setTarget<CtFieldRead<Any>>(target as CtExpression<*>)
        return fieldRead.definite()
    }

    override fun visitExpressionBody(body: IrExpressionBody, data: ContextData):
            TransformResult<CtElement> {
        return body.expression.accept(this, data)
    }

    private fun createIsTypeOperation(call: IrTypeOperatorCall, data: ContextData): DefiniteTransformResult<CtBinaryOperator<*>> {
        val ctBinaryOperator = core.createBinaryOperator<Boolean>()
        val operatorKind = if(call.operator == IrTypeOperator.INSTANCEOF) KtBinaryOperatorKind.IS
            else KtBinaryOperatorKind.IS_NOT
        ctBinaryOperator.putKtMetadata(KtMetadataKeys.KT_BINARY_OPERATOR_KIND, KtMetadata.binOpKind(operatorKind))
        ctBinaryOperator.setType<CtExpression<*>>(referenceBuilder.getNewTypeReference(call.type))
        val lhs = call.argument.accept(this, data).resultOrNull
        val rhs = createTypeAccess(call.typeOperand)
        if(lhs != null) {
            ctBinaryOperator.setLeftHandOperand<CtBinaryOperator<Boolean>>(expressionOrWrappedInStatementExpression(lhs))
        }
        ctBinaryOperator.setRightHandOperand<CtBinaryOperator<Boolean>>(rhs)
        return ctBinaryOperator.definite()
    }

    private fun createTypeCast(call: IrTypeOperatorCall, data: ContextData): DefiniteTransformResult<CtExpression<*>> {
        val castedExpr = call.argument.accept(this, data).resultUnsafe as CtExpression<Any>
        val conversionTypeRef = referenceBuilder.getNewTypeReference<Any>(call.typeOperand)
        castedExpr.addTypeCast<CtExpression<Any>>(conversionTypeRef)

        val safe = call.operator == IrTypeOperator.SAFE_CAST
        conversionTypeRef.putKtMetadata(KtMetadataKeys.TYPE_CAST_AS_SAFE, KtMetadata.bool(safe))
        return castedExpr.definite()
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: ContextData): TransformResult<CtElement> {
        return when(expression.operator) {
            IrTypeOperator.CAST, IrTypeOperator.SAFE_CAST -> createTypeCast(expression, data)
            IrTypeOperator.INSTANCEOF, IrTypeOperator.NOT_INSTANCEOF -> createIsTypeOperation(expression, data)
            IrTypeOperator.IMPLICIT_COERCION_TO_UNIT,
            IrTypeOperator.IMPLICIT_NOTNULL,
            IrTypeOperator.IMPLICIT_CAST -> expression.argument.accept(this, data)
            else -> throw SpoonIrBuildException("Unimplemented type operator: ${expression.operator}")
        }
    }

    private fun createWildcardVariable(): CtLocalVariable<Any> {
        return core.createLocalVariable<Any>().also {
            it.setType<CtVariable<Any>>(referenceBuilder.getNewTypeReference(context.irBuiltIns.nothing))
            it.setSimpleName<CtVariable<*>>("_")
            it.setInferred<CtLocalVariable<Any>>(true)
        }
    }

    override fun visitComposite(expression: IrComposite, data: ContextData): TransformResult<CtElement> {
        val context = Destruct(data)
        val irComponents = expression.statements.drop(1).map { visitVariable(it as IrVariable, context).resultSafe }
        val allComponents = getSourceHelper(data).destructuredNames(expression)
        var ir = 0
        var allI = 0
        val components = ArrayList<CtLocalVariable<*>>(allComponents.size)
        while(allI < allComponents.size) {
            if(allComponents[allI++] == "_") {
                components.add(createWildcardVariable())
            } else {
                components.add(irComponents[ir++])
            }
        }
        val placeHolder = components.toDestructuredVariable()
        placeHolder.setDefaultExpression<CtLocalVariable<Any>>(
            (expression.statements[0] as IrVariable).initializer!!.accept(this, data).resultUnsafe as CtExpression<Any>
        )
        placeHolder.addModifiersAsMetadata(IrToModifierKind.fromVariable(expression.statements[1] as IrVariable))
        return placeHolder.definite()
    }

    private fun getThisAccessOrVariableRef(getValue: IrGetValue, data: ContextData): CtElement? {
        val owner = getValue.symbol.owner
        if(owner.origin == IrDeclarationOrigin.IR_TEMPORARY_VARIABLE && owner is IrVariable) {
            val initializer = owner.initializer
            return if(initializer is IrGetValue) {
                 getThisAccessOrVariableRef(initializer, data)
            }
            else initializer?.accept(this, data)?.resultOrNull
        }
        val descriptor = owner.descriptor
        if(descriptor is ReceiverParameterDescriptor) {
            when(descriptor.value) {
                is ExtensionReceiver, is ImplicitClassReceiver -> {
                    return visitThisReceiver(getValue, data)
                }
            }
        }
        val ref = referenceBuilder.getNewVariableReference<Any>(getValue) ?: return null
        return createVariableRead(ref)
    }

    override fun visitGetValue(expression: IrGetValue, data: ContextData): TransformResult<CtElement> {
        val symbol = expression.symbol
        val descriptor = symbol.descriptor
        if(symbol is IrValueParameterSymbol && descriptor is ReceiverParameterDescriptor) {
            return visitThisReceiver(expression, data).definite()
        }
        if(symbol is IrVariableSymbol && descriptor is IrTemporaryVariableDescriptor) {
            if(descriptor.name.asString().matches("tmp\\d+_this".toRegex())) {
                return symbol.owner.initializer!!.accept(this,data) 
            }

        }

        val access = getThisAccessOrVariableRef(expression, data)
        return access?.definite() ?: TransformResult.nothing()
    }

    private fun createSetOperator(irCall: IrCall, data: ContextData, operator: KtBinaryOperatorKind?): DefiniteTransformResult<CtAssignment<Any,Any>> {
        val receiver = helper.getReceiver(irCall)!!.accept(this, data).resultUnsafe
        val ctArrayWrite = factory.Core().createArrayWrite<Any>()
        val ctAssignment = if(operator == null) core.createAssignment<Any, Any>()
            else core.createOperatorAssignment<Any,Any>()
        val args = ArrayList<CtElement>()
        val argCount = irCall.valueArgumentsCount
        for(i in 0 until argCount) {
            val irArg = irCall.getValueArgument(i)!!
            val ctArg = if(i == argCount - 1
                && irArg is IrCall
                && irArg.origin in AUGMENTED_ASSIGNMENTS
            ) {
                // If LHS is a set-operator of opassign, RHS is also marked with opassign origin.
                // Disregard RHS receiver, which is a get-operator of LSH
                // a[x,y] += b
                // > translates to >
                // a[x,y] = a[x,y] + b
                irArg.getValueArgument(0)!!.accept(this, data).resultUnsafe
            } else {
                irArg.accept(this, data).resultUnsafe
            }

            if(i == irCall.valueArgumentsCount - 1) {
                ctAssignment.setAssignment<CtAssignment<Any,Any>>(expressionOrWrappedInStatementExpression(ctArg))
            }
            else {
                args.add(ctArg)
                ctArg.setParent(ctArrayWrite)
            }
        }

        if(operator != null) {
            (ctAssignment as CtOperatorAssignment<Any,Any>)
                .setKind<CtOperatorAssignment<Any,Any>>(operator.toJavaAssignmentOperatorKind())
        }
        ctArrayWrite.setTarget<CtArrayWrite<Any>>(expressionOrWrappedInStatementExpression(receiver))
        ctArrayWrite.setType<CtArrayWrite<*>>(referenceBuilder.getNewTypeReference(irCall.type))
        ctArrayWrite.putMetadata<CtArrayWrite<*>>(KtMetadataKeys.ARRAY_ACCESS_INDEX_ARGS, args)

        ctAssignment.setAssigned<CtAssignment<Any,Any>>(ctArrayWrite)

        // Type args ignored, they are implicit
        return ctAssignment.definite()
    }

    private fun createGetOperator(irCall: IrCall, data: ContextData): DefiniteTransformResult<CtArrayRead<Any>> {
        val receiver = irCall.dispatchReceiver!!.accept(this, data).resultUnsafe
        val ctArrAccess = factory.Core().createArrayRead<Any>()
        ctArrAccess.setTarget<CtArrayRead<Any>>(expressionOrWrappedInStatementExpression(receiver))
        ctArrAccess.setType<CtArrayRead<Any>>(referenceBuilder.getNewTypeReference(irCall.type))
        val args = ArrayList<CtElement>()
        for(i in 0 until irCall.valueArgumentsCount) {
            val ctArg = irCall.getValueArgument(i)!!.accept(this, data).resultUnsafe
            ctArg.setParent(ctArrAccess)
            args.add(ctArg)
        }
        ctArrAccess.putMetadata<CtElement>(KtMetadataKeys.ARRAY_ACCESS_INDEX_ARGS, args)
        // Type args ignored, they are implicit
        return ctArrAccess.definite()
    }

    private fun getReceiver(irCall: IrFunctionAccessExpression, data: ContextData): CtElement? {
        if(irCall is IrCall && irCall.superQualifierSymbol != null) return visitSuperTarget(irCall.superQualifierSymbol!!)
        return helper.getReceiver(irCall)?.accept(this, data)?.resultOrNull
    }

    private fun visitThisReceiver(irGetValue: IrGetValue, data: ContextData): CtThisAccess<*> {
        val (explicitThis, targetLabel) = helper.getThisExtensionTarget(irGetValue, data.file)
        return factory.Code().createThisAccess<Any>(
            referenceBuilder.getNewTypeReference(irGetValue.type),
            explicitThis == null
        ). also {
            if(targetLabel != null) {
                it.putKtMetadata(KtMetadataKeys.EXTENSION_THIS_TARGET, KtMetadata.string(targetLabel))
            }
        }
    }

    private fun visitSuperTarget(symbol: IrClassSymbol): CtSuperAccess<*> {
        val superAccess = core.createSuperAccess<Any>()
        superAccess.setType<CtSuperAccess<*>>(referenceBuilder.getNewTypeReference(
            symbol.descriptor
        ))
        superAccess.setImplicit<CtSuperAccess<*>>(false)
        return superAccess
    }

    private fun createTypeAccess(ctType: CtTypeReference<Any>): CtTypeAccess<Any> {
        val typeAccess = core.createTypeAccess<Any>()
        typeAccess.setAccessedType<CtTypeAccess<Any>>(ctType)
        return typeAccess
    }

    private fun createTypeAccess(irType: IrType): CtTypeAccess<Any> {
        return createTypeAccess(referenceBuilder.getNewTypeReference<Any>(irType))
    }

    override fun visitVararg(expression: IrVararg, data: ContextData): CompositeTransformResult<CtExpression<*>> {
        val result = ArrayList<CtExpression<*>>()
        for(arg in expression.elements) {
            if(arg is IrSpreadElement) {
                val spreadElement = arg.expression.accept(this, data).resultUnsafe
                result.add(expressionOrWrappedInStatementExpression(spreadElement))
                spreadElement.putKtMetadata(KtMetadataKeys.SPREAD, KtMetadata.bool(true))
            } else {
                result.add(expressionOrWrappedInStatementExpression(arg.accept(this, data).resultUnsafe))
            }
        }
        return CompositeTransformResult(result)
    }

    override fun visitGetObjectValue(expression: IrGetObjectValue, data: ContextData): MaybeTransformResult<CtElement> {
        if(getSourceHelper(data).sourceTextIs(expression) { text -> text == "return" }) {
            return TransformResult.nothing()
        }
        val typeAccess = createTypeAccess(expression.type)
        return maybe(typeAccess)
    }

    override fun visitReturn(expression: IrReturn, data: ContextData): DefiniteTransformResult<CtElement> {
        val ctReturn = core.createReturn<Any>()
        checkLabelOfStatement(expression, ctReturn, data)
        val targetLabel = getSourceHelper(data).returnTargetLabelOrNull(expression)
        if(targetLabel != null) {
            /*
            'label1@ return@label2' is valid in Kotlin, but CtReturn only has one label inherited from CtStatement.
             We use metadata label for the target label, and the CtStatement label is reserved for the prefix label
             */
            ctReturn.putKtMetadata(KtMetadataKeys.LABEL, KtMetadata.string(targetLabel))
        }
        val transformResult = expression.value.accept(this, data).resultOrNull
        if(transformResult != null) {
            ctReturn.setReturnedExpression<CtReturn<Any>>(
                expressionOrWrappedInStatementExpression(transformResult)
            )
        }
        if(expression.endOffset == expression.startOffset ||
                getSourceHelper(data).sourceTextIs(expression) { !it.startsWith("return")})
            ctReturn.setImplicit<CtReturn<*>>(true)
        return ctReturn.definite()
    }

    override fun visitThrow(expression: IrThrow, data: ContextData): DefiniteTransformResult<CtThrow> {
        val ctThrow = core.createThrow()
        val throwExpr = expression.value.accept(this, data).resultUnsafe
        ctThrow.setThrownExpression<CtThrow>(expressionOrWrappedInStatementExpression(throwExpr)  as CtExpression<Throwable>)
        ctThrow.putKtMetadata(KtMetadataKeys.KT_STATEMENT_TYPE,
            KtMetadata.element(referenceBuilder.getNewTypeReference<Any>(expression.type)))
        return ctThrow.definite()
    }

    override fun visitTry(aTry: IrTry, data: ContextData): DefiniteTransformResult<CtTry> {
        val ctTry = core.createTry()
        ctTry.setBody<CtTry>(aTry.tryResult.accept(this, data).resultUnsafe as CtStatement)
        ctTry.setCatchers<CtTry>(aTry.catches.map { visitCatch(it, data).resultSafe })
        val finalizer = aTry.finallyExpression?.accept(this, data)?.resultUnsafe as CtBlock<*>?
        if(finalizer != null) {
            ctTry.setFinalizer<CtTry>(finalizer)
        }
        ctTry.putKtMetadata(KtMetadataKeys.KT_STATEMENT_TYPE, KtMetadata.element(
            referenceBuilder.getNewTypeReference<Any>(aTry.type)
        ))
        return ctTry.definite()
    }

    private fun createCatchVariable(variable: IrVariable): CtCatchVariable<Throwable> {
        val catchVar = core.createCatchVariable<Throwable>()
        catchVar.setSimpleName<CtVariable<*>>(variable.name.escaped())
        catchVar.setType<CtVariable<*>>(referenceBuilder.getNewTypeReference(variable.type))
        return catchVar
    }

    override fun visitCatch(aCatch: IrCatch, data: ContextData): DefiniteTransformResult<CtCatch> {
        val block = aCatch.result.accept(this, data).resultUnsafe as CtBlock<*>
        val ctCatch = core.createCatch()
        ctCatch.setParameter<CtCatch>(createCatchVariable(aCatch.catchParameter))
        ctCatch.setBody<CtCatch>(block)
        return ctCatch.definite()
    }

    override fun visitContinue(jump: IrContinue, data: ContextData): DefiniteTransformResult<CtContinue> {
        val ctContinue = core.createContinue()
        val label = jump.label
        if(label != null) {
            ctContinue.setTargetLabel<CtContinue>(label)
        }
        return ctContinue.definite()
    }

    override fun visitBreak(jump: IrBreak, data: ContextData): TransformResult<CtElement> {
        val ctBreak = core.createBreak()
        val label = jump.label
        if(label != null) {
            ctBreak.setTargetLabel<CtBreak>(jump.label)
        }
        return ctBreak.definite()
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
        is CtSwitchExpression<*,*> -> {
            val switchStmt = core.createSwitch<Any>()
            if(e.selector != null) {
                switchStmt.setSelector<CtSwitch<Any>>(e.selector as CtExpression<Any>)
            }
            switchStmt.setCases<CtSwitch<Any>>(e.cases as List<CtCase<Any>>)
            switchStmt.putKtMetadata(KtMetadataKeys.KT_STATEMENT_TYPE, KtMetadata.element(e.type))
            switchStmt
        }
        is CtExpression<*> -> e.wrapInImplicitReturn()
        else -> throw RuntimeException("Can't wrap ${e::class} in StatementExpression")
    }

    private fun List<CtLocalVariable<*>>.toDestructuredVariable(): CtLocalVariable<Any> {
        val placeHolder = core.createLocalVariable<Any>()
        placeHolder.setImplicit<CtElement>(true)
        placeHolder.putKtMetadata(KtMetadataKeys.IS_DESTRUCTURED, KtMetadata.bool(true))
        placeHolder.putMetadata<CtElement>(KtMetadataKeys.COMPONENTS, this)
        return placeHolder
    }

    private fun CtElement.blockOrSingleStatementBlock(): CtBlock<*> = when(this) {
        is CtBlock<*> -> this
        else -> {
            val block = core.createBlock<Any>()
            if(this is CtExpression<*> && this !is CtAssignment<*,*>) {
                block.putKtMetadata(KtMetadataKeys.KT_STATEMENT_TYPE,
                    KtMetadata.element(this.type)
                    )
            }
            block.addStatement<CtBlock<*>>(statementOrWrappedInImplicitReturn(this))
            block.setImplicit<CtBlock<*>>(true)
        }
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
            is CtLocalVariable<*> -> {
                statementExpression = e.wrapInStatementExpression(e.type as CtTypeReference<Any>)
                statementExpression.setImplicit(true)
            }
            is CtStatement -> {
                val typeRef = e.getMetadata(KtMetadataKeys.KT_STATEMENT_TYPE) as CtTypeReference<Any>
                statementExpression = e.wrapInStatementExpression(typeRef)
                statementExpression.setImplicit(true)
            }
            else -> throw RuntimeException("Can't wrap ${e::class.simpleName} in StatementExpression")
        }
        return statementExpression
    }

    private fun CtMethod<*>.wrapLocalMethod(): CtClass<Any> {
        val wrapperClass = factory.Core().createClass<Any>()
        wrapperClass.setImplicit<CtClass<Any>>(true)
        wrapperClass.setSimpleName<CtClass<*>>("<local>")
        wrapperClass.addMethod<Any, CtClass<Any>>(this as CtMethod<Any>)
        return wrapperClass
    }
}