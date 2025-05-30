package dev.kansuki20.resultmapbuilder

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import java.awt.datatransfer.StringSelection

class GenerateResultMapAction : AnAction() {
    private val allowedTypes = setOf(
        "int", "long", "double", "float", "boolean", "char", "byte", "short",
        "java.lang.Integer", "java.lang.Long", "java.lang.Double", "java.lang.Float",
        "java.lang.Boolean", "java.lang.Character", "java.lang.Byte", "java.lang.Short",
        "java.lang.String", "java.time.LocalTime", "java.time.LocalDate", "java.time.LocalDateTime", "java.util.Date"
    )

    override fun actionPerformed(e: AnActionEvent) {
        val psiFile: PsiFile? = e.getData(CommonDataKeys.PSI_FILE)
        if (psiFile !is PsiJavaFile) return

        if (psiFile.classes.isEmpty()) return

        val clazz: PsiClass = psiFile.classes.first()


        val dbColumnPrefix = Prefix.getPrefix(e, false) // column 앞에 붙여줄 prefix


        val resultMapId = clazz.name!!
            .replaceFirstChar { it.lowercase() }
            .replace("Dto", "")
            .replace("dto", "")


        val sb = StringBuilder()
        sb.append("<resultMap id=\"")
            .append(resultMapId)
            .append("Map\" type=\"")
            .append(clazz.qualifiedName)
            .append("\">\n")

        val allFields = clazz.allFields

        for (i in allFields.indices) {
            val field = allFields[i]
            // 기본적인 자료형 체크
            val fieldType = field.type
            val typeText = fieldType.canonicalText

            // enum 체크
            val isEnum = (fieldType as? PsiClassType)?.resolve()?.isEnum == true

            if (typeText !in allowedTypes && !isEnum)
                continue

            val name = field.name
            val lowerName = name.toLowerCase()
            if (i == 0 && (lowerName.endsWith("idx") || lowerName.endsWith("id"))) {
                sb
                    .append("  <id column=\"")
                        .append(dbColumnPrefix)
                        .append(name.toSnakeCase())
                    .append("\" property=\"")
                        .append(name)
                    .append("\"/>\n")
            } else {
                sb
                    .append("  <result column=\"")
                        .append(dbColumnPrefix)
                        .append(name.toSnakeCase())
                    .append("\" property=\"")
                        .append(name)
                    .append("\"/>\n")
            }
        }

        sb.append("</resultMap>")
        CopyPasteManager.getInstance().setContents(StringSelection(sb.toString()))

        Notifications.Bus.notify(
            Notification(
                "ResultMapBuilder",
                "ResultMap Copied",
                "[${clazz.name}.class] - copied resultMap \uD83D\uDE80",
                NotificationType.INFORMATION
            ),
            e.project
        )
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isVisible = true
        e.presentation.isEnabled = true


        /*val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)

        val isJava = psiFile is PsiJavaFile || psiElement is PsiClass

        val className = when {
            psiFile is PsiJavaFile -> psiFile.classes.firstOrNull()?.name
            psiElement is PsiClass -> psiElement.name
            else -> null
        }

        val isDto = className?.endsWith("Dto") == true

        // java or class 에서만 보이게 함
        e.presentation.isVisible = isJava
        // Dto일 때만 클릭되게
        e.presentation.isEnabled = isDto*/
    }



    private fun String.toSnakeCase(): String {
        return this
            .replace(Regex("([a-z])([A-Z]+)"), "$1_$2")
            .lowercase()
    }
}