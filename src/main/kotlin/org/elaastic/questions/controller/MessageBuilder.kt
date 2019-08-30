package org.elaastic.questions.controller

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.servlet.mvc.support.RedirectAttributes

class MessageBuilder(
        private val messageSource: MessageSource
) {

    fun success(redirectAttributes: RedirectAttributes,
                code: String,
                vararg args: String) {

        redirectAttributes.addFlashAttribute("messageType", "success")
        redirectAttributes.addFlashAttribute(
                "messageContent",
                internalMessage(code, arrayOf(*args))
        )
    }

    fun message(code: String, vararg args: String): String {
        return internalMessage(code, arrayOf(*args))
    }

    private fun internalMessage(code: String, args: Array<String>? = null): String {
        return messageSource.getMessage(
                code,
                args,
                LocaleContextHolder.getLocale()
        )
    }
}