package org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.chatGptPrompt

import org.elaastic.questions.persistence.AbstractJpaPersistable
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