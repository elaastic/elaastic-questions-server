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
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.pagination.PaginationUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.lang.IllegalArgumentException
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
        @Autowired val messageBuilder: MessageBuilder
) {

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

    @GetMapping("/assignment/{id}/play")
    fun playAssignment(authentication: Authentication,
                       model: Model,
                       @PathVariable id: Long): String {

        assignmentService.get(id, true).let { assignment ->

            if (assignment.sequences.isEmpty()) {
                throw IllegalStateException("Assignment $id has no sequences")
            }

            return "redirect:/player/sequence/${assignment.sequences.first().id}/play"
        }
    }

    @GetMapping("/sequence/{id}/play")
    fun playSequence(authentication: Authentication,
                     model: Model,
                     @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        // TODO Improve data fetching (should start from the assignment)

        sequenceService.get(id, true).let { sequence ->
            model.addAttribute("user", user)
            val teacher = user == sequence.owner

            model.addAttribute(
                    "playerModel",
                    PlayerModelFactory.build(
                            user = user,
                            teacher = teacher,
                            sequence = sequence,
                            nbRegisteredUsers = assignmentService.getNbRegisteredUsers(sequence.assignment!!),
                            sequenceToUserActiveInteraction =
                            if (teacher)
                                sequence.assignment!!.sequences.associate { it to it.activeInteraction }
                            else sequence.assignment!!.sequences.associate {
                                it to learnerSequenceService.findOrCreateLearnerSequence(user, it).activeInteraction
                            },
                            messageBuilder = messageBuilder,
                            getLearnerSequence = {
                                learnerSequenceService.findOrCreateLearnerSequence(user, sequence)
                            },
                            hasResponseForUser = {
                                responseService.hasResponseForUser(user, sequence)
                            },
                            findAllResponses = {
                                responseService.findAll(sequence)
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

        sequenceService.get(user, id, true)
                .let {
                    sequenceService.start(
                            user,
                            it,
                            executionContext,
                            studentsProvideExplanation ?: true,
                            responseToEvaluateCount ?: 0
                    )
                }

        return "redirect:/player/sequence/${id}/play"
    }

    @GetMapping("/interaction/{id}/start")
    fun startInteraction(authentication: Authentication,
                         model: Model,
                         @PathVariable id: Long): String {
        val user: User = authentication.principal as User
        val interaction = interactionService.start(user, id)
        return "redirect:/player/sequence/${interaction.sequence.id}/play"
    }

    @GetMapping("/interaction/{id}/startNext")
    fun startNextInteraction(authentication: Authentication,
                             model: Model,
                             @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        interactionService.findById(id).let {
            sequenceService.loadInteractions(it.sequence)
            val interaction = interactionService.startNext(user, it)
            return "redirect:/player/sequence/${interaction.sequence.id}/play"
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
            return "redirect:/player/sequence/${it.sequence.id}/play"
        }
    }

    @GetMapping("/sequence/{id}/stop")
    fun stopSequence(authentication: Authentication,
                     model: Model,
                     @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        sequenceService.get(user, id).let {
            sequenceService.stop(user, it)
        }

        return "redirect:/player/sequence/${id}/play"
    }

    @GetMapping("/sequence/{id}/reopen")
    fun reopenSequence(authentication: Authentication,
                       model: Model,
                       @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        sequenceService.get(user, id).let {
            sequenceService.reopen(user, it)
        }

        return "redirect:/player/sequence/${id}/play"
    }

    @GetMapping("/sequence/{id}/publish-results")
    fun publishResults(authentication: Authentication,
                       model: Model,
                       @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        sequenceService.get(user, id, true).let {
            sequenceService.publishResults(user, it)
        }

        return "redirect:/player/sequence/${id}/play"
    }

    @GetMapping("/sequence/{id}/unpublish-results")
    fun unpublishResults(authentication: Authentication,
                         model: Model,
                         @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        sequenceService.get(user, id, true).let {
            sequenceService.unpublishResults(user, it)
        }

        return "redirect:/player/sequence/${id}/play"
    }

    @PostMapping("/sequence/{id}/submit-response")
    fun submitResponse(authentication: Authentication,
                       model: Model,
                       @ModelAttribute responseSubmissionData: ResponseSubmissionData,
                       @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        sequenceService.get(id, true).let { sequence ->
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
                            confidenceDegree = responseSubmissionData.confidenceDegree?.ordinal,
                            explanation = responseSubmissionData.explanation,  // TODO Sanitize
                            learnerChoice = choiceListSpecification,
                            score = choiceListSpecification?.let {
                                Response.computeScore(
                                        it,
                                        sequence.statement.choiceSpecification
                                                ?: error("The choice specification is undefined")
                                )
                            }
                    )
            )
            if (sequence.executionIsDistance() || sequence.executionIsBlended()) {
                sequenceService.nextInteractionForLearner(sequence, user)
            }
        }

        return "redirect:/player/sequence/${id}/play"
    }

    data class ResponseSubmissionData(
            val interactionId: Long,
            val attempt: AttemptNum,
            val choiceList: List<Int>?,
            val confidenceDegree: ConfidenceDegree?,
            val explanation: String?
    )
}
