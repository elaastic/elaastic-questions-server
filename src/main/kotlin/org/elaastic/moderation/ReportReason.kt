package org.elaastic.moderation

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder

enum class ReportReason {
    FALSE_INFORMATION,
    INCOHERENCE,
    PERSONAL_JUDGMENT,
    OTHER;

    fun toHumanReadableString(messageSource: MessageSource): String {
        return messageSource.getMessage(
            "player.sequence.chatGptEvaluation.reportReason.$this",
            null,
            LocaleContextHolder.getLocale()
        )
    }
}