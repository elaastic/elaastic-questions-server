package org.elaastic.questions.player.components.explanationViewer

import org.elaastic.questions.assignment.sequence.interaction.response.Response
import java.math.BigDecimal

object ExplanationDataFactory {

    fun create(response: Response): ExplanationData {
        val explanationData = ExplanationData(response)


        return if(response.learner == response.statement.owner) {
            TeacherExplanationData(explanationData)
        } else {
            explanationData
        }
    }
}