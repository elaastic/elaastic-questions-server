/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.common.onboarding

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/properties")
class PropertiesController(
    @Autowired
    val messageSource: MessageSource
) {

    @GetMapping("onboarding")
    fun getI18nMessages(locale: Locale): Map<String, String> {
        return OnboardingChapter.values()
            .map { chapter -> chapter.i18nMessages }
            .flatten()
            .associateWith { i18nKey -> messageSource.getMessage(i18nKey, null, locale) }
    }

    @GetMapping("onboarding/chapter/{chapter}")
    fun getI18nMessages(locale: Locale, @PathVariable chapter: String): Map<String, String> {
        return OnboardingChapter.valueOf(chapter).i18nMessages.associateWith { i18nKey -> messageSource.getMessage(i18nKey, null, locale) }
    }
}