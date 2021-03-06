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

package org.elaastic.questions.player

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.ExecutionContext
import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.LearnerSequenceService
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.assignment.sequence.interaction.InteractionService
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.interaction.results.AttemptNum
import org.elaastic.questions.assignment.sequence.interaction.results.ItemIndex
import org.elaastic.questions.assignment.sequence.interaction.results.ResultsService
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.pagination.PaginationUtil
import org.elaastic.questions.player.websocket.AutoReloadSessionHandler
import org.elaastic.questions.subject.statement.Statement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.lang.IllegalArgumentException
import java.util.HashMap
import javax.persistence.EntityNotFoundException
import kotlin.IllegalStateException


@Controller
@RequestMapping("/player")
class PlayerController(
        @Autowired val assignmentService: AssignmentService,
        @Autowired val sequenceService: SequenceService,
        @Autowired val learnerSequenceService: LearnerSequenceService,
        @Autowired val interactionService: InteractionService,
        @Autowired val responseService: ResponseService,
        @Autowired val peerGradingService: PeerGradingService,
        @Autowired val messageBuilder: MessageBuilder,
        @Autowired val resultsService: ResultsService
) {

    private val autoReloadSessionHandler = AutoReloadSessionHandler

    @GetMapping(value = ["", "/", "/index"])
    fun index(authentication: Authentication,
              model: Model,
              @RequestParam("page") page: Int?,
              @RequestParam("size") size: Int?): String {
        val user: User = authentication.principal as User

        assignmentService.findAllAssignmentsForLearner(
                user,
                PageRequest.of((page ?: 1) - 1, size ?: 10, Sort.by(Sort.Direction.DESC, "lastUpdated"))
        ).let {
            model.addAttribute("user", user)
            model.addAttribute("learnerAssignmentPage", it)
            model.addAttribute(
                    "pagination",
                    PaginationUtil.buildInfo(
                            it.totalPages,
                            page,
                            size
                    )
            )
        }

        return "/player/index"
    }

    @GetMapping("/register")
    fun register(authentication: Authentication,
                 model: Model,
                 @RequestParam("globalId") globalId: String?): String {
        val user: User = authentication.principal as User

        if (globalId == null || globalId == "") {
            throw IllegalArgumentException(
                    messageBuilder.message("assignment.register.empty.globalId")
            )
        }

        assignmentService.findByGlobalId(globalId).let {
            if (it == null) {
                throw EntityNotFoundException(
                        messageBuilder.message("assignment.globalId.does.not.exist")
                )
            }

            assignmentService.registerUser(user, it)
            return "redirect:/player/assignment/${it.id}/play"
        }
    }

    @GetMapping("/assignment/{assignmentId}/play/sequence/{sequenceId}",
            "/assignment/{assignmentId}/play",
            "/assignment/{assignmentId}/{sequenceId}")
    fun playAssignment(authentication: Authentication,
                       model: Model,
                       @PathVariable assignmentId: Long,
                       @PathVariable sequenceId: Long?): String {

        val user: User = authentication.principal as User
        val assignment: Assignment = assignmentService.get(assignmentId, true)

        if (assignment.sequences.isEmpty()) {
            return "redirect:/subject/${assignment.subject!!.id}"
            throw IllegalStateException("Assignment $assignmentId has no sequences")
        }

        val sequenceIdValid:Long = if (sequenceId != null) sequenceId else assignment.sequences.first().id!!

        // TODO Improve data fetching (should start from the assignment)
        sequenceService.get(sequenceIdValid, true).let { sequence ->
            model.addAttribute("user", user)
            val teacher = user == sequence.owner
            val nbRegisteredUsers = assignmentService.getNbRegisteredUsers(sequence.assignment!!)

            model.addAttribute(
                    "playerModel",
                    if (teacher)
                        PlayerModelFactory.buildForTeacher(
                                user = user,
                                sequence = sequence,
                                nbRegisteredUsers = nbRegisteredUsers,
                                sequenceToUserActiveInteraction = assignment.sequences.associate { it to it.activeInteraction },
                                messageBuilder = messageBuilder,
                                findAllResponses = { responseService.findAll(sequence, excludeFakes = false) },
                                sequenceStatistics = sequenceService.getStatistics(sequence), userCanRefreshResults = { resultsService.canUpdateResults(user, sequence) }
                        )
                    else PlayerModelFactory.buildForLearner(
                            user = user,
                            sequence = sequence,
                            nbRegisteredUsers = nbRegisteredUsers,
                            sequenceToUserActiveInteraction = assignment.sequences.associate {
                                it to if (it.executionIsFaceToFace())
                                    it.activeInteraction
                                else learnerSequenceService.findOrCreateLearnerSequence(user, it).activeInteraction
                            },
                            messageBuilder = messageBuilder,
                            getActiveInteractionForLearner = { learnerSequenceService.getActiveInteractionForLearner(user, sequence) },
                            hasResponseForUser = { attemptNum: AttemptNum ->
                                responseService.hasResponseForUser(user, sequence, attemptNum)
                            },
                            findAllResponses = { responseService.findAll(sequence, excludeFakes = false) },
                            findAllRecommandedResponsesForUser = {
                                responseService.findAllRecommandedResponsesForUser(
                                        sequence = sequence,
                                        attempt = sequence.whichAttemptEvaluate(),
                                        user = user
                                )
                            },
                            userHasPerformedEvaluation = {
                                peerGradingService.userHasPerformedEvaluation(user, sequence)
                            },
                            getFirstAttemptResponse = {
                                responseService.find(user, sequence)
                            },
                            userCanRefreshResults = {
                                resultsService.canUpdateResults(user, sequence)
                            }
                    )
            )
        }

        return "/player/assignment/sequence/play"
    }

    @GetMapping("/assignment/{id}/nbRegisteredUsers")
    @ResponseBody
    fun getNbRegisteredUsers(@PathVariable id: Long): Int {
        return assignmentService.getNbRegisteredUsers(id)
    }


    @GetMapping("/sequence/{id}/start")
    fun startSequence(authentication: Authentication,
                      model: Model,
                      @PathVariable id: Long,
                      @RequestParam executionContext: ExecutionContext,
                      @RequestParam studentsProvideExplanation: Boolean?,
                      @RequestParam responseToEvaluateCount: Int?): String {
        val user: User = authentication.principal as User
        var assignment: Assignment? = null

        sequenceService.get(user, id, true)
                .let {
                    sequenceService.start(
                            user,
                            it,
                            executionContext,
                            studentsProvideExplanation ?: true,
                            responseToEvaluateCount ?: 0
                    )
                    autoReloadSessionHandler.broadcastReload(id)
                    assignment = it.assignment!!
                }

        return "redirect:/player/assignment/${assignment!!.id}/play/sequence/${id}"
    }

    @GetMapping("/interaction/{id}/start")
    fun startInteraction(authentication: Authentication,
                         model: Model,
                         @PathVariable id: Long): String {
        val user: User = authentication.principal as User
        val interaction = interactionService.start(user, id)
        autoReloadSessionHandler.broadcastReload(interaction.sequence.id!!)
        return "redirect:/player/assignment/${interaction.sequence.assignment!!.id}/play/sequence/${interaction.sequence.id}"
    }

    @GetMapping("/interaction/{id}/startNext")
    fun startNextInteraction(authentication: Authentication,
                             model: Model,
                             @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        interactionService.findById(id).let {
            sequenceService.loadInteractions(it.sequence)
            val interaction = interactionService.startNext(user, it)
            autoReloadSessionHandler.broadcastReload(interaction.sequence.id!!)
            return "redirect:/player/assignment/${interaction.sequence.assignment!!.id}/play/sequence/${interaction.sequence.id}"
        }
    }

    @GetMapping("/interaction/{id}/stop")
    fun stopInteraction(authentication: Authentication,
                        model: Model,
                        @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        interactionService.findById(id).let {
            sequenceService.loadInteractions(it.sequence)
            interactionService.stop(user, id)
            autoReloadSessionHandler.broadcastReload(it.sequence.id!!)
            return "redirect:/player/assignment/${it.sequence.assignment!!.id}/play/sequence/${it.sequence.id}"
        }
    }

    @GetMapping("/sequence/{id}/stop")
    fun stopSequence(authentication: Authentication,
                     model: Model,
                     @PathVariable id: Long): String {
        val user: User = authentication.principal as User
        var assignment: Assignment? = null

        sequenceService.get(user, id).let {
            sequenceService.stop(user, it)
            autoReloadSessionHandler.broadcastReload(id)
            assignment = it.assignment!!
        }

        return "redirect:/player/assignment/${assignment!!.id}/play/sequence/${id}"
    }

    @GetMapping("/sequence/{id}/reopen")
    fun reopenSequence(authentication: Authentication,
                       model: Model,
                       @PathVariable id: Long): String {
        val user: User = authentication.principal as User
        var assignment: Assignment? = null

        sequenceService.get(user, id).let {
            sequenceService.reopen(user, it)
            autoReloadSessionHandler.broadcastReload(id)
            assignment = it.assignment!!
        }

        return "redirect:/player/assignment/${assignment!!.id}/play/sequence/${id}"
    }

    @GetMapping("/sequence/{id}/publish-results")
    fun publishResults(authentication: Authentication,
                       model: Model,
                       @PathVariable id: Long): String {
        val user: User = authentication.principal as User
        var assignment: Assignment? = null

        sequenceService.get(user, id, true).let {
            sequenceService.publishResults(user, it)
            autoReloadSessionHandler.broadcastReload(id)
            assignment = it.assignment!!
        }

        return "redirect:/player/assignment/${assignment!!.id}/play/sequence/${id}"
    }

    @GetMapping("/sequence/{id}/refresh-results")
    fun refreshResults(authentication: Authentication,
                       model: Model,
                       @PathVariable id: Long): String {
        val user: User = authentication.principal as User
        var assignment: Assignment? = null

        sequenceService.get(id, true).let {
            sequenceService.refreshResults(user, it)
            assignment = it.assignment!!
        }

        return "redirect:/player/assignment/${assignment!!.id}/play/sequence/${id}"
    }

    @GetMapping("/sequence/{id}/unpublish-results")
    fun unpublishResults(authentication: Authentication,
                         model: Model,
                         @PathVariable id: Long): String {
        val user: User = authentication.principal as User
        var assignment: Assignment? = null

        sequenceService.get(user, id, true).let {
            sequenceService.unpublishResults(user, it)
            autoReloadSessionHandler.broadcastReload(id)
            assignment = it.assignment!!
        }

        return "redirect:/player/assignment/${assignment!!.id}/play/sequence/${id}"
    }

    @PostMapping("/sequence/{id}/submit-response")
    fun submitResponse(authentication: Authentication,
                       model: Model,
                       @ModelAttribute responseSubmissionData: ResponseSubmissionData,
                       @PathVariable id: Long): String {
        val user: User = authentication.principal as User
        var assignment: Assignment? = null

        sequenceService.get(id, true).let { sequence ->
            assignment = sequence.assignment!!
            val choiceListSpecification = responseSubmissionData.choiceList?.let {
                LearnerChoice(it)
            }

            val userActiveInteraction = sequenceService.getActiveInteractionForLearner(sequence, user)

            responseService.save(
                    userActiveInteraction
                            ?: error("No active interaction, cannot submit a response"), // TODO we should provide a user-friendly error page for this
                    Response(
                            learner = user,
                            interaction = sequence.getResponseSubmissionInteraction(),
                            attempt = responseSubmissionData.attempt,
                            confidenceDegree = responseSubmissionData.confidenceDegree,
                            explanation = responseSubmissionData.explanation,  // TODO Sanitize
                            learnerChoice = choiceListSpecification,
                            score = choiceListSpecification?.let {
                                Response.computeScore(
                                        it,
                                        sequence.statement.choiceSpecification
                                                ?: error("The choice specification is undefined")
                                )
                            },
                            statement = sequence.statement
                    )
            )
            if (sequence.executionIsDistance() || sequence.executionIsBlended()) {
                sequenceService.nextInteractionForLearner(sequence, user)
            }
        }

        return "redirect:/player/assignment/${assignment!!.id}/play/sequence/${id}"
    }

    data class ResponseSubmissionData(
            val interactionId: Long,
            val attempt: AttemptNum,
            val choiceList: List<Int>?,
            val confidenceDegree: ConfidenceDegree?,
            val explanation: String?
    )

    @PostMapping("/sequence/{id}/submit-evaluation-and-second-attempt")
    fun submitEvaluationAndSecondAttempt(authentication: Authentication,
                                         model: Model,
                                         @ModelAttribute evaluationData: EvaluationData,
                                         @PathVariable id: Long): String {
        val user: User = authentication.principal as User
        var assignment: Assignment? = null

        sequenceService.get(id, true).let { sequence ->
            assignment = sequence.assignment!!
            evaluationData.getGrades().forEach {
                peerGradingService.createOrUpdate(user, responseService.getOne(it.key), it.value.toBigDecimal())
            }

            if (sequence.isSecondAttemptAllowed()
                    && !responseService.hasResponseForUser(user, sequence, 2)) {
                val choiceListSpecification = evaluationData.choiceList?.let {
                    LearnerChoice(it)
                }

                Response(
                        learner = user,
                        interaction = sequence.getResponseSubmissionInteraction(),
                        attempt = 2,
                        confidenceDegree = evaluationData.confidenceDegree,
                        explanation = evaluationData.explanation,
                        learnerChoice = choiceListSpecification,
                        score = choiceListSpecification?.let {
                            Response.computeScore(
                                    it,
                                    sequence.statement.choiceSpecification
                                            ?: error("The choice specification is undefined")
                            )
                        },
                        statement = sequence.statement

                )
                        .let {
                            val userActiveInteraction = sequenceService.getActiveInteractionForLearner(sequence, user)
                                    ?: error("No active interaction, cannot submit a response") // TODO we should provide a user-friendly error page for this

                            responseService.save(
                                    userActiveInteraction,
                                    it
                            )
                        }


            }

            if (sequence.executionIsDistance() || sequence.executionIsBlended()) {
                sequenceService.nextInteractionForLearner(sequence, user)
            }

            return "redirect:/player/assignment/${assignment!!.id}/play/sequence/${id}"
        }
    }

    class EvaluationData(
            val id: Long,
            val choiceList: List<ItemIndex>?,
            val confidenceDegree: ConfidenceDegree?,
            val explanation: String?
    ) {
        private var grades = HashMap<Long, Int>()

        fun getGrades() = grades

        fun setGrades(value: HashMap<Long, Int>) {
            grades = value
        }
    }
}
