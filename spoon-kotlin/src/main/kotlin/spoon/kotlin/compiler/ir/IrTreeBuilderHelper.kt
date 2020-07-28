package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.psi2ir.PsiSourceManager
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassNotAny
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces
import org.jetbrains.kotlin.resolve.source.getPsi
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.reflect.code.LiteralBase
import spoon.reflect.declaration.CtModule
import spoon.reflect.declaration.CtType

internal class IrTreeBuilderHelper(private val irTreeBuilder: IrTreeBuilder) {
    private val factory get() = irTreeBuilder.factory
    private val referenceBuilder get() = irTreeBuilder.referenceBuilder
    private val keywords = KtTokens.KEYWORDS.types.map { (it as KtKeywordToken).value }

    private fun getKtFile(file: IrFile): KtFile {
        return irTreeBuilder.sourceManager.getKtFile(file.fileEntry as PsiSourceManager.PsiFileEntry)!!
    }

    fun createType(irClass: IrClass, context: ContextData): CtType<*> {
        val type: CtType<Any> = when(irClass.kind) {
            ClassKind.CLASS -> factory.Core().createClass()
            ClassKind.INTERFACE -> factory.Core().createInterface()
            ClassKind.ENUM_CLASS -> factory.Core().createEnum<Enum<*>>() as CtType<Any>
            ClassKind.ENUM_ENTRY -> TODO()
            ClassKind.ANNOTATION_CLASS -> TODO()
            ClassKind.OBJECT -> factory.Core().createClass<Any>().apply {
                putMetadata<CtType<Any>>(KtMetadataKeys.CLASS_IS_OBJECT, true)
            }
        }
        type.setSimpleName<CtType<*>>(irClass.name.identifier)
        type.addModifiersAsMetadata(IrToModifierKind.fromClass(irClass))

        val superClass = irClass.descriptor.getSuperClassNotAny()
        if(superClass != null) {
            type.setSuperclass<CtType<Any>>(irTreeBuilder.referenceBuilder.getNewTypeReference<Any>(superClass))
        }
        type.setSuperInterfaces<CtType<Any>>(irClass.descriptor.getSuperInterfaces().map {
            referenceBuilder.getNewTypeReference<Any>(it)
        }.toSet())

        type.setFormalCtTypeParameters<CtType<*>>(irClass.typeParameters.map {
            irTreeBuilder.visitTypeParameter(it, context).resultUnsafe
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
        return text != "this"
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
}