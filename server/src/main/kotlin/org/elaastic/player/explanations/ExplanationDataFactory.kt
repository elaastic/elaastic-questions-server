package org.elaastic.player.explanations

import org.elaastic.activity.response.Response

object ExplanationDataFactory {

    fun create(response: Response, explanationHasChatGPTEvaluation: Boolean): ExplanationData {
        val explanationData = ExplanationData(response, explanationHasChatGPTEvaluation)


        return if(response.learner == response.statement.owner) {
            TeacherExplanationData(explanationData)
        } else {
            explanationData
        }
    }
}