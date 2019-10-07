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
package org.elaastic.questions.assignment.sequence.interaction.results

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.InteractionRepository
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseSet
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class ResultsService(
        @Autowired val responseService: ResponseService,
        @Autowired val interactionRepository: InteractionRepository
        ) {

    fun canUpdateResults(user: User, sequence: Sequence): Boolean =
            user == sequence.owner ||
                    (
                            !sequence.isStopped() &&
                                    user.isRegisteredInAssignment(sequence.assignment!!) &&
                                    sequence.executionIsDistance()
                            )

    fun checkCanUpdateResults(user: User, sequence: Sequence) {
        require(canUpdateResults(user, sequence)) {
            "user cannot update results"
        }
    }

    fun updateResults(user: User, sequence: Sequence) {
        require(canUpdateResults(user, sequence)) {
            "user cannot update results"
        }

        val responseSubmissionInteraction = sequence.getResponseSubmissionInteraction()
        responseService.findAll(responseSubmissionInteraction).let { responseSet ->
            if (sequence.statement.hasChoices()) {
                updateResponsesDistribution(responseSubmissionInteraction, responseSet)
            }
            updateExplanationsMeanGrade(responseSet, sequence)
        }
    }

    fun updateResponsesDistribution(user: User, sequence: Sequence): Unit {
        checkCanUpdateResults(user, sequence)

        require(sequence.statement.hasChoices()) {
            "This sequence is not bound to a choice question"
        }

        sequence.getResponseSubmissionInteraction().let {
            updateResponsesDistribution(
                    it,
                    responseService.findAll(it)
            )
        }
    }

    private fun updateResponsesDistribution(responseSubmissionInteraction: Interaction,
                                            responseSet: ResponseSet) {
        responseSubmissionInteraction.results = ResponsesDistributionFactory.build(
                responseSubmissionInteraction.sequence.statement.choiceSpecification!!,
                responseSet
        )
        interactionRepository.save(responseSubmissionInteraction)
    }

    fun updateExplanationsMeanGrade(responseSet: ResponseSet, sequence: Sequence) {
        responseSet[if (sequence.executionIsFaceToFace()) 1 else 2]
                .forEach { responseService.updateMeanGrade(it) }
    }
}