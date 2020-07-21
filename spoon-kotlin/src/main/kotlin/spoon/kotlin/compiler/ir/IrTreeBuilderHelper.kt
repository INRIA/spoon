package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.util.file
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi2ir.PsiSourceManager
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassNotAny
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces
import spoon.kotlin.ktMetadata.KtMetadataKeys
import spoon.reflect.code.LiteralBase
import spoon.reflect.declaration.CtModule
import spoon.reflect.declaration.CtType

internal class IrTreeBuilderHelper(private val irTreeBuilder: IrTreeBuilder) {
    private val factory get() = irTreeBuilder.factory
    private val referenceBuilder get() = irTreeBuilder.referenceBuilder
    private val keywords = KtTokens.KEYWORDS.types.map { (it as KtKeywordToken).value }

    private fun getKtFile(irDeclaration: IrDeclaration): KtFile {
        return irTreeBuilder.sourceManager.getKtFile(irDeclaration.file)!!
    }

    private fun getKtFile(file: IrFile): KtFile {
        return irTreeBuilder.sourceManager.getKtFile(file.fileEntry as PsiSourceManager.PsiFileEntry)!!
    }

    fun createType(irClass: IrClass): CtType<*> {
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
            irTreeBuilder.visitTypeParameter(it, null).resultUnsafe
        })

        return type
    }

    fun getOrCreateModule(): CtModule {
        return factory.Module().unnamedModule
    }

    fun getReceiver(irCall: IrCall) = irCall.extensionReceiver ?: irCall.dispatchReceiver

    fun getBaseOfConst(constExpression: IrConst<Number>, file: IrFile): LiteralBase {
        val ktFile = getKtFile(file)
        val text = ktFile.text.substring(constExpression.startOffset,constExpression.endOffset)
        if(text.startsWith("0x", ignoreCase = true) == true) return LiteralBase.HEXADECIMAL
        if(text.startsWith("0b", ignoreCase = true) == true) return LiteralBase.BINARY
        // No octal in Kotlin
        return LiteralBase.DECIMAL
    }

    fun isImplicitThis(irGetValue: IrGetValue, file: IrFile): Boolean {
        val text = getKtFile(file).text.substring(irGetValue.startOffset, irGetValue.endOffset)
        return text != "this"
    }

    fun escapedIdentifier(name: Name): String {
        val identifier = name.identifier
       // if('$' in identifier | identifier in keywords) return "`$identifier`"
        if('$' in identifier || identifier in keywords) return "\$$identifier\$"
        return identifier
    }
}