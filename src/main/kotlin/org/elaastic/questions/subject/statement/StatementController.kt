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

package org.elaastic.questions.subject.statement

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.subject.statement.Statement
import org.elaastic.questions.assignment.choice.*
import org.elaastic.questions.assignment.sequence.FakeExplanationData
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.SequenceController
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.assignment.sequence.explanation.FakeExplanation
import org.elaastic.questions.assignment.sequence.explanation.FakeExplanationService
import org.elaastic.questions.attachment.*
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.directory.User
import org.elaastic.questions.subject.Subject
import org.elaastic.questions.subject.SubjectService
import org.elaastic.questions.subject.statement.StatementService
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

@Controller
@RequestMapping("/subject/{subjectId}/statement")
@Transactional
class StatementController(
        @Autowired val assignmentService: AssignmentService,
        @Autowired val statementService: StatementService,
        @Autowired val fakeExplanationService: FakeExplanationService,
        @Autowired val messageBuilder: MessageBuilder,
        @Autowired val subjectService: SubjectService,
        @Autowired val attachmentService: AttachmentService
) {

    @GetMapping("{id}/edit")
    fun edit(authentication: Authentication,
             model: Model,
             @PathVariable id: Long): String {

        val user: User = authentication.principal as User
        val statementBase = statementService.get(user, id)
        val subject = statementBase.subject
        val fakeExplanations = fakeExplanationService.findAllByStatement(statementBase)
        val statement = SequenceController.StatementData(statementBase, fakeExplanations)

        model.addAttribute("user", user)
        model.addAttribute("subject", subject)
        model.addAttribute("statementData",statement)
        model.addAttribute("rank",statementBase.rank)

        return "/subject/statement/edit"
    }

    @PostMapping("{id}/update")
    fun update(authentication: Authentication,
               @RequestParam("fileToAttached") fileToAttached: MultipartFile,
               @Valid @ModelAttribute statementData: SequenceController.StatementData,
               result: BindingResult,
               model: Model,
               @PathVariable subjectId: Long,
               @PathVariable id: Long,
               response: HttpServletResponse,
               redirectAttributes: RedirectAttributes): String {

        val user: User = authentication.principal as User
        val statementBase = statementService.get(user, id)
        val subject = statementBase.subject
        var newStatement = statementBase

        return if (result.hasErrors()) {
            response.status = HttpStatus.BAD_REQUEST.value()
            model.addAttribute("user", user)
            model.addAttribute("subject", subject)
            model.addAttribute("statementData",statementData)
            model.addAttribute("rank",statementBase.rank)

            "/subject/statement/edit"
        } else {
            // if there is a sequence with results that uses the statement
            if (isStatementUsed(statementBase))
                newStatement = statementService.duplicate(statementBase)

            newStatement.let {
                newStatement.updateFrom(statementData.toEntity(user))
                statementService.save(it)
                statementService.updateFakeExplanationList(it,statementData.fakeExplanations)
                attachedFileIfAny(fileToAttached, it)
            }

            with(messageBuilder) {
                success(
                        redirectAttributes,
                        message(
                                "sequence.updated.message.variant",
                                statementBase.title
                        )
                )
            }

            if (isStatementUsed(statementBase)) {
                statementService.assignStatementToSequences(newStatement)
                subjectService.removeStatement(statementBase.owner, statementBase)
            }

            return if (statementData.returnOnSubject) {
                "redirect:/subject/$subjectId"
            } else {
                "redirect:/subject/$subjectId/statement/$id/edit"
            }
        }
    }

    private fun isStatementUsed(statement: Statement): Boolean{
        val subject: Subject = statement.subject!!
        var isUsed: Boolean = false
        for (assignment: Assignment in subject.assignments){
            for (sequence: Sequence in assignment.sequences){
                if (sequence.statement == statement)
                    isUsed = true
            }
        }
        return isUsed
    }

    private fun attachedFileIfAny(fileToAttached: MultipartFile, it: Statement) {
        if (!fileToAttached.isEmpty) {
            attachmentService.saveStatementAttachment(
                    it,
                    SequenceController.createAttachment(fileToAttached),
                    fileToAttached.inputStream)
        }
    }

    @GetMapping("{id}/delete")
    fun delete(authentication: Authentication,
               @PathVariable subjectId: Long,
               @PathVariable id: Long,
               redirectAttributes: RedirectAttributes): String {
        val user: User = authentication.principal as User

        val statement = statementService.get(user, id)
        subjectService.removeStatement(user, statement)

        with(messageBuilder) {
            success(
                    redirectAttributes,
                    message(
                            "sequence.deleted.message",
                            message("sequence.label"),
                            statement.title
                    )
            )
        }

        return "redirect:/subject/$subjectId"
    }

    @GetMapping("{id}/removeAttachment")
    fun removeAttachment(authentication: Authentication,
                         @PathVariable subjectId: Long,
                         @PathVariable id: Long): String {
        val user: User = authentication.principal as User
        statementService.get(user, id).let {
            attachmentService.detachAttachmentFromStatement(user, it)
        }
        return "redirect:/subject/$subjectId/statement/$id/edit"
    }

    @GetMapping("{id}/up")
    fun up(authentication: Authentication,
           @PathVariable subjectId: Long,
           @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        val subject = subjectService.get(user, subjectId, true)
        subjectService.moveUpStatement(subject, id)

        return "redirect:/subject/$subjectId#statement_${id}"
    }

    @GetMapping("{id}/down")
    fun down(authentication: Authentication,
             @PathVariable subjectId: Long,
             @PathVariable id: Long): String {
        val user: User = authentication.principal as User

        val subject = subjectService.get(user, subjectId, true)
        subjectService.moveDownStatement(subject, id)

        return "redirect:/subject/$subjectId#statement_${id}"
    }

}
