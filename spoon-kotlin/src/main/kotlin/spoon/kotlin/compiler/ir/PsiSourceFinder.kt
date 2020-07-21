package spoon.kotlin.compiler.ir

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset

object PsiSourceFinder {
    fun getSourceElement(ktFile: KtFile, startOffset: Int, endOffset: Int): List<KtElement> {
        val res = ArrayList<KtElement>()
        ktFile.accept(object: KtTreeVisitorVoid() {
            override fun visitElement(element: PsiElement) {
                if(element.startOffset > endOffset) return
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
}