/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.elaastic.common.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Component
@Scope(WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
class MessageBuilder(
        @Autowired val messageSource: MessageSource
) {

    fun success(redirectAttributes: RedirectAttributes,
                message: String) {

        redirectAttributes.addFlashAttribute("messageType", "success")
        redirectAttributes.addFlashAttribute("messageContent", message)
    }

    fun error(redirectAttributes: RedirectAttributes,
              message: String) {

        redirectAttributes.addFlashAttribute("messageType", "error")
        redirectAttributes.addFlashAttribute("messageContent", message)
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
