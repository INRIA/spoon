package spoon.kotlin.compiler

import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.kotlin.reflect.KtModifierKind
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.psi
import org.jetbrains.kotlin.fir.references.impl.FirPropertyFromParameterResolvedNamedReference
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.visitors.CompositeTransformResult
import org.jetbrains.kotlin.fir.visitors.FirVisitor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import spoon.kotlin.reflect.KtStatementExpression
import spoon.kotlin.reflect.KtStatementExpressionImpl
import spoon.reflect.code.*
import spoon.reflect.declaration.*
import spoon.reflect.factory.Factory
import spoon.reflect.reference.CtTypeReference
import spoon.support.reflect.code.CtLiteralImpl

class FirTreeBuilder(val factory : Factory, val file : FirFile) : FirVisitor<CompositeTransformResult<CtElement>, Nothing?>() {
    internal val referenceBuilder = ReferenceBuilder(this)
    internal val helper = FirTreeBuilderHelper(this)

    // Temporary printing, remove later
    private val msgCollector = PrintingMsgCollector()
    fun report(m : Message) = msgCollector.report(m)
    fun report(s : String) = report(Message(s, MessageType.COMMON))
    fun warn(s : String) = report(Message(s, MessageType.WARN))

    override fun visitElement(element: FirElement, data: Nothing?): CompositeTransformResult<CtElement> {
        element.acceptChildren(this,null)
        //throw SpoonException("Element type not implemented $element")
        return CtLiteralImpl<String>().setValue<CtLiteral<String>>("Unimplemented element $element").compose()
    }

    fun addModifiersAsMetadata(element: CtElement, modifierList: List<KtModifierKind>) {
        element.putMetadata<CtElement>(KtMetadataKeys.KT_MODIFIERS, modifierList.toMutableSet())
    }

    override fun visitFile(file: FirFile, data: Nothing?): CompositeTransformResult<CtElement> {
        val module = helper.getOrCreateModule(file, factory)
        val compilationUnit = factory.CompilationUnit().getOrCreate(file.name)

        val pkg = if(file.packageFqName.isRoot) module.rootPackage else
            factory.Package().getOrCreate(file.packageFqName.shortName().identifier, module)
        compilationUnit.declaredPackage = pkg

        val ktFile = file.psi
        if(ktFile is KtFile) {
            compilationUnit.lineSeparatorPositions = with(ktFile) {
                if (hasNormalizedEOL()) lineSeparatorPositions() else lineSeparatorPositionsRaw()
            }
        }

        val transformedTopLvlDecl = file.declarations.map {
            it.accept(this, null)
        }

        return transformedTopLvlDecl.composeManySingles()
    }

    override fun visitRegularClass(regularClass: FirRegularClass, data: Nothing?): CompositeTransformResult<CtElement> {
        val module = helper.getOrCreateModule(file, factory)
        val pkg = if (file.packageFqName.isRoot) module.rootPackage else
            factory.Package().getOrCreate(file.packageFqName.shortName().identifier, module)
        val type = helper.createType(regularClass)
        pkg.addType<CtPackage>(type)

        // Modifiers
        val modifierList = KtModifierKind.fromClass(regularClass)
        addModifiersAsMetadata(type, modifierList)

        val decls = regularClass.declarations.map {
            it.accept(this, null).single.also { decl ->
                decl.setParent(type)
                when (decl) {
                    is CtField<*> -> (type as CtClass<*>).addField(decl)
                    is CtMethod<*> -> {
                        if (regularClass.isInterface() && decl.body != null) {
                            decl.setDefaultMethod<Nothing>(true)
                        }
                        type.addMethod(decl)
                    }
                    is CtConstructor<*> -> {
                        if (type is CtClass<*>) {
                            (type as CtClass<Any>).addConstructor<CtClass<Any>>(decl as CtConstructor<Any>)
                        } else warn("Constructor without accompanying CtClass")
                    }
                }
            }
        }

        return type.compose()
    }



