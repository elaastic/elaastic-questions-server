package org.elaastic.questions.player

import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.pagination.PaginationUtil
import org.elaastic.questions.player.components.command.CommandModelFactory
import org.elaastic.questions.player.components.sequenceInfo.SequenceInfoResolver
import org.elaastic.questions.player.components.statement.StatementInfo
import org.elaastic.questions.player.components.statement.StatementPanelModel
import org.elaastic.questions.player.components.steps.StepsModelFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.math.BigDecimal
import java.math.RoundingMode
import javax.persistence.EntityNotFoundException


@Controller
@RequestMapping("/player")
class PlayerController(
        @Autowired val assignmentService: AssignmentService,
        @Autowired val sequenceService: SequenceService,
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
            model.addAttribute("stepsModel", StepsModelFactory.build())
        }

        return "/player/assignment/show"
    }

    @GetMapping("/sequence/{id}/play")
    fun playSequence(authentication: Authentication,
                     model: Model,
                     @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        sequenceService.get(user, id, true).let {
            model.addAttribute("user", user)
            model.addAttribute("assignment", it.assignment)
            model.addAttribute("sequence", it)
            model.addAttribute(
                    "userRole",
                    if (user == it.owner) "teacher" else "learner" // TODO Define a type for this
            )
            model.addAttribute("stepsModel", StepsModelFactory.build())
            model.addAttribute("commandModel", CommandModelFactory.build(user, it))
            model.addAttribute(
                    "sequenceInfoModel",
                    SequenceInfoResolver.resolve(it, messageBuilder)
            )
            model.addAttribute("statementPanelModel", StatementPanelModel())
            model.addAttribute("statement", StatementInfo(it.statement))
        }

        return "/player/assignment/sequence/play"
    }

    @GetMapping("/assignment/{id}/nbRegisteredUsers")
    @ResponseBody
    fun getNbRegisteredUsers(@PathVariable id: Long): Int {
        return assignmentService.getNbRegisteredUsers(id)
    }

    interface ExplanationViewerModel {
        val hasChoice: Boolean
        val explanationsExcerpt: List<ExplanationData>
        val hasMoreThanExcerpt: Boolean
        val nbExplanations: Int
    }

    class ChoiceExplanationViewerModel(
            explanationsByResponse: Map<ResponseData, List<ExplanationData>>,
            alreadySorted: Boolean = false
    ) : ExplanationViewerModel {
        override val hasChoice = true
        val explanationsByResponse =
                if (alreadySorted) explanationsByResponse
                else explanationsByResponse.mapValues {
                    it.value.sortedWith(
                            compareByDescending<ExplanationData> { it.meanGrade }.thenByDescending { it.nbEvaluations }
                    )
                }

        val correctResponse = this.explanationsByResponse.keys.find { it.correct }
                ?: throw IllegalStateException("There is no correct answer")
        val explanationsForCorrectResponse = this.explanationsByResponse.filter { it.key.correct }.values.flatten()
        val explanationsByIncorrectResponses = this.explanationsByResponse.filter { !it.key.correct }
        val hasExplanationsForIncorrectResponse = this.explanationsByResponse.any { !it.key.correct && !it.value.isEmpty() }
        val nbExplanationsForCorrectResponse = explanationsForCorrectResponse.count()
        override val explanationsExcerpt = explanationsForCorrectResponse.take(3)
        override val nbExplanations = this.explanationsByResponse.values.flatten().count()
        override val hasMoreThanExcerpt = nbExplanationsForCorrectResponse > 3 || hasExplanationsForIncorrectResponse
    }

    class OpenExplanationViewerModel(explanations: List<ExplanationData>, alreadySorted: Boolean = false) : ExplanationViewerModel {
        val explanations =
                if (alreadySorted) explanations
                else explanations.sortedWith(
                        compareByDescending<ExplanationData> { it.meanGrade }.thenByDescending { it.nbEvaluations }
                )
        override val hasChoice = false
        override val nbExplanations = this.explanations.count()
        override val explanationsExcerpt = this.explanations.take(3)
        val nbExplanationsForCorrectResponse = nbExplanations
        override val hasMoreThanExcerpt = nbExplanations > 3
    }

    class ResponseData(
            val choices: List<Int> = listOf(),
            val score: Int, // percents
            val correct: Boolean
    )

    class ExplanationData(
            val content: String? = null,
            val author: String? = null,
            val nbEvaluations: Int = 0,
            meanGrade: BigDecimal? = null
    ) {
        val meanGrade = meanGrade
                ?.setScale(2, RoundingMode.CEILING)
                ?.stripTrailingZeros()
    }
}