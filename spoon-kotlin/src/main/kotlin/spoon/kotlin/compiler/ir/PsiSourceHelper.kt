package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.expressions.IrComposite
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.psi2ir.PsiSourceManager
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance
import org.jetbrains.kotlin.utils.addToStdlib.indexOfOrNull
import java.util.*
import kotlin.collections.ArrayList

class PsiSourceHelper {
    private val labelOffSetMap = RangeMap()
    private val ktFile: KtFile
    private val previousStack = LinkedList<KtElement>()
    val sourceText: String
        get() = ktFile.text

    constructor(ktFile: KtFile) {
        this.ktFile = ktFile
        init()
    }

    constructor(sourceManager: PsiSourceManager, irFile: IrFile) {
        this.ktFile = sourceManager.getKtFile(irFile)!!
        init()
    }

    private fun popCache(): KtElement {
        if(previousStack.size == 1) return previousStack.peek()
        return previousStack.pop()
    }

    private fun init() {
        previousStack.push(ktFile)
        ktFile.accept(object : KtTreeVisitorVoid() {
            override fun visitElement(element: PsiElement) {
                if(element is KtLabeledExpression) {
                    labelOffSetMap.put(element)
                }
                super.visitElement(element)
            }
        })
    }

    fun getLabelOrNull(irElement: IrElement): String? {
        val labeledExpr = labelOffSetMap[irElement.startOffset, irElement.endOffset] ?: return null
        val candidate = labeledExpr.baseExpression
        val label = labeledExpr.getLabelName()
        if(candidate != null &&
            candidate.startOffset == irElement.startOffset &&
            candidate.endOffset == irElement.endOffset
        ) {
            if (label != null) return label
        }

        val sourceElements = getSourceElements(irElement.startOffset, irElement.endOffset, labeledExpr)
        for(sourceCandidate in sourceElements) {
            var labelParent = sourceCandidate.parent
            while(labelParent is KtDotQualifiedExpression) {
                labelParent = labelParent.parent
            }
            if(labelParent === labeledExpr) {
                if(label != null) return label
                val labelChild = labelParent.labelQualifier?.firstChild?.firstChild
                if(labelChild?.node?.elementType == KtTokens.IDENTIFIER) {
                    return labelChild?.text
                }
            }
        }
        return null
    }

    private fun clearUpTo(startOffset: Int, endOffset: Int) {
        var cachedElem = previousStack.peek()
        while(previousStack.size > 1 && (cachedElem.startOffset > startOffset || cachedElem.endOffset < endOffset)) {
            popCache()
            cachedElem = previousStack.peek()
        }
    }

    private fun pushCache(element: KtElement) {
        if(element === ktFile) return
        clearUpTo(element.startOffset, element.endOffset)
        previousStack.push(element)
    }

    private fun getSourceElements(startOffset: Int, endOffset: Int, startElement: KtElement?): List<KtElement> {
        clearUpTo(startOffset, endOffset)
        if(startElement != null) pushCache(startElement)
        val actualElement = previousStack.peek()
        val res = ArrayList<KtElement>()
        actualElement.accept(object: KtTreeVisitorVoid() {
            override fun visitElement(element: PsiElement) {
                if(element.startOffset > endOffset || element.endOffset < startOffset) return
                if(element is KtElement) {
                    if(element.startOffset == startOffset && element.endOffset == endOffset)
                        res += element
                }
                super.visitElement(element)
            }
        }
        )
        if(res.size == 1)
            pushCache(res[0])

        return res
    }

    fun getSourceElements(startOffset: Int, endOffset: Int): List<KtElement>
        = getSourceElements(startOffset, endOffset, null)

    private fun sourceTextIs(start: Int, end: Int, predicate: (String) -> Boolean): Boolean {
        return predicate(ktFile.text.substring(start, end))
    }

    fun sourceTextIs(element: IrElement, predicate: (String) -> Boolean): Boolean =
        sourceTextIs(element.startOffset, element.endOffset, predicate)

    fun sourceElementIs(element: IrElement, predicate: (KtElement) -> Boolean): Boolean {
        return getSourceElements(element.startOffset, element.endOffset, null).any(predicate)
    }

    fun hasExplicitType(property: KtProperty?): Boolean {
        return property != null && property.children.any { it is KtTypeReference }
    }

    fun hasExplicitType(function: KtFunction?): Boolean {
        return function != null && function.hasDeclaredReturnType()
    }

    fun returnTargetLabelOrNull(irReturn: IrReturn): String? {
        val sourceText = ktFile.text.substring(irReturn.startOffset, irReturn.endOffset)
        if(sourceText.startsWith("return@")) {
            val labelStart = sourceText.indexOf('@')+1
            val labelEnd = sourceText.indexOfOrNull(' ', labelStart) ?: sourceText.length
            return sourceText.substring(labelStart, labelEnd)
        }
        return null
    }

    fun destructuredNames(irComposite: IrComposite): List<String> {
        val psi = getSourceElements(irComposite.startOffset, irComposite.endOffset).
            firstIsInstance<KtDestructuringDeclaration>()
        return psi.entries.map { it.text }
    }

    fun getValueArgumentPsi(irExpr: IrElement): KtValueArgument? {
        val psi = getSourceElements(irExpr.startOffset, irExpr.endOffset)
        for(element in psi) {
            if(element is KtValueArgument) {
                return element
            }
            val p = element.getParentOfType<KtValueArgument>(true, KtCallExpression::class.java)
            if(p != null) {
                return p
            }
        }
        return null
    }

    fun getNamedArgumentIfAny(irExpr: IrExpression): String? {
        return getValueArgumentPsi(irExpr)?.getArgumentName()?.text
    }
}

/*
 Naive map, but the amount of labels in a file is expected to be so low that the performance gain
 of an Interval Tree would be negligible
 */
private class RangeMap {
    private val expressions = ArrayList<KtLabeledExpression>()

    fun put(expression: KtLabeledExpression) {
        var i = 0
        while(i < expressions.size) {
            if(expression.startOffset < expressions[i].startOffset)
                break
            i += 1
        }
        expressions.add(i, expression)
    }

    operator fun get(range: IntRange): KtLabeledExpression? {
        return expressions.firstOrNull {
            range.first >= it.startOffset && range.last <= it.endOffset
        }
    }

    operator fun get(start: Int, end: Int): KtLabeledExpression? {
        return expressions.firstOrNull {
            start >= it.startOffset && end <= it.endOffset
        }
    }
}