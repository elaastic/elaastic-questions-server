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

import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.sequence.Sequence
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
        @Autowired val interactionRepository: InteractionRepository,
        @Autowired val assignmentService: AssignmentService
        ) {

    fun canUpdateResults(user: User, sequence: Sequence): Boolean =
            user == sequence.owner ||
                    (
                            !sequence.isStopped() &&
                                    assignmentService.userIsRegisteredInAssignment(user, sequence.assignment!!) &&
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

        responseService.findAll(sequence).let { responseSet ->
            if (sequence.statement.hasChoices()) {
                updateResponsesDistribution(sequence, responseSet)
            }
            updateExplanationsMeanGrade(responseSet, sequence)
        }
    }

    fun updateResponsesDistribution(user: User, sequence: Sequence) {
        checkCanUpdateResults(user, sequence)

        require(sequence.statement.hasChoices()) {
            "This sequence is not bound to a choice question"
        }

        updateResponsesDistribution(
                sequence,
                responseService.findAll(sequence)
        )
    }

    private fun updateResponsesDistribution(sequence: Sequence,
                                            responseSet: ResponseSet) {
        sequence.getResponseSubmissionInteraction().let {
            it.results = ResponsesDistributionFactory.build(
                    it.sequence.statement.choiceSpecification!!,
                    responseSet
            )
            interactionRepository.save(it)
        }
    }

    fun updateExplanationsMeanGrade(responseSet: ResponseSet, sequence: Sequence) {
        responseSet[sequence.whichAttemptEvaluate()]
                .forEach { responseService.updateMeanGradeAndEvaluationCount(it) }
    }
}
