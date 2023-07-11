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
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.LearnerSequenceService
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.assignment.sequence.interaction.InteractionService
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.interaction.results.AttemptNum
import org.elaastic.questions.controller.ControllerUtil
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.course.Course
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.*
import org.elaastic.questions.player.components.results.TeacherResultDashboardService
import org.elaastic.questions.player.phase.LearnerPhaseService
import org.elaastic.questions.player.websocket.AutoReloadSessionHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.togglz.core.manager.FeatureManager
import java.lang.IllegalArgumentException
import java.util.*
import javax.persistence.EntityNotFoundException
import javax.servlet.http.HttpServletRequest
import javax.transaction.Transactional

@Controller
@RequestMapping("/player")
class PlayerController(
    @Autowired val assignmentService: AssignmentService,
    @Autowired val sequenceService: SequenceService,
    @Autowired val learnerSequenceService: LearnerSequenceService,
    @Autowired val interactionService: InteractionService,
    @Autowired val responseService: ResponseService,
    @Autowired val messageBuilder: MessageBuilder,
    @Autowired val anonymousUserService: AnonymousUserService,
    @Autowired val learnerPhaseService: LearnerPhaseService,
    @Autowired val userService: UserService,
    @Autowired val featureManager: FeatureManager,
    @Autowired val teacherResultDashboardService: TeacherResultDashboardService,
) {

    private val autoReloadSessionHandler = AutoReloadSessionHandler

    @GetMapping(value = ["", "/", "/index"])
    fun index(
        authentication: Authentication,
        model: Model
    ): String {
        val user: User = authentication.principal as User

        // TODO N+1 SELECT (Assignment => Course)
        val assignments: List<Assignment> = assignmentService.findAllAssignmentsForLearner(user)
        val mapCourseAssignments: Map<Course, MutableList<Assignment>> =
            assignmentService.getCoursesAssignmentsMap(assignments)
        val assignmentsWithoutCourse: List<Assignment> =
            assignments.filter { assignment -> assignment.subject?.course == null }
        model.addAttribute("user", user)
        model.addAttribute("mapCourseAssignments", mapCourseAssignments)
        model.addAttribute("assignmentsWithoutCourse", assignmentsWithoutCourse)

        return "player/index"
    }

    private fun findAssignment(globalId: String?): Assignment {
        if (globalId == null || globalId == "") {
            throw IllegalArgumentException(
                messageBuilder.message("assignment.register.empty.globalId")
            )
        }

        return assignmentService.findByGlobalId(
            UUID.fromString(globalId)) ?: throw EntityNotFoundException(
            messageBuilder.message("assignment.globalId.does.not.exist")
        )
    }

    @GetMapping("/register")
    fun register(
        authentication: Authentication?,
        model: Model,
        @RequestParam("globalId") globalId: String?
    ): ModelAndView {
        findAssignment(globalId).let {

            return if (authentication == null && it.acceptAnonymousUsers) {
                model.addAttribute("globalId", globalId)
                model.addAttribute("assignmentTitle", it.title)
                ModelAndView("/player/assignment/register")
            } else {
                ModelAndView(
                    "redirect:/player/authenticated-register",
                    mapOf("globalId" to globalId)
                )
            }
        }
    }

    /**
     * Register action for authenticated users (this URL is
     * not accessible to anonymous users)
     */
    @GetMapping("/authenticated-register")
    fun authenticatedOnlyRegister(
        authentication: Authentication,
        @RequestParam("globalId") globalId: String
    ): String {
        return doRegister(
            authentication.principal as User,
            findAssignment(globalId)
        )
    }

    /**
     * Start an anonymous session for a student :
     * - the user is only identified by a nickname
     * - a user entity will be created (with isAnonymous flag)
     * - the user won't be able to log anymore for this user ; it may just uses the service during the session lifetime
     * - the session start by registering the on the assignment identified by globalId
     */
    @GetMapping("/start-anonymous-session")
    @Transactional
    fun startAnonymousSession(
        authentication: Authentication?,
        @RequestParam("nickname") nickname: String,
        @RequestParam("globalId") globalId: String,
        redirectAttributes: RedirectAttributes
    ): String {
        authentication == null ||
                throw IllegalStateException("You cannot start an anonymous session while being authenticated")

        if (nickname == "") {
            redirectAttributes.addAttribute("globalId", globalId)
            with(messageBuilder) {
                error(redirectAttributes, message("nickname.mandatory"))
            }

            return "redirect:/player/register"
        }

        val anonymousAuthentication =
            anonymousUserService.authenticateAnonymousUser(nickname)
        SecurityContextHolder.getContext().authentication = anonymousAuthentication

        return doRegister(
            anonymousAuthentication.principal as User,
            findAssignment(globalId)
        )
    }

    private fun doRegister(user: User, assignment: Assignment): String {
        assignmentService.registerUser(user, assignment)
        return "redirect:/player/assignment/${assignment.id}/play"
    }

    @GetMapping(
        "/assignment/{assignmentId}/play/sequence/{sequenceId}",
        "/assignment/{assignmentId}/play",
        "/assignment/{assignmentId}/{sequenceId}"
    )
    fun playAssignment(
        authentication: Authentication,
        httpServletRequest: HttpServletRequest,
        model: Model,
        @PathVariable assignmentId: Long,
        @PathVariable sequenceId: Long?
    ): String {

        val user: User = authentication.principal as User
        val assignment: Assignment = assignmentService.get(assignmentId, true)
        model.addAttribute("user", user)

        if (assignment.sequences.isEmpty()) {
            return "redirect:/subject/${assignment.subject!!.id}"
        }

        val sequenceIdValid: Long = sequenceId ?: assignment.sequences.first().id!!

        // TODO Improve data fetching (should start from the assignment)
        sequenceService.get(sequenceIdValid, true).let { sequence ->
            model.addAttribute("user", user)
            val teacher = user == sequence.owner

            return if (teacher)
                playAssignmentForTeacher(user, model, sequence, httpServletRequest)
            else playAssignmentForLearner(user, model, sequence)
        }

    }


    private fun playAssignmentForTeacher(
        user: User,
        model: Model,
        sequence: Sequence,
        httpServletRequest: HttpServletRequest,
    ): String {

        val assignment = sequence.assignment!!
        val nbRegisteredUsers = assignmentService.getNbRegisteredUsers(assignment)

        model.addAttribute(
            "playerModel",
            PlayerModelFactory.buildForTeacher(
                user = user,
                sequence = sequence,
                serverBaseUrl = ControllerUtil.getServerBaseUrl(httpServletRequest),
                nbRegisteredUsers = nbRegisteredUsers,
                sequenceToUserActiveInteraction = assignment.sequences.associateWith { it.activeInteraction },
                messageBuilder = messageBuilder,
                sequenceStatistics = sequenceService.getStatistics(sequence),
                teacherResultDashboardService = teacherResultDashboardService,
            )
        )

        return "player/assignment/sequence/play-teacher"
    }

    private fun playAssignmentForLearner(
        user: User,
        model: Model,
        sequence: Sequence
    ): String {

        val assignment = sequence.assignment!!
        val nbRegisteredUsers = assignmentService.getNbRegisteredUsers(assignment)

        val learnerSequence = learnerSequenceService.getLearnerSequence(user, sequence)
        learnerPhaseService.loadPhaseList(learnerSequence)

        model.addAttribute(
            "playerModel",
            PlayerModelFactory.buildForLearner(
                sequence = sequence,
                nbRegisteredUsers = nbRegisteredUsers,
                sequenceToUserActiveInteraction = assignment.sequences.associateWith {
                    if (it.executionIsFaceToFace())
                        it.activeInteraction
                    else learnerSequenceService.findOrCreateLearnerSequence(user, it).activeInteraction
                },
                messageBuilder = messageBuilder,
                activeInteraction = learnerSequenceService.getActiveInteractionForLearner(
                    user,
                    sequence
                ),
                learnerSequence = learnerSequence
            )
        )

        return "player/assignment/sequence/play-learner"
    }

    @GetMapping("/assignment/{id}/nbRegisteredUsers")
    @ResponseBody
    fun getNbRegisteredUsers(@PathVariable id: Long): Int {
        return assignmentService.getNbRegisteredUsers(id)
    }


    @GetMapping("/sequence/{id}/start")
    fun startSequence(
        authentication: Authentication,
        model: Model,
        @PathVariable id: Long,
        @RequestParam executionContext: ExecutionContext,
        @RequestParam studentsProvideExplanation: Boolean?,
        @RequestParam responseToEvaluateCount: Int?
    ): String {
        val user: User = authentication.principal as User
        var assignment: Assignment?

        sequenceService.get(user, id, true)
            .let {
                sequenceService.start(
                    user,
                    it,
                    executionContext,
                    studentsProvideExplanation ?: false,
                    responseToEvaluateCount ?: 0
                )
                userService.updateUserActiveSince(user)
                autoReloadSessionHandler.broadcastReload(id)
                assignment = it.assignment!!
            }

        return "redirect:/player/assignment/${assignment!!.id}/play/sequence/${id}"
    }

    @GetMapping("/interaction/{id}/restart")
    fun restartInteraction(
        authentication: Authentication,
        model: Model,
        @PathVariable id: Long
    ): String {
        val user: User = authentication.principal as User
        val interaction = interactionService.restart(user, id)
        autoReloadSessionHandler.broadcastReload(interaction.sequence.id!!)
        return "redirect:/player/assignment/${interaction.sequence.assignment!!.id}/play/sequence/${interaction.sequence.id}"
    }

    @GetMapping("/interaction/{id}/start")
    fun startInteraction(
        authentication: Authentication,
        model: Model,
        @PathVariable id: Long
    ): String {
        val user: User = authentication.principal as User
        val interaction = interactionService.start(user, id)
        autoReloadSessionHandler.broadcastReload(interaction.sequence.id!!)
        return "redirect:/player/assignment/${interaction.sequence.assignment!!.id}/play/sequence/${interaction.sequence.id}"
    }

    @GetMapping("/interaction/{id}/startNext")
    fun startNextInteraction(
        authentication: Authentication,
        model: Model,
        @PathVariable id: Long
    ): String {
        val user: User = authentication.principal as User

        interactionService.findById(id).let {
            sequenceService.loadInteractions(it.sequence)
            val interaction = interactionService.startNext(user, it)
            autoReloadSessionHandler.broadcastReload(interaction.sequence.id!!)
            return "redirect:/player/assignment/${interaction.sequence.assignment!!.id}/play/sequence/${interaction.sequence.id}"
        }
    }

    @GetMapping("/interaction/{id}/skipNext")
    fun skipNextInteraction(
        authentication: Authentication,
        model: Model,
        @PathVariable id: Long
    ): String {
        val user: User = authentication.principal as User

        interactionService.findById(id).let {
            sequenceService.loadInteractions(it.sequence)
            val interaction = interactionService.skipNext(user, it)
            autoReloadSessionHandler.broadcastReload(interaction.sequence.id!!)
            return "redirect:/player/assignment/${interaction.sequence.assignment!!.id}/play/sequence/${interaction.sequence.id}"
        }
    }

    @GetMapping("/interaction/{id}/stop")
    fun stopInteraction(
        authentication: Authentication,
        model: Model,
        @PathVariable id: Long
    ): String {
        val user: User = authentication.principal as User

        interactionService.findById(id).let {
            sequenceService.loadInteractions(it.sequence)
            interactionService.stop(user, id)
            autoReloadSessionHandler.broadcastReload(it.sequence.id!!)
            return "redirect:/player/assignment/${it.sequence.assignment!!.id}/play/sequence/${it.sequence.id}"
        }
    }

    @GetMapping("/sequence/{id}/stop")
    fun stopSequence(
        authentication: Authentication,
        model: Model,
        @PathVariable id: Long
    ): String {
        val user: User = authentication.principal as User
        var assignment: Assignment?

        sequenceService.get(user, id).let {
            sequenceService.stop(user, it)
            autoReloadSessionHandler.broadcastReload(id)
            assignment = it.assignment!!
        }

        return "redirect:/player/assignment/${assignment!!.id}/play/sequence/${id}"
    }

    @GetMapping("/sequence/{id}/reopen")
    fun reopenSequence(
        authentication: Authentication,
        model: Model,
        @PathVariable id: Long
    ): String {
        val user: User = authentication.principal as User
        var assignment: Assignment?

        sequenceService.get(user, id).let {
            sequenceService.reopen(user, it)
            autoReloadSessionHandler.broadcastReload(id)
            assignment = it.assignment!!
        }

        return "redirect:/player/assignment/${assignment!!.id}/play/sequence/${id}"
    }

    @GetMapping("/sequence/{id}/publish-results")
    fun publishResults(
        authentication: Authentication,
        model: Model,
        @PathVariable id: Long
    ): String {
        val user: User = authentication.principal as User
        var assignment: Assignment?

        sequenceService.get(user, id, true).let {
            sequenceService.publishResults(user, it)
            autoReloadSessionHandler.broadcastReload(id)
            assignment = it.assignment!!
        }

        return "redirect:/player/assignment/${assignment!!.id}/play/sequence/${id}"
    }

    @GetMapping("/sequence/{id}/refresh-results")
    fun refreshResults(
        authentication: Authentication,
        model: Model,
        @PathVariable id: Long
    ): String {
        val user: User = authentication.principal as User
        var assignment: Assignment?

        sequenceService.get(id, true).let {
            sequenceService.refreshResults(user, it)
            assignment = it.assignment!!
        }

        return "redirect:/player/assignment/${assignment!!.id}/play/sequence/${id}"
    }

    @GetMapping("/sequence/{id}/unpublish-results")
    fun unpublishResults(
        authentication: Authentication,
        model: Model,
        @PathVariable id: Long
    ): String {
        val user: User = authentication.principal as User
        var assignment: Assignment?

        sequenceService.get(user, id, true).let {
            sequenceService.unpublishResults(user, it)
            autoReloadSessionHandler.broadcastReload(id)
            assignment = it.assignment!!
        }

        return "redirect:/player/assignment/${assignment!!.id}/play/sequence/${id}"
    }

    @PostMapping("/sequence/{id}/submit-response")
    fun submitResponse(
        authentication: Authentication,
        model: Model,
        @ModelAttribute responseSubmissionData: ResponseSubmissionData,
        @PathVariable id: Long
    ): String {
        val user: User = authentication.principal as User

        val sequence = sequenceService.get(id, true)
        sequenceService.submitResponse(user, sequence, responseSubmissionData)

        return "redirect:/player/assignment/${sequence.assignment!!.id}/play/sequence/${id}"
    }

    data class ResponseSubmissionData(
        val interactionId: Long,
        val attempt: AttemptNum,
        val choiceList: List<Int>?,
        val confidenceDegree: ConfidenceDegree?,
        val explanation: String?
    )
}
