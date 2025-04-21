package dev.kansuki20.resultmapbuilder

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile

class ResetPrefixAction: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val psiFile: PsiFile? = e.getData(CommonDataKeys.PSI_FILE)
        if (psiFile !is PsiJavaFile) return

        if (psiFile.classes.isEmpty()) return

        Prefix.getPrefix(e, true)
    }
}