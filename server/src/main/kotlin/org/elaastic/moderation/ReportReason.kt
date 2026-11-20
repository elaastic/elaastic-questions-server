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