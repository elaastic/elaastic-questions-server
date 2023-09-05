package org.elaastic.questions.assignment.sequence.interaction.chatgptEvaluation.chatgptPrompt

import org.elaastic.questions.persistence.AbstractJpaPersistable
import java.util.*
import javax.persistence.*

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class ChatgptPrompt (

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