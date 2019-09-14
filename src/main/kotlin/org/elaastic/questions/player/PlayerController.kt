package org.elaastic.questions.player

import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.InteractionResponse
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


@Controller
@RequestMapping("/player")
class PlayerController(
        @Autowired val assignmentService: AssignmentService,
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
            return "redirect:/player/${it.id}/playFirstSequence"
        }
    }

    @GetMapping(value = ["/assignment/{id}/show", "/assignment/{id}"])
    fun show(authentication: Authentication,
             model: Model,
             @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        assignmentService.get(user, id, true).let {
            model.addAttribute("user", user)
            model.addAttribute("assignment", it)
            model.addAttribute(
                    "nbRegisteredUsers",
                    assignmentService.getNbRegisteredUsers(it)
            )
            model.addAttribute(
                    "userRole",
                    if (user == it.owner) "teacher" else "learner" // TODO Define a type for this
            )
        }

        return "/player/assignment/show"
    }

    @GetMapping("/assignment/{id}/nbRegisteredUsers")
    @ResponseBody
    fun getNbRegisteredUsers(@PathVariable id: Long): Int {
        return assignmentService.getNbRegisteredUsers(id)
    }

    @GetMapping("/test/steps")
    fun testSteps(authentication: Authentication,
                  model: Model,
                  @RequestParam responseSubmissionState: String?,
                  @RequestParam evaluationState: String?,
                  @RequestParam readState: String?,
                  @RequestParam showStatistics: Boolean?,
                  @RequestParam studentsProvideExplanation: Boolean?): String {
        val user: User = authentication.principal as User

        model.addAttribute("user", user)
        model.addAttribute("responseSubmissionState", responseSubmissionState ?: "completed")
        model.addAttribute("evaluationState", evaluationState ?: "active")
        model.addAttribute("readState", readState ?: "disabled")
        model.addAttribute("showStatistics", showStatistics ?: false)
        model.addAttribute("sequenceStatistics", SequenceStatistics())
        model.addAttribute("studentsProvideExplanation", studentsProvideExplanation ?: true)


        return "/player/assignment/sequence/components/test-steps"
    }

    @GetMapping("/test/explanation-list")
    fun testExplanationList(authentication: Authentication,
                            model: Model): String {
        val user: User = authentication.principal as User

        model.addAttribute("user", user)
        model.addAttribute(
                "responses",
                listOf(
                        InteractionResponseInfo(),
                        InteractionResponseInfo(),
                        InteractionResponseInfo()
                )
        )
        model.addAttribute("displaysAll", true)
        model.addAttribute("explanationCount", 17)

        return "/player/assignment/sequence/components/test-explanation-list"
    }

    class SequenceStatistics(
            val nbResponsesAttempt1: Int = 10,
            val nbResponsesAttempt2: Int = 8,
            val nbEvaluations: Int = 5
    )

    class InteractionResponseInfo(val explanation: String? = null)
}