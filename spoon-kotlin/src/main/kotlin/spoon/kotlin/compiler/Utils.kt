package spoon.kotlin.compiler

import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiRecursiveElementVisitor
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.kdoc.psi.api.KDocElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.getNextSiblingIgnoringWhitespace
import org.jetbrains.kotlin.psi.psiUtil.getPrevSiblingIgnoringWhitespace

fun KtElement.getTrailingCommentOrNull() : PsiComment? {
    val sibling = this.getNextSiblingIgnoringWhitespace(withItself = false)
    return if(sibling != null && sibling is PsiComment) sibling else null
}

fun KtElement.getLeadingCommentOrNull() : PsiComment? {
    val sibling = this.getPrevSiblingIgnoringWhitespace(withItself = false)
    return if(sibling != null && sibling is PsiComment) sibling else null
}

fun String.normalizeLineBreaks() : String = this.replace(System.lineSeparator(), "\n")

/**
 * Find the position of line separators ('\n'), as they occur in the text representation of this
 * KtFile, after EOL normalization.
 *
 */
fun KtFile.lineSeparatorPositionsRaw() : IntArray {
    val lineSeparatorPositions = ArrayList<Int>()
    this.text.normalizeLineBreaks().forEachIndexed { i, c -> if(c == '\n') lineSeparatorPositions.add(i) }
    return lineSeparatorPositions.toIntArray()
}

fun KtFile.hasNormalizedEOL() : Boolean = this.textContains('\r')

/**
 * Find the position of line separators ('\n'), as provided by the PSI. The result might
 * differ from KtFile.lineSeparatorPositionsRaw() if there are parse errors, i.e. the PSI tree contains error nodes.
 *
 * Note that this function assumes that the KtFile has been built from a source with
 * Unix-like EOL ('\n').
 */
fun KtFile.lineSeparatorPositions() : IntArray {
    val lineSeparatorPositions = ArrayList<Int>()
    this.accept(object : PsiRecursiveElementVisitor() {
        override fun visitElement(element: PsiElement) {
            when(element) {
                is PsiWhiteSpace -> element.text
                is KDoc -> null
                is KDocElement -> null
                is PsiComment -> element.text
                else -> null
            }?.let { it.forEachIndexed { i, c ->
                    if(c == '\n') {
                        lineSeparatorPositions.add(element.textOffset + i)
                    }
                }
             } ?: super.visitElement(element)
        }
    })
    return lineSeparatorPositions.toIntArray()
}
