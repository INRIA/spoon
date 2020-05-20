package spoon.kotlin.compiler

import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.kotlin.reflect.KtExpressionStatementImpl
import spoon.kotlin.reflect.KtModifierKind
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.expressions.FirBlock
import org.jetbrains.kotlin.fir.expressions.FirConstExpression
import org.jetbrains.kotlin.fir.expressions.FirConstKind
import org.jetbrains.kotlin.fir.expressions.FirWhenExpression
import org.jetbrains.kotlin.fir.psi
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.visitors.CompositeTransformResult
import org.jetbrains.kotlin.fir.visitors.FirVisitor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import spoon.reflect.code.*
import spoon.reflect.declaration.*
import spoon.reflect.factory.Factory
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
        element.putMetadata<CtElement>(KtMetadataKeys.KT_MODIFIERS, modifierList.toSet())
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

    override fun visitRegularClass(firClass: FirRegularClass, data: Nothing?): CompositeTransformResult<CtElement> {
        val module = helper.getOrCreateModule(file, factory)
        val pkg = if(file.packageFqName.isRoot) module.rootPackage else
            factory.Package().getOrCreate(file.packageFqName.shortName().identifier, module)
        val type = helper.createType(firClass)
        pkg.addType<CtPackage>(type)

        // Modifiers
        val modifierList = KtModifierKind.fromClass(firClass)
        addModifiersAsMetadata(type, modifierList)

        val decls = firClass.declarations.map { it.accept(this,null).single.also { decl ->
                decl.setParent(type)
                when(decl) {
                    is CtField<*> -> type.addField(decl)
                    is CtMethod<*> -> {
                        if(firClass.isInterface() && decl.body != null) {
                            decl.setDefaultMethod<Nothing>(true)
                        }
                        type.addMethod(decl)
                    }
                }

            }
        }

        return type.compose()
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
            is CtExpression<*> -> e.wrapAsStatementExpression()
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
                s.wrapAsStatementExpression()
            } else {
                s
            }
            }
        } as List<CtStatement>
        ktBlock.setStatements<CtBlock<*>>(statements)
        return ktBlock.compose()
    }

    override fun visitSimpleFunction(firFunction: FirSimpleFunction, data: Nothing?): CompositeTransformResult.Single<CtMethod<*>> {
        val ctMethod = factory.Core().createMethod<Any>()
        ctMethod.setSimpleName<CtMethod<Any>>(firFunction.name.identifier)

        // Add modifiers
        val modifiers = KtModifierKind.fromFunctionDeclaration(firFunction)
        addModifiersAsMetadata(ctMethod, modifiers)

        // Add body
        val body = firFunction.body
        if(body != null) {
            val ctBody = body.accept(this, null).single
            ctMethod.setBody<CtMethod<Any>>(ctBody as CtStatement)
        }

        // Set (return) type
        ctMethod.setType<CtMethod<*>>(referenceBuilder.getNewTypeReference<Any>(firFunction.returnTypeRef))

        return ctMethod.compose()
    }

    override fun visitProperty(property: FirProperty, data: Nothing?): CompositeTransformResult.Single<CtVariable<*>> {
       // if(property.isLocal) return visitLocalVariable(property, data)

        val ctProperty = factory.Core().createField<Any>()
        ctProperty.setSimpleName<CtField<*>>(property.name.identifier)

        // Visit and transform initializer
        val transformedExpression = property.initializer?.accept(this, null)
        if(transformedExpression != null && transformedExpression.isSingle) {
            val initializer = transformedExpression.single
            if(initializer is CtExpression<*>) {
                ctProperty.setDefaultExpression<CtField<Any>>(initializer as CtExpression<Any>)
                initializer.setParent(ctProperty)
            }
            else {
                warn("Property initializer not a CtExpression: $initializer")
            }
        }

        // Transform and add delegate to metadata if it exists
        val delegate = property.delegate?.accept(this,null)
        if(delegate != null && delegate.isSingle) {
            val ctDelegate = delegate.single
            if(ctDelegate is CtExpression<*>) {
                ctProperty.putMetadata<CtElement>(KtMetadataKeys.PROPERTY_DELEGATE, ctDelegate)
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

        // TODO getter/setter

        // TODO Comments

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

    fun visitLocalVariable(property: FirProperty, data: Nothing?) : CompositeTransformResult<CtVariable<*>> {
        return TODO()
    }


    private fun Any?.notNullOrFalse() = this != null
    private fun <T : CtElement> T.compose() = CompositeTransformResult.single(this)
    private fun <T : CtElement> List<CompositeTransformResult<T>>.composeManySingles() = CompositeTransformResult.many(this.map { it.single })
    private fun <T : CtElement> List<CompositeTransformResult<T>>.compose() = CompositeTransformResult.many(this)
    private fun FirClass<*>.isInterface() = this.classKind == ClassKind.INTERFACE
    private fun FirClass<*>.isClass() = this.classKind == ClassKind.CLASS
    private fun FirClass<*>.isObject() = this.classKind == ClassKind.OBJECT
    private fun FirClass<*>.isEnumClass() = this.classKind == ClassKind.ENUM_CLASS
    private fun FirWhenExpression.isIf() = this.subject == null &&
            this.subjectVariable?.apply { warn("Subject variable found: ${this}") } == null // Temporary warn, don't know what subject variable is
    private fun <T> CtExpression<T>.wrapAsStatementExpression() =
        KtExpressionStatementImpl<T>(this)

}