    override fun visitConstructor(constructor: FirConstructor, data: Nothing?): CompositeTransformResult.Single<CtConstructor<*>> {
        val ctConstructor = factory.Core().createConstructor<Any>()

        val modifierList = listOfNotNull(KtModifierKind.convertVisibility(constructor.visibility))

        ctConstructor.setImplicit<CtConstructor<Any>>(
            constructor.isPrimary &&
            constructor.valueParameters.isEmpty() &&
            constructor.body == null &&
            modifierList.isEmpty()
        )

        addModifiersAsMetadata(ctConstructor, modifierList)

        // Add body
        val body = constructor.body?.accept(this, null)?.single as? CtStatement?
        if(body != null) {
            ctConstructor.addChildWith(body, ctConstructor::setBody)
        }

        // Add params
        constructor.valueParameters.forEach {
            val p = it.accept(this,null).single as CtParameter<*>
            /*
            * Primary constructor property declaration creates implicit properties in the class. An implicit property is the
            * holder of the val/var modifier, not the parameter:
            * ClassName(var x = 2) <translates to> ClassName(x = 2) { var x = x }
            * To facilitate printing, we look in the PSI if the parameter has a val/var keyword and add it as a modifier.
            *
            * TODO: Perhaps add metadata mapping property <-> param?
            *  */
            if(constructor.isPrimary) {
                val psiTokens = it.source.psi?.getChildrenOfType<LeafPsiElement>()
                val pModifiers = p.getMetadata(KtMetadataKeys.KT_MODIFIERS) as MutableSet<KtModifierKind>?
                if(psiTokens?.any { t -> t.elementType == KtTokens.VAL_KEYWORD } == true) {
                    pModifiers?.add(KtModifierKind.VAL)
                } else if(psiTokens?.any { t -> t.elementType == KtTokens.VAR_KEYWORD } == true) {
                    pModifiers?.add(KtModifierKind.VAR)
                }
            }
            ctConstructor.addChildWith(p, ctConstructor::addParameter)
        }

        ctConstructor.putMetadata<CtConstructor<*>>(KtMetadataKeys.CONSTRUCTOR_IS_PRIMARY, constructor.isPrimary)

        return ctConstructor.compose()
    }

    private inline fun <ChildT : CtElement> CtElement.addChildWith(child: ChildT, action: (ChildT) -> CtElement) {
        action(child)
        child.setParent(this)
    }

    override fun visitWhenExpression(whenExpression: FirWhenExpression, data: Nothing?): CompositeTransformResult<CtElement> {
        if(whenExpression.isIf()) return visitIfExpression(whenExpression)



        return super.visitWhenExpression(whenExpression, data)
    }

    fun visitIfExpression(ifExpression : FirWhenExpression) : CompositeTransformResult.Single<CtIf> {
        // Sanity check
        if(ifExpression.branches.size > 2) {
            warn("WhenExpression misplaced as if")
        }

        val ctIf = factory.Core().createIf()
        val condition = ifExpression.branches[0].condition.accept(this,null).single
        ctIf.setCondition<CtIf>(condition as CtExpression<Boolean>)
        val thenBranch = ifExpression.branches[0].result.accept(this,null).single

        fun branchToStatement(e : CtElement) : CtStatement = when(e) {
            is CtStatement -> e
            is CtExpression<*> -> e.wrapInImplicitReturn()
            else -> throw RuntimeException("Branch is not statement nor expression")
        }

        ctIf.setThenStatement<CtIf>(branchToStatement(thenBranch))

        ifExpression.branches.getOrNull(1)?.let {
            ctIf.setElseStatement<CtIf>(branchToStatement(it.result.accept(this,null).single))
        }

        // Add type
        val type = referenceBuilder.getNewTypeReference<CtType<Any>>(ifExpression.typeRef)
        ctIf.putMetadata<CtIf>(KtMetadataKeys.KT_IF_TYPE, type)

        return ctIf.compose()
    }

    override fun visitBlock(block: FirBlock, data: Nothing?): CompositeTransformResult.Single<CtBlock<*>> {
        val ktBlock = factory.Core().createBlock<Any>()
        val statements = block.statements.map { it.accept(this, null).single.let { s ->
            s.setParent(ktBlock)
            if(s is CtExpression<*> && s !is CtStatement) {
                s.wrapInImplicitReturn()
            } else {
                s
            }
            }
        } as List<CtStatement>
        ktBlock.setStatements<CtBlock<*>>(statements)
        return ktBlock.compose()
    }

