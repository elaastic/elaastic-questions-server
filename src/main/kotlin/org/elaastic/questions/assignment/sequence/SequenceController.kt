package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.Statement
import org.elaastic.questions.assignment.choice.ChoiceInteractionType
import org.elaastic.questions.assignment.choice.ChoiceItemSpecification
import org.elaastic.questions.assignment.choice.ChoiceSpecification
import org.elaastic.questions.attachement.Attachment
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.lang.IllegalStateException
import javax.servlet.http.HttpServletResponse
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Controller
@RequestMapping("/assignment/{assignmentId}/sequence")
@Transactional
class SequenceController(
        @Autowired val assignmentService: AssignmentService,
        @Autowired val sequenceService: SequenceService
) {

    @GetMapping("{id}/edit")
    fun edit(authentication: Authentication,
             model: Model,
             @PathVariable assignmentId: Long,
             @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        val assignment = assignmentService.get(user, assignmentId)
        val sequence = sequenceService.get(user, id)

        model.addAttribute("user", user)
        model.addAttribute("assignment", assignment)
        model.addAttribute("sequenceData", SequenceData(sequence))

        return "/assignment/sequence/edit"
    }

    @PostMapping("save")
    fun save(authentication: Authentication,
             @Valid @ModelAttribute statementData: StatementData,
             result: BindingResult,
             model: Model,
             @PathVariable assignmentId: Long,
             response: HttpServletResponse): String {
        val user: User = authentication.principal as User

        val assignment = assignmentService.get(user, assignmentId)

        if (result.hasErrors()) {
            response.status = HttpStatus.BAD_REQUEST.value()
            model.addAttribute("user", user)
            model.addAttribute("assignment", assignment)
            model.addAttribute("sequence", statementData)
            model.addAttribute(
                    "nbSequence",
                    assignmentService.countAllSequence(assignment) // Note JT: Could be stored into the view instead of being queried each time...
            )
            return "/assignment/sequence/create"
        } else {
            val sequence = assignmentService.addSequence(
                    assignment,
                    statementData.toEntity(user)
            )

            return "redirect:/assignment/$assignmentId#sequence_${sequence.id}"
        }
    }

    data class SequenceData(
            var id: Long? = null,
            var rank: Int = 0,
            val statementData: StatementData
            ) {
        constructor(sequence: Sequence) : this(
                id = sequence.id,
                rank = sequence.rank,
                statementData = StatementData(sequence.statement)
        )
    }

    // Note JT : would be nicer if we receive only the relevant data depending on QuestionType
    data class StatementData(
            var id: Long? = null,
            @field:NotBlank val title: String? = null,
            @field:NotBlank val content: String? = null,
            val attachment: Attachment? = null,
            val hasChoices: Boolean = true,
            @field:NotNull val choiceInteractionType: ChoiceInteractionType? = null,
            @field:NotNull @field:Max(10) val itemCount: Int? = null,
            @field:NotNull var exclusiveChoice: Int = 1,
            @field:NotNull var expectedChoiceList: List<Int> = listOf(1)
    ) {
        constructor(statement: Statement) : this(
                id = statement.id,
                title = statement.title,
                content = statement.content,
                hasChoices = statement.questionType != QuestionType.OpenEnded,
                choiceInteractionType = statement.choiceSpecification?.choiceInteractionType,
                itemCount = statement.choiceSpecification?.itemCount ?: 2
        ) {
            when (choiceInteractionType) {
                ChoiceInteractionType.EXCLUSIVE ->
                    exclusiveChoice = statement.choiceSpecification?.getExpectedChoice()?.index ?: 1
                ChoiceInteractionType.MULTIPLE ->
                    expectedChoiceList = statement.choiceSpecification?.expectedChoiceList?.map { it.index }
                            ?: listOf(1)
            }
        }

        fun toEntity(user: User): Statement {
            val questionType: QuestionType =
                    if (hasChoices) {
                        when (choiceInteractionType) {
                            ChoiceInteractionType.EXCLUSIVE -> QuestionType.ExclusiveChoice
                            ChoiceInteractionType.MULTIPLE -> QuestionType.MultipleChoice
                            null -> throw IllegalStateException("No choiceInteractionType defined")
                        }
                    } else QuestionType.OpenEnded

            val choiceSpecification: ChoiceSpecification? =
                    if (hasChoices) {
                        ChoiceSpecification(
                                choiceInteractionType = choiceInteractionType!!,
                                itemCount = itemCount!!
                        ).let {
                            when (choiceInteractionType) {
                                ChoiceInteractionType.MULTIPLE -> it.expectedChoiceList =
                                        expectedChoiceList.map { n ->
                                            ChoiceItemSpecification(n, 100f / expectedChoiceList.size)
                                        }
                                ChoiceInteractionType.EXCLUSIVE -> it.setExpectedChoice(
                                        ChoiceItemSpecification(exclusiveChoice, 100f)
                                )
                            }
                            it
                        }
                    } else null




            return Statement(
                    owner = user,
                    title = title!!,
                    content = content!!,
                    questionType = questionType,
                    choiceSpecification = choiceSpecification
            )
        }
    }
}