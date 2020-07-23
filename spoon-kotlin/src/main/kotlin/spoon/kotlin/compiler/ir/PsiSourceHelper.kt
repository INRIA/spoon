package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import org.jetbrains.kotlin.psi2ir.PsiSourceManager

class PsiSourceHelper {
    private val labelOffSetMap = RangeMap()
    private val ktFile: KtFile

    constructor(ktFile: KtFile) {
        this.ktFile = ktFile
        init()
    }

    constructor(sourceManager: PsiSourceManager, irFile: IrFile) {
        this.ktFile = sourceManager.getKtFile(irFile)!!
        init()
    }

    private fun init() {
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

    private fun getSourceElements(startOffset: Int, endOffset: Int, startElement: PsiElement): List<KtElement> {
        val res = ArrayList<KtElement>()
        startElement.accept(object: KtTreeVisitorVoid() {
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
        return res
    }

    fun getSourceElements(startOffset: Int, endOffset: Int): List<KtElement>
        = getSourceElements(startOffset, endOffset, ktFile)

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