    override fun visitSimpleFunction(simpleFunction: FirSimpleFunction, data: Nothing?): CompositeTransformResult.Single<CtMethod<*>> {
        val ctMethod = factory.Core().createMethod<Any>()
        ctMethod.setSimpleName<CtMethod<Any>>(simpleFunction.name.identifier)

        // Add modifiers
        val modifiers = KtModifierKind.fromFunctionDeclaration(simpleFunction)
        addModifiersAsMetadata(ctMethod, modifiers)

        // Add params
        simpleFunction.valueParameters.forEach {
            val p = it.accept(this,null).single
            if(p !is CtParameter<*>) {
                warn("Transformed parameter is not CtParameter")
            }
            else ctMethod.addParameter<CtMethod<Any>>(p)
        }

        // Add body
        val body = simpleFunction.body
        if(body != null) {
            val ctBody = body.accept(this, null).single
            ctMethod.setBody<CtMethod<Any>>(ctBody as CtStatement)
        }

        // Set (return) type
        ctMethod.setType<CtMethod<*>>(referenceBuilder.getNewTypeReference<Any>(simpleFunction.returnTypeRef))

        return ctMethod.compose()
    }

    override fun visitValueParameter(
        valueParameter: FirValueParameter,
        data: Nothing?
    ): CompositeTransformResult.Single<CtParameter<*>> {
        val ctParam = factory.Core().createParameter<Any>()
        ctParam.setSimpleName<CtParameter<Any>>(valueParameter.name.identifier)
        ctParam.setInferred<CtParameter<Any>>(false) // Not allowed

        // Modifiers
        val modifierList = KtModifierKind.fromValueParameter(valueParameter)
        addModifiersAsMetadata(ctParam, modifierList)
        ctParam.setVarArgs<CtParameter<Any>>(KtModifierKind.VARARG in modifierList)

        // Default value
        val defaultValue = valueParameter.defaultValue?.accept(this, null)?.single
        if(defaultValue != null) { // TODO Replace with setDefaultExpr
            ctParam.putMetadata<CtParameter<*>>(KtMetadataKeys.PARAMETER_DEFAULT_VALUE, defaultValue)
            defaultValue.setParent(ctParam)
        }

        // Type
        ctParam.setType<CtParameter<Any>>(referenceBuilder.getNewTypeReference<Any>(valueParameter.returnTypeRef))

        return ctParam.compose()
    }

    override fun visitProperty(property: FirProperty, data: Nothing?): CompositeTransformResult.Single<CtVariable<*>> {
        if(property.isLocal) return visitLocalVariable(property, data)

        val ctProperty = factory.Core().createField<Any>()
        ctProperty.setSimpleName<CtField<*>>(property.name.identifier)

        // Visit and transform initializer
        val transformedExpression = property.initializer?.accept(this, null)
        if(transformedExpression != null && transformedExpression.isSingle) {
            val initializer = transformedExpression.single
            when (initializer) {
                is CtExpression<*> -> {
                    ctProperty.setDefaultExpression<CtField<Any>>(initializer as CtExpression<Any>)
                    initializer.setParent(ctProperty)
                }
                is CtIf -> {
                    val typeRef = initializer.getMetadata(KtMetadataKeys.KT_IF_TYPE) as CtTypeReference<Any>
                    val statementExpression = initializer.wrapInStatementExpression(typeRef)
                    statementExpression.setImplicit<CtStatement>(true)
                    ctProperty.setDefaultExpression<CtField<Any>>(statementExpression)
                    initializer.setParent(ctProperty)
                    statementExpression.setParent(ctProperty)
                }
                else -> warn("Property initializer not a CtExpression or if-statement: $initializer")
            }
        }


        // Transform and add delegate to metadata if it exists
        val delegate = property.delegate?.accept(this,null)
        if(delegate != null && delegate.isSingle) {
            val ctDelegate = delegate.single
            if(ctDelegate is CtExpression<*>) {
                ctProperty.putMetadata<CtElement>(KtMetadataKeys.PROPERTY_DELEGATE, ctDelegate)
                ctDelegate.setParent(ctProperty)
            }
        }

        // Add modifiers
        addModifiersAsMetadata(ctProperty, KtModifierKind.fromProperty(property))

        val returnType = property.returnTypeRef
        // Add type
        ctProperty.setType<CtField<*>>(referenceBuilder.getNewTypeReference(returnType))

        // Mark as implicit/explicit type
        val explicitType = (returnType is FirResolvedTypeRef && returnType.delegatedTypeRef != null)
        ctProperty.putMetadata<CtField<*>>(KtMetadataKeys.VARIABLE_EXPLICIT_TYPE, explicitType)

        // Check if property stems from primary constructor value parameter, in that case this property is implicit
        val initializer = property.initializer
        if(initializer is FirQualifiedAccessExpression &&
            initializer.calleeReference is FirPropertyFromParameterResolvedNamedReference) {
            ctProperty.setImplicit<CtField<*>>(true)
        }

        // TODO getter/setter

        return ctProperty.compose()
    }

