package dev.kansuki20.resultmapbuilder

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import java.awt.datatransfer.StringSelection

class GenerateResultMapAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val psiFile: PsiFile? = e.getData(CommonDataKeys.PSI_FILE)
        if (psiFile !is PsiJavaFile) return

        if (psiFile.classes.isEmpty()) return

        val clazz: PsiClass = psiFile.classes.first()

        val sb = StringBuilder()
        sb.append("<resultMap id=\"")
            .append(clazz.name!!.replaceFirstChar { it.lowercase() })
            .append("Map\" type=\"")
            .append(clazz.qualifiedName)
            .append("\">\n")

        for (field in clazz.allFields) {
            val name = field.name
            sb
                .append("  <result property=\"").append(name)
                .append("\" column=\"")
                .append(name.toSnakeCase())
                .append("\"/>\n")
        }

        sb.append("</resultMap>")
        CopyPasteManager.getInstance().setContents(StringSelection(sb.toString()))

        Notifications.Bus.notify(
            Notification(
                "ResultMapBuilder",
                "ResultMap Copied",
                "[${clazz.name}.class] - copied resultMap\uD83D\uDE80",
                NotificationType.INFORMATION
            ),
            e.project
        )
    }

    override fun update(e: AnActionEvent) {
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) as? PsiJavaFile
        val clazz = psiFile?.classes?.firstOrNull()
        e.presentation.isEnabledAndVisible = clazz != null && clazz.name?.endsWith("Dto") == true
    }


    private fun String.toSnakeCase(): String {
        return this
            .replace(Regex("([a-z])([A-Z]+)"), "$1_$2")
            .lowercase()
    }
}