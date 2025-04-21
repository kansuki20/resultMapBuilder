package dev.kansuki20.resultmapbuilder

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

object Prefix {
    private const val prefixKey = "resultMapBuilder.prefix"

    // db column의 prefix 입력받을지 정하기
    fun getPrefix(
        e: AnActionEvent,
        showCurrentPrefix: Boolean,
    ): String {
        val props = PropertiesComponent.getInstance()
        var prefix = props.getValue(prefixKey)

        if (prefix == null || showCurrentPrefix) {
            prefix = Messages.showInputDialog(
                e.project,
                "Enter Db column prefix (e.g., ka_):",
                "ResultMapBuilder - Column Prefix",
                Messages.getQuestionIcon(),
                prefix?:"",
                null
            )

            if (prefix == null) return ""

            prefix = prefix.trim()
            props.setValue(prefixKey, prefix)
        }

        return prefix
    }

    // prefix 초기화
    fun resetPrefix() {
        PropertiesComponent.getInstance().unsetValue(prefixKey)
    }
}