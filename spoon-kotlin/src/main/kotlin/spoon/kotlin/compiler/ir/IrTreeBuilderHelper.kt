package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.descriptors.IrTemporaryVariableDescriptor
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrIfThenElseImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrTypeOperatorCallImpl
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.types.classifierOrFail
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.psi2ir.PsiSourceManager
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassNotAny
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassOrAny
import org.jetbrains.kotlin.resolve.source.getPsi
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.reflect.code.LiteralBase
import spoon.reflect.declaration.CtAnnotationType
import spoon.reflect.declaration.CtElement
import spoon.reflect.declaration.CtModule
import spoon.reflect.declaration.CtType

internal class IrTreeBuilderHelper(private val irTreeBuilder: IrTreeBuilder) {
    private val factory get() = irTreeBuilder.factory
    private val referenceBuilder get() = irTreeBuilder.referenceBuilder
    private val keywords = KtTokens.KEYWORDS.types.map { (it as KtKeywordToken).value }.toMutableList().also {
        it.add("assert") // Escaped because Spoon doesn't allow assert
    }

    private fun getKtFile(file: IrFile): KtFile {
        return irTreeBuilder.sourceManager.getKtFile(file.fileEntry as PsiSourceManager.PsiFileEntry)!!
    }

    fun createType(irClass: IrClass, context: ContextData): CtType<*> {
        val type: CtType<*> = when(irClass.kind) {
            ClassKind.CLASS -> factory.Core().createClass()
            ClassKind.INTERFACE -> factory.Core().createInterface()
            ClassKind.ENUM_CLASS -> factory.Core().createEnum<Enum<*>>() as CtType<Any>
            ClassKind.ENUM_ENTRY -> factory.Core().createClass()
            ClassKind.ANNOTATION_CLASS -> factory.Core().createClass()
            ClassKind.OBJECT -> factory.Core().createClass<Any>().apply {
                putMetadata<CtType<Any>>(KtMetadataKeys.CLASS_IS_OBJECT, true)
            }
        }
        if(!irClass.name.isSpecial) {
            type.setSimpleName<CtType<*>>(irClass.name.identifier)
        }
        type.addModifiersAsMetadata(IrToModifierKind.fromClass(irClass))
        if(irClass.annotations.isNotEmpty()) {
            type.setAnnotations<CtElement>(irClass.annotations.map { irTreeBuilder.visitAnnotation(it, context).resultSafe })
        }

        if(type !is CtAnnotationType<*>) {
            type as CtType<Any>
            val superClassNotAny = irClass.descriptor.getSuperClassNotAny()
            val superClassOrAny = irClass.descriptor.getSuperClassOrAny()
            if (superClassNotAny != null) { // ==> superClassNotAny = superClassOrAny
                type.setSuperclass<CtType<Any>>(
                    referenceBuilder.getNewTypeReference<Any>(
                        irClass.superTypes.first { it.classifierOrFail.descriptor === superClassNotAny })
                )
            }

            type.setSuperInterfaces<CtType<Any>>(irClass.superTypes.filterNot {
                it.classifierOrFail.descriptor === superClassOrAny // Check against Any, because it is listed in supertypes
            }.map {
                referenceBuilder.getNewTypeReference<Any>(it)
            }.toSet())
        }
        type.setFormalCtTypeParameters<CtType<*>>(irClass.typeParameters.map {
            irTreeBuilder.visitTypeParameter(it, context).resultSafe
        })
        return type
    }

    fun getOrCreateModule(): CtModule {
        return factory.Module().unnamedModule
    }

    fun getReceiver(irCall: IrFunctionAccessExpression) = irCall.extensionReceiver ?: irCall.dispatchReceiver

    fun getBaseOfConst(constExpression: IrConst<Number>, file: IrFile): LiteralBase {
        val ktFile = getKtFile(file)
        val text = ktFile.text.substring(constExpression.startOffset,constExpression.endOffset)
        if(text.startsWith("0x", ignoreCase = true)) return LiteralBase.HEXADECIMAL
        if(text.startsWith("0b", ignoreCase = true)) return LiteralBase.BINARY
        // No octal in Kotlin
        return LiteralBase.DECIMAL
    }

    fun isImplicitThis(irGetValue: IrGetValue, file: IrFile): Boolean {
        val text = getKtFile(file).text.substring(irGetValue.startOffset, irGetValue.endOffset)
        return !text.matches("this(@.+)?".toRegex())
    }

