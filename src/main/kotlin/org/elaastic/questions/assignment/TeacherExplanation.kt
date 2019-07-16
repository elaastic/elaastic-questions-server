package org.elaastic.questions.assignment

import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

/**
 * @author John Tranier
 */
@Validated
data class TeacherExplanation(  // Rename of "ExplanationChoice"
        var choiceItemIndex: Int, // Rename of "item"
        @field:NotBlank var explanation: String,
        var score: Float? = null
)