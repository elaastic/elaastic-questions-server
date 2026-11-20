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

package org.elaastic.ai.evaluation.chatgpt.prompt

import org.elaastic.common.persistence.AbstractJpaPersistable
import java.util.*
import javax.persistence.*

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

/**
 * A prompt for a chatGpt evaluation
 *
 * @param startDate the date when the prompt is created
 * @param active whether the prompt is active
 * @param content the content of the prompt
 * @param language the language of the prompt
 * @property endDate the date when the prompt is deactivated
 */
@Entity
class ChatGptPrompt (

    @NotNull
    var startDate: Date = Date(),

    @NotNull
    var active: Boolean = true,

    @NotNull
    @NotBlank
    var content:String,

    @NotNull
    var language:String = "fr"

) : AbstractJpaPersistable<Long>() {

    @Version
    var version: Long? = null

    var endDate: Date? = null

}