    private fun getBaseOfConst(constExpression: FirConstExpression<Number>) : LiteralBase {
        val text = constExpression.source?.psi?.text
        if(text?.startsWith("0x", ignoreCase = true) == true) return LiteralBase.HEXADECIMAL
        if(text?.startsWith("0b", ignoreCase = true) == true) return LiteralBase.BINARY
        // No octal in Kotlin
        return LiteralBase.DECIMAL
    }

    override fun <T> visitConstExpression(constExpression: FirConstExpression<T>, data: Nothing?): CompositeTransformResult<CtLiteral<T>> {
        val value = when(constExpression.kind) {
            FirConstKind.Int -> (constExpression.value as Long).toInt()
            FirConstKind.Null -> null
            FirConstKind.Boolean -> constExpression.value as Boolean
            FirConstKind.Char -> constExpression.value as Char
            FirConstKind.Byte -> constExpression.value as Byte
            FirConstKind.Short -> constExpression.value as Short
            FirConstKind.Long -> constExpression.value as Long
            FirConstKind.String -> constExpression.value as String
            FirConstKind.Float -> constExpression.value as Float
            FirConstKind.Double -> constExpression.value as Double
            FirConstKind.IntegerLiteral -> constExpression.value as Int
        }
        val l : CtLiteral<T> = factory.Code().createLiteral(value as T)

        if(value is Number) {
            l.setBase<CtLiteral<T>>(getBaseOfConst(constExpression as FirConstExpression<Number>))
            if(constExpression.value is Float || constExpression.value is Double) {
                l.putMetadata<CtLiteral<T>>(KtMetadataKeys.FLOAT_LITERAL_SCIENTIFIC, constExpression.psi?.textContains('e') == true)
            }
        }
        if(constExpression.value is String) {
            // Check if it's multiline
            val openQuote = constExpression.source.psi?.getChildrenOfType<LeafPsiElement>()?.
                firstOrNull { it.node.elementType == KtTokens.OPEN_QUOTE }
            val multiline = "\"\"\"" == openQuote?.text
            l.putMetadata<CtLiteral<T>>(KtMetadataKeys.STRING_LITERAL_MULTILINE, multiline)
        }
        return l.compose()
    }