    fun isActualField(irElement: IrElement, file: IrFile): Boolean {
        val psiCandidates = irTreeBuilder.getSourceHelper(Empty(file)).getSourceElements(irElement.startOffset, irElement.endOffset)
        val psiElements = psiCandidates.filter {
            it.getParentOfType<KtPropertyAccessor>(true, KtClassOrObject::class.java) != null
        }
        if(psiElements.isNotEmpty()) {
            return psiElements.any { it.text == "field" }
        }
        return false
    }

    fun getThisExtensionTarget(irGetValue: IrGetValue, file: IrFile): Pair<String?, String?> {
        val text = getKtFile(file).text.substring(irGetValue.startOffset, irGetValue.endOffset)
        if(text.matches("this(@.+)?".toRegex())) {
            if(text.contains('@')) { // Target label, 'this@target'
                return "this" to text.substring(text.indexOf('@')+1)
            } else { // Just 'this'
                return "this" to null
            }
        }
        return null to null // Implicit this
    }

    fun isInfixCall(irCall: IrCall, context: ContextData): Boolean {
        val psi = irCall.symbol.descriptor.source.getPsi() // Prefer using built in source
        if(psi != null)
            return psi is KtBinaryExpression && psi.operationToken == KtTokens.IDENTIFIER
        val psiElements = irTreeBuilder.getSourceHelper(context).getSourceElements(
            irCall.startOffset,
            irCall.endOffset
        )
        return psiElements.any { it is KtBinaryExpression && it.operationToken == KtTokens.IDENTIFIER }
    }

    /**
     * Checks which non-terminal in grammar the identifier matches (throws error if it doesn't match any)
     * Kotlin grammar (spec 0.1-29):
     * Identifier:
        (Letter | Underscore) {Letter | Underscore | UnicodeDigit}
        | '`' {EscapedIdentifierCharacter} '`'
     * EscapedIdentifierCharacter:
        <any character except CR, LF, '`'', '[', ']', '<' or '>'>
     */
    private fun legalOnlyIfEscaped(id: String): Boolean {
        if(id.isEmpty()) {
            throw SpoonIrBuildException("Empty identifier")
        }
        if(id.contains("""[\[\],`<>\r\n]""".toRegex())) {
            throw SpoonIrBuildException("Illegal identifier, even if escaped \"$id\"")
        }
        if(!(id.first().isLetter() || id.first() == '_')) return true
        for(c in id) {
            if(!(c.isLetterOrDigit() || c == '_')) return true
        }
        return false
    }

    fun escapedIdentifier(name: Name): String {
        val identifier = name.asString()
        if(identifier.matches("<anonymous parameter \\d+>".toRegex())) return "_"
        // Should be return "`$identifier`" but spoon doesn't allow '`'. However, '$' is legal in java but not
        // in Kotlin (unless escaped), so it serves as a marker for an escaped identifier
        if(identifier in keywords || legalOnlyIfEscaped(identifier)) return "\$$identifier\$"
        return identifier
    }

    fun constructorBodyIsSynthetic(irBody: IrBody?): Boolean {
        return irBody is IrBlockBody &&
                irBody.statements.size <= 2 &&
                irBody.statements.all {
                    it is IrDelegatingConstructorCall || it is IrInstanceInitializerCall
                }
    }

    fun getNamedArgumentsMap(block: IrBlock, data: ContextData): List<Pair<String?, IrExpression>> {
        assert(block.origin == IrStatementOrigin.ARGUMENTS_REORDERING_FOR_CALL)
        val map = mutableListOf<Pair<String?, IrExpression>>()
        val variables = block.statements.takeWhile { it is IrVariable } as List<IrVariable>
        for(i in variables.indices) {
            val v = variables[i]
            val name = v.name.asString().substringAfter('_')
            val psiValueArg: KtValueArgument? = irTreeBuilder.getSourceHelper(data).getValueArgumentPsi(v)
            if(psiValueArg == null) { // Default to named arg if no PSI is found
                map.add(Pair(name, v.initializer!!))
            } else {
                val psiValueArgName = psiValueArg.getArgumentName()
                map.add(Pair(psiValueArgName?.text, v.initializer!!))
            }
        }
        return map
    }

