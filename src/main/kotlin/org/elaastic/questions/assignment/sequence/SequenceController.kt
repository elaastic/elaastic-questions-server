package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.Statement
import org.elaastic.questions.assignment.choice.*
import org.elaastic.questions.assignment.sequence.explanation.FakeExplanation
import org.elaastic.questions.assignment.sequence.explanation.FakeExplanationService
import org.elaastic.questions.attachment.*
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.lang.IllegalStateException
import javax.servlet.http.HttpServletResponse
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import kotlin.math.roundToInt

@Controller
@RequestMapping("/assignment/{assignmentId}/sequence")
@Transactional
class SequenceController(
        @Autowired val assignmentService: AssignmentService,
        @Autowired val sequenceService: SequenceService,
        @Autowired val statementService: StatementService,
        @Autowired val fakeExplanationService: FakeExplanationService,
        @Autowired val attachmentService: AttachmentService,
        @Autowired val messageBuilder: MessageBuilder
) {

    @GetMapping("{id}/edit")
    fun edit(authentication: Authentication,
             model: Model,
             @PathVariable assignmentId: Long,
             @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        val sequence = sequenceService.get(user, id)

        model.addAttribute("user", user)
        model.addAttribute("assignment", sequence.assignment)
        model.addAttribute("sequenceData", SequenceData(sequence))
        model.addAttribute(
                "statementData",
                StatementData(
                        sequence.statement,
                        fakeExplanationService.findAllByStatement(sequence.statement)
                )
        )

        return "/assignment/sequence/edit"
    }

    @PostMapping("save")
    fun save(authentication: Authentication,
             @RequestParam("fileToAttached") fileToAttached: MultipartFile,
             @Valid @ModelAttribute statementData: StatementData,
             result: BindingResult,
             model: Model,
             @PathVariable assignmentId: Long,
             response: HttpServletResponse): String {
        val user: User = authentication.principal as User

        val assignment = assignmentService.get(user, assignmentId, fetchSequences = true)

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
            statementService.updateFakeExplanationList(
                    sequence.statement,
                    statementData.fakeExplanations
            )
            attachedFileIfAny(fileToAttached, sequence.statement)
            return "redirect:/assignment/$assignmentId#sequence_${sequence.id}"
        }
    }

    @PostMapping("{id}/update")
    fun update(authentication: Authentication,
               @RequestParam("fileToAttached") fileToAttached: MultipartFile,
               @Valid @ModelAttribute statementData: StatementData,
               result: BindingResult,
               model: Model,
               @PathVariable assignmentId: Long,
               @PathVariable id: Long,
               response: HttpServletResponse,
               redirectAttributes: RedirectAttributes): String {
        val user: User = authentication.principal as User

        val sequence = sequenceService.get(user, id)

        return if (result.hasErrors()) {
            response.status = HttpStatus.BAD_REQUEST.value()

            model.addAttribute("user", user)
            model.addAttribute("assignment", sequence.assignment)
            model.addAttribute("sequenceData", SequenceData(sequence))
            model.addAttribute("statementData", statementData)

            "/assignment/sequence/edit"
        } else {
            sequence.statement.let {
                it.updateFrom(statementData.toEntity(user))
                statementService.save(it)
                statementService.updateFakeExplanationList(it, statementData.fakeExplanations)
                attachedFileIfAny(fileToAttached, it)
            }

            with(messageBuilder) {
                success(
                        redirectAttributes,
                        message(
                                "sequence.updated.message.variant",
                                sequence.statement.title
                        )
                )
            }

            return if (statementData.returnOnSubject) {
                "redirect:/assignment/$assignmentId"
            } else {
                "redirect:/assignment/$assignmentId/sequence/$id/edit"
            }
        }
    }

    private fun attachedFileIfAny(fileToAttached: MultipartFile, it: Statement) {
        if (!fileToAttached.isEmpty) {
            attachmentService.saveStatementAttachment(
                    it,
                    createAttachment(fileToAttached),
                    fileToAttached.inputStream)
        }
    }

    @GetMapping("{id}/delete")
    fun delete(authentication: Authentication,
               @PathVariable assignmentId: Long,
               @PathVariable id: Long,
               redirectAttributes: RedirectAttributes): String {
        val user: User = authentication.principal as User

        val sequence = sequenceService.get(user, id)
        assignmentService.removeSequence(sequence)

        with(messageBuilder) {
            success(
                    redirectAttributes,
                    message(
                            "sequence.deleted.message",
                            message("sequence.label"),
                            sequence.statement.title
                    )
            )
        }

        return "redirect:/assignment/$assignmentId"
    }

    @GetMapping("{id}/up")
    fun up(authentication: Authentication,
           @PathVariable assignmentId: Long,
           @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        val assignment = assignmentService.get(user, assignmentId, true)
        assignmentService.moveUpSequence(assignment, id)

        return "redirect:/assignment/$assignmentId#sequence_${id}"
    }

    @GetMapping("{id}/down")
    fun down(authentication: Authentication,
             @PathVariable assignmentId: Long,
             @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        val assignment = assignmentService.get(user, assignmentId, true)
        assignmentService.moveDownSequence(assignment, id)

        return "redirect:/assignment/$assignmentId#sequence_${id}"
    }

    companion object {

        fun createAttachment(file: MultipartFile): Attachment {
            return Attachment(
                    name = file.name,
                    size = file.size,
                    originalFileName = file.originalFilename,
                    mimeType = if (file.contentType.isNullOrBlank()) MimeType() else MimeType(file.contentType!!)
            )
        }

    }

    data class SequenceData(
            var id: Long? = null,
            var rank: Int = 0
    ) {
        constructor(sequence: Sequence) : this(
                id = sequence.id,
                rank = sequence.rank
        )
    }

    // Note JT : would be nicer if we receive only the relevant data depending on QuestionType
    // Moreover, the gap between the view model and the entities representation is too important
    // The code below could be widely simplified by refactoring the view to use the same model as the backend
    data class StatementData(
            var id: Long? = null,
            var version: Long? = null,
            @field:NotBlank val title: String? = null,
            @field:NotBlank val content: String? = null,
            val attachment: Attachment? = null,
            val hasChoices: Boolean = true,
            val choiceInteractionType: ChoiceType? = null,
            @field:NotNull @field:Max(10) val itemCount: Int? = null,
            @field:NotNull var exclusiveChoice: Int = 1,
            @field:NotNull var expectedChoiceList: List<Int> = listOf(1),
            var returnOnSubject: Boolean = false,
            var expectedExplanation: String?,
            var fakeExplanations: List<FakeExplanationData> = ArrayList()
    ) {
        constructor(statement: Statement) : this(
                id = statement.id,
                version = statement.version,
                title = statement.title,
                content = statement.content,
                hasChoices = statement.questionType != QuestionType.OpenEnded,
                choiceInteractionType = statement.choiceSpecification?.getChoiceType(),
                itemCount = statement.choiceSpecification?.nbCandidateItem ?: 2,
                expectedChoiceList = listOf(1),
                exclusiveChoice = 1,
                expectedExplanation = statement.expectedExplanation,
                fakeExplanations = ArrayList(),
                attachment = statement.attachment

        ) {
            val choiceSpecification: ChoiceSpecification? = statement.choiceSpecification
            when (choiceSpecification) {
                is ExclusiveChoiceSpecification -> exclusiveChoice = choiceSpecification.expectedChoice.index
                is MultipleChoiceSpecification -> expectedChoiceList = choiceSpecification.expectedChoiceList.map { it.index }
            }
        }

        constructor(statement: Statement, fakeExplanations: List<FakeExplanation>) : this(statement) {
            this.fakeExplanations = fakeExplanations.map { FakeExplanationData(it) }
        }

        fun toEntity(user: User): Statement {
            val questionType: QuestionType =
                    if (hasChoices) {
                        when (choiceInteractionType) {
                            ChoiceType.EXCLUSIVE -> QuestionType.ExclusiveChoice
                            ChoiceType.MULTIPLE -> QuestionType.MultipleChoice
                            null -> throw IllegalStateException("No choiceInteractionType defined")
                        }
                    } else QuestionType.OpenEnded

            val choiceSpecification: ChoiceSpecification? =
                    if (hasChoices) {
                        when (choiceInteractionType) {
                            null -> null

                            ChoiceType.MULTIPLE -> MultipleChoiceSpecification(
                                    itemCount!!,
                                    expectedChoiceList.map { n ->
                                        ChoiceItem(n, 100f / expectedChoiceList.size)
                                    },
                                    listOf()
                            )

                            ChoiceType.EXCLUSIVE -> ExclusiveChoiceSpecification(
                                    itemCount!!,
                                    ChoiceItem(exclusiveChoice, 100f)
                            )
                        }
                    } else null


            val statement = Statement(
                    owner = user,
                    title = title!!,
                    content = content!!,
                    questionType = questionType,
                    choiceSpecification = choiceSpecification,
                    expectedExplanation = expectedExplanation
            )
            statement.id = id
            statement.version = version

            return statement
        }
    }
}
