package org.elaastic.questions.assignment.sequence.interaction.results

import org.elaastic.questions.assignment.choice.ChoiceSpecification
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseSet

object ResponsesDistributionFactory {

    fun build(choiceSpecification: ChoiceSpecification,
              responseSet: ResponseSet)  =
            ResponsesDistribution(
                    ResponsesDistributionOnAttempt(
                            choiceSpecification.nbCandidateItem,
                            responseSet[1]
                    ),
                    if (responseSet[2].isNotEmpty()) {
                        ResponsesDistributionOnAttempt(
                                choiceSpecification.nbCandidateItem,
                                responseSet[2]
                        )
                    } else null
            )
}