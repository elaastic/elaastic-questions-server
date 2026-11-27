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

package org.elaastic.bootstrap.demo

import java.math.BigDecimal
import org.elaastic.activity.evaluation.peergrading.PeerGradingService
import org.elaastic.activity.response.ConfidenceDegree
import org.elaastic.activity.response.Response
import org.elaastic.activity.response.ResponseService
import org.elaastic.assignment.Assignment
import org.elaastic.assignment.AssignmentService
import org.elaastic.assignment.ReadyForConsolidation
import org.elaastic.material.instructional.question.QuestionType
import org.elaastic.material.instructional.question.legacy.LearnerChoice
import org.elaastic.material.instructional.subject.Subject
import org.elaastic.material.instructional.subject.SubjectService
import org.elaastic.sequence.ExecutionContext
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.SequenceService
import org.elaastic.sequence.interaction.InteractionService
import org.elaastic.sequence.phase.evaluation.EvaluationPhaseConfig
import org.elaastic.user.User
import org.elaastic.user.UserService
import org.springframework.stereotype.Service

@Service
class DemoSessionSimulation(
    private val userService: UserService,
    private val subjectService: SubjectService,
    private val assignmentService: AssignmentService,
    private val sequenceService: SequenceService,
    private val responseService: ResponseService,
    private val interactionService: InteractionService,
    private val peerGradingService: PeerGradingService
) {
    private enum class State { ENROLLED, STARTED, PUBLISHED }

    private data class Variant(
        val suffix: String,
        val description: String,
        val audience: String,
        val consolidation: ReadyForConsolidation,
        val mode: ExecutionContext,
        val state: State
    )

    private data class Participation(
        val login: String,
        val exclusiveChoice: Int,
        val multipleChoices: List<Int>,
        val confidence: ConfidenceDegree,
        val explanation: String,
        val reviewedLogin: String,
        val rating: Int
    )

    private val description = "Exercice en classe"

    private val variants = listOf(
        Variant(
            "Face à face - Finished",
            description,
            "groupe A1",
            ReadyForConsolidation.NotAtAll,
            ExecutionContext.FaceToFace,
            State.PUBLISHED
        ),
        Variant(
            "Face à face",
            description,
            "groupe A2",
            ReadyForConsolidation.AfterTeachings,
            ExecutionContext.FaceToFace,
            State.STARTED
        ),
        Variant(
            "Distant",
            description,
            "groupe C",
            ReadyForConsolidation.Immediately,
            ExecutionContext.Distance,
            State.ENROLLED
        ),
        Variant(
            "Blended",
            description,
            "groupe G",
            ReadyForConsolidation.AfterTeachings,
            ExecutionContext.Blended,
            State.ENROLLED
        )
    )

    private val msil = "msil"
    private val tsil = "tsil"
    private val jtra = "jtra"
    private val erik = "erik"

    private val participants = listOf(
        Participation(
            msil,
            1,
            listOf(2),
            ConfidenceDegree.CONFIDENT,
            "Une réponse correcte, mais pas complète",
            tsil,
            5
        ),
        Participation(
            tsil,
            1,
            listOf(2),
            ConfidenceDegree.TOTALLY_CONFIDENT,
            "Une réponse excellente et correcte, qui couvre bien toute la question.",
            jtra,
            1
        ),
        Participation(
            jtra,
            2,
            listOf(1, 3),
            ConfidenceDegree.NOT_CONFIDENT_AT_ALL,
            "Une injure envers l'enseignant",
            erik,
            2
        ),
        Participation(
            erik,
            2,
            listOf(1, 3),
            ConfidenceDegree.NOT_REALLY_CONFIDENT,
            "Une explication bancale",
            msil,
            4
        )
    )

    internal fun loadLearners(): Map<String, User> =
        participants.associate { it.login to userService.findByUsername(it.login)!! }

    internal fun createAssignments(subject: Subject, learners: Map<String, User>) {
        val assignments = variants.map { variant ->
            val assignment = Assignment(
                owner = subject.owner,
                title = "${subject.title} - ${variant.suffix}",
                description = variant.description,
                audience = variant.audience,
                readyForConsolidation = variant.consolidation
            )
            subjectService.addAssignment(subject, assignment)
            variant to assignment
        }

        assignments.forEach { (variant, assignment) ->
            learners.values.forEach { assignmentService.registerUser(it, assignment) }
            when (variant.state) {
                State.ENROLLED -> Unit
                State.STARTED -> startSequences(assignment, variant.mode)
                State.PUBLISHED -> publishResults(assignment, variant.mode, learners)
            }
        }
    }

    private fun publishResults(assignment: Assignment, mode: ExecutionContext, learners: Map<String, User>) {
        startSequences(assignment, mode)
        val responses = assignment.sequences.map { sequence ->
            sequence to participants.associate { participant ->
                participant.login to submitResponse(sequence, learners.getValue(participant.login), participant)
            }
        }

        responses.forEach { (sequence, _) ->
            interactionService.startNext(assignment.owner, sequence.activeInteraction!!)
        }
        responses.forEach { (_, byLogin) ->
            participants.forEach { participant ->
                peerGradingService.createOrUpdateLikert(
                    learners.getValue(participant.login),
                    byLogin.getValue(participant.reviewedLogin),
                    BigDecimal(participant.rating)
                )
            }
        }
        responses.forEach { (sequence, _) ->
            interactionService.startNext(assignment.owner, sequence.activeInteraction!!)
        }
        assignment.sequences.forEach { sequenceService.stop(assignment.owner, it) }
        assignment.sequences.forEach { sequenceService.publishResults(assignment.owner, it) }
    }

    private fun startSequences(assignment: Assignment, mode: ExecutionContext) {
        assignment.sequences.forEach {
            sequenceService.start(
                assignment.owner,
                it,
                mode,
                true,
                2,
                EvaluationPhaseConfig.ALL_AT_ONCE,
                false
            )
        }
    }

    private fun submitResponse(sequence: Sequence, learner: User, participant: Participation): Response {
        val statement = sequence.statement
        val choice = when (statement.questionType) {
            QuestionType.ExclusiveChoice -> LearnerChoice(listOf(participant.exclusiveChoice))
            QuestionType.MultipleChoice -> LearnerChoice(participant.multipleChoices)
            QuestionType.OpenEnded -> null
        }
        val interaction = sequenceService.getActiveInteractionForLearner(sequence, learner)
            ?: error("No active interaction, cannot submit a response")
        val response = responseService.save(
            interaction,
            Response(
                learner = learner,
                interaction = sequence.getResponseSubmissionInteraction(),
                attempt = 1,
                confidenceDegree = participant.confidence,
                explanation = participant.explanation,
                learnerChoice = choice,
                score = choice?.let {
                    Response.computeScore(it, statement.choiceSpecification ?: error("The choice specification is undefined"))
                },
                statement = statement
            )
        )
        if (sequence.executionIsDistance() || sequence.executionIsBlended()) {
            sequenceService.nextInteractionForLearner(sequence, learner)
        }
        return response
    }
}