    fun visitLocalVariable(property: FirProperty, data: Nothing?) : CompositeTransformResult.Single<CtVariable<*>> {
        val localVar = factory.Core().createLocalVariable<Any>().also {
            it.setSimpleName<CtLocalVariable<Any>>(property.name.identifier)
        }

        val transformedExpression = property.initializer?.accept(this,null)
        if(transformedExpression != null) {
            when(val initializer = transformedExpression.single) {
                is CtExpression<*> -> {
                    localVar.setDefaultExpression<CtLocalVariable<Any>>(initializer as CtExpression<Any>)
                    initializer.setParent(localVar)
                }
                is CtIf -> {
                    val typeRef = initializer.getMetadata(KtMetadataKeys.KT_IF_TYPE) as CtTypeReference<Any>
                    val statementExpression = initializer.wrapInStatementExpression(typeRef)
                    statementExpression.setImplicit<CtStatement>(true)
                    localVar.setDefaultExpression<CtLocalVariable<Any>>(statementExpression)
                    initializer.setParent(localVar)
                    statementExpression.setParent(localVar)
                }
                else -> warn("Local variable initializer not a CtExpression or if-statement: $initializer")
            }
        }

        // Transform and add delegate to metadata if it exists
        val delegate = property.delegate?.accept(this,null)
        if(delegate != null && delegate.isSingle) {
            val ctDelegate = delegate.single
            if(ctDelegate is CtExpression<*>) {
                localVar.putMetadata<CtElement>(KtMetadataKeys.PROPERTY_DELEGATE, ctDelegate)
                ctDelegate.setParent(localVar)
            }
        }

        // Add modifiers
        addModifiersAsMetadata(localVar, KtModifierKind.fromProperty(property))

        val returnType = property.returnTypeRef
        // Add type
        localVar.setType<CtLocalVariable<*>>(referenceBuilder.getNewTypeReference(returnType))

        // Mark as implicit/explicit type
        val explicitType = (returnType is FirResolvedTypeRef && returnType.delegatedTypeRef != null)
        localVar.putMetadata<CtLocalVariable<*>>(KtMetadataKeys.VARIABLE_EXPLICIT_TYPE, explicitType)
        localVar.setInferred<CtLocalVariable<Any>>(!explicitType)

        return localVar.compose()
    }

    override fun visitReturnExpression(
        returnExpression: FirReturnExpression,
        data: Nothing?
    ): CompositeTransformResult.Single<CtReturn<*>> {
        val ctReturn = factory.Core().createReturn<Any>()
        val ctExpr = returnExpression.result.accept(this,null).single

        when(ctExpr) {
            is CtExpression<*> -> {
                ctReturn.setReturnedExpression<CtReturn<Any>>(ctExpr as CtExpression<Any>)
                ctExpr.setParent(ctReturn)
            }
            is CtIf -> {
                val typeRef = ctExpr.getMetadata(KtMetadataKeys.KT_IF_TYPE) as CtTypeReference<Any>
                val statementExpression = ctExpr.wrapInStatementExpression(typeRef)
                statementExpression.setImplicit<CtStatement>(true)
                ctReturn.setReturnedExpression<CtReturn<Any>>(statementExpression)
                ctExpr.setParent(ctReturn)
                statementExpression.setParent(ctReturn)
            }
        }
        return ctReturn.compose()
    }
    
    private fun <T : CtElement> T.compose() = CompositeTransformResult.single(this)
    private fun <T : CtElement> List<CompositeTransformResult<T>>.composeManySingles() = CompositeTransformResult.many(this.map { it.single })
    private fun <T : CtElement> List<CompositeTransformResult<T>>.compose() = CompositeTransformResult.many(this)
    private fun FirClass<*>.isInterface() = this.classKind == ClassKind.INTERFACE
    private fun FirClass<*>.isClass() = this.classKind == ClassKind.CLASS
    private fun FirClass<*>.isObject() = this.classKind == ClassKind.OBJECT
    private fun FirClass<*>.isEnumClass() = this.classKind == ClassKind.ENUM_CLASS
    private fun FirWhenExpression.isIf() = this.subject == null &&
            this.subjectVariable?.apply { warn("Subject variable found: ${this}") } == null // Temporary warn, don't know what subject variable is
    private fun <T> CtExpression<T>.wrapInImplicitReturn() : CtReturn<T> {
        val r = factory.Core().createReturn<T>()
        r.setReturnedExpression<CtReturn<T>>(this)
        r.setImplicit<CtReturn<T>>(true)
        return r
    }
    private fun <T> CtStatement.wrapInStatementExpression(type : CtTypeReference<T>) : KtStatementExpression<T> {
        val se = KtStatementExpressionImpl<T>(this)
        se.setType<KtStatementExpression<T>>(type)
        return se
    }
}