    /**
     * Extracts the branch conditions from its potentially nested if-else expressions
     */
    fun resolveBranchMultiCondition(whenBranch: IrBranch, subject: IrVariable?): List<Pair<IrExpression,Boolean>> {
        if(whenBranch is IrElseBranch) return emptyList()
        if(subject == null) return listOf(whenBranch.condition to false)
        val orderedExprs = ArrayList<Pair<IrExpression,Boolean>>()
        val condition = whenBranch.condition
        if(condition !is IrIfThenElseImpl) {
            orderedExprs.add(getExprWithoutTempVar(condition, subject))
        } else {
            visitConditionsInOrder(whenBranch.condition as IrIfThenElseImpl, subject, orderedExprs)
        }

        return orderedExprs
    }

    private fun isWhenTempSubjectVar(symbol: IrValueSymbol): Boolean {
        return symbol.descriptor is IrTemporaryVariableDescriptor &&
                symbol.descriptor.name.asString().matches("tmp\\d+_subject".toRegex())
    }

    private fun isWhenSubjectVar(expr: IrStatement, subject: IrVariable): Boolean {
        return expr is IrGetValue && expr.symbol === subject.symbol
    }

    private fun getExprWithoutTempVar(irExpr: IrExpression, subject: IrVariable?): Pair<IrExpression,Boolean> {
        if(subject == null) return irExpr to false
        if(irExpr is IrCall) {
            when (irExpr.origin) {
                IrStatementOrigin.EQEQ -> {
                    val eqLhs = irExpr.getValueArgument(0)!!
                    if (isWhenSubjectVar(eqLhs, subject)) {
                        return irExpr.getValueArgument(1)!! to false
                    }
                }
                IrStatementOrigin.EXCL -> {
                    val typeOp = irExpr.dispatchReceiver as? IrTypeOperatorCall?
                    if (typeOp != null && typeOp.operator == IrTypeOperator.INSTANCEOF &&
                        isWhenSubjectVar(typeOp.argument, subject)) {
                        // Ignore not() call and change operator to !is
                        val newTypeOp = IrTypeOperatorCallImpl(
                            typeOp.startOffset,
                            typeOp.endOffset,
                            typeOp.type,
                            IrTypeOperator.NOT_INSTANCEOF,
                            typeOp.typeOperand,
                            typeOp.argument
                        )
                        return newTypeOp to true
                    }
                    val notInCall = irExpr.dispatchReceiver as? IrCall?
                    if (notInCall != null && notInCall.origin == IrStatementOrigin.NOT_IN) {
                        return getExprWithoutTempVar(notInCall, subject)
                    }
                }
                IrStatementOrigin.NOT_IN -> {
                    val arg = irExpr.getValueArgument(0)
                    if (arg != null && isWhenSubjectVar(arg, subject)) {
                        return irExpr to true
                    }
                }
            }
        }

        return irExpr to true
    }

    /**
     * Extract the actual conditions from a nested if-else that represents a list of branch conditions.
     *
     * when(x) {
     *      a, in l, is C -> {}
     * }
     * > branch condition translates to >
     * if( if(x == a) true else l.contains(x) ) true else x is C
     *
     * With that as input, this method will return [a, x in l, x is C]. Only the rhs (non-subject expr) is returned
     * for equality checks against the subject. For in/is operators, a marker indicates if lhs should be implicit.
     * The marker is needed because the condition can be "y in l" where y is not the subject, if the subject is a Boolean.
     */
    private fun visitConditionsInOrder(
        ifElse: IrIfThenElseImpl,
        subject: IrVariable?,
        list: MutableList<Pair<IrExpression,Boolean>>) {
        if(ifElse.origin != IrStatementOrigin.WHEN_COMMA) {
            list.add(ifElse to false)
            return
        }

        val lhs = ifElse.branches[0].condition
        if(lhs is IrIfThenElseImpl) {
            visitConditionsInOrder(lhs, subject, list)
        }
        else {
            list.add(getExprWithoutTempVar(lhs, subject))
        }

        val actualExpr = ifElse.branches[1].result
        list.add(getExprWithoutTempVar(actualExpr, subject))
    }

    fun getWhenSubjectVarDeclaration(irStatement: IrStatement?): IrStatement? {
        if(irStatement is IrVariable && isWhenTempSubjectVar(irStatement.symbol)) {
            return irStatement.initializer!!
        }
        return irStatement
    }
}