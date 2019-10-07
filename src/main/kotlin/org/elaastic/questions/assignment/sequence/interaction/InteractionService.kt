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

package org.elaastic.questions.assignment.sequence.interaction

import org.elaastic.questions.assignment.ia.ResponseRecommendationService
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.SequenceRepository
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseRepository
import org.elaastic.questions.assignment.sequence.interaction.results.ResultsService
import org.elaastic.questions.assignment.sequence.interaction.specification.EvaluationSpecification
import org.elaastic.questions.assignment.sequence.interaction.specification.InteractionSpecification
import org.elaastic.questions.assignment.sequence.interaction.specification.ResponseSubmissionSpecification
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class InteractionService(
        @Autowired val interactionRepository: InteractionRepository,
        @Autowired val sequenceRepository: SequenceRepository,
        @Autowired val resultsService: ResultsService,
        @Autowired val responseRepository: ResponseRepository,
        @Autowired val responseRecommendationService: ResponseRecommendationService
) {

    fun create(sequence: Sequence,
               interactionSpecification: InteractionSpecification,
               rank: Int,
               state: State = State.beforeStart): Interaction =
            Interaction(
                    interactionType = interactionSpecification.getType(),
                    rank = rank,
                    specification = interactionSpecification,
                    owner = sequence.owner,
                    sequence = sequence,
                    state = state
            ).let(interactionRepository::save)

    fun findById(id: Long): Interaction =
            interactionRepository.findById(id).get()

    fun stop(user: User, interactionId: Long) =
            stop(user, interactionRepository.getOne(interactionId))

    fun stop(user: User, interaction: Interaction): Interaction {
        require(user == interaction.owner) {
            "Only its owner can stop an interaction"
        }

        require(interaction.state == State.show) {
            "This interaction is not running... Can't be stopped"
        }

        interaction.state = State.afterStop

        val specification = interaction.specification
        when (specification) {
            is ResponseSubmissionSpecification -> specification.let {
                if (interaction.sequence.statement.hasChoices()) {
                    resultsService.updateResponsesDistribution(
                            user,
                            interaction.sequence
                    )
                }
                if (it.studentsProvideExplanation) {
                    responseRepository.findAllByInteractionAndAttempt(
                            interaction,
                            1
                    ).let { responses ->
                        interaction.explanationRecommendationMapping =
                                responseRecommendationService.computeRecommendations(
                                        responses,
                                        (interaction.sequence.getEvaluationInteraction().specification as EvaluationSpecification)
                                                .responseToEvaluateCount
                                )
                    }
                }
            }

            is EvaluationSpecification ->
                resultsService.updateResults(user, interaction.sequence)
        }

        interactionRepository.save(interaction)
        return interaction
    }

    fun start(user: User, interactionId: Long): Interaction =
            start(user, interactionRepository.getOne(interactionId))

    fun start(user: User, interaction: Interaction): Interaction {
        require(user == interaction.owner) {
            "Only its owner can start an interaction"
        }

        interaction.state = State.show
        interactionRepository.save(interaction)
        interaction.sequence.let {
            it.state = State.show
            sequenceRepository.save(it)
        }

        return interaction
    }
}