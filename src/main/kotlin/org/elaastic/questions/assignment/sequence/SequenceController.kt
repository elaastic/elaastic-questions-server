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

package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.sequence.explanation.FakeExplanationService
import org.elaastic.questions.attachment.*
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.directory.User
import org.elaastic.questions.subject.statement.StatementController
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
import javax.servlet.http.HttpServletResponse
import javax.transaction.Transactional
import javax.validation.Valid

@Controller
@RequestMapping("/assignment/{assignmentId}/sequence")
@Transactional
class SequenceController(
        @Autowired val assignmentService: AssignmentService,
        @Autowired val sequenceService: SequenceService,
        @Autowired val statementService: StatementService,
        @Autowired val fakeExplanationService: FakeExplanationService,
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
                StatementController.StatementData(
                        sequence.statement,
                        fakeExplanationService.findAllByStatement(sequence.statement)
                )
        )

        return "/assignment/sequence/edit"
    }

    @PostMapping("save")
    fun save(authentication: Authentication,
             @RequestParam("fileToAttached") fileToAttached: MultipartFile,
             @Valid @ModelAttribute statementData: StatementController.StatementData,
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
            // attachedFileIfAny(fileToAttached, sequence.statement)
            return "redirect:/assignment/$assignmentId#sequence_${sequence.id}"
        }
    }

    @PostMapping("{id}/update")
    fun update(authentication: Authentication,
               @RequestParam("fileToAttached") fileToAttached: MultipartFile,
               @Valid @ModelAttribute statementData: StatementController.StatementData,
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
                //attachedFileIfAny(fileToAttached, it)
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

    @GetMapping("{id}/delete")
    fun delete(authentication: Authentication,
               @PathVariable assignmentId: Long,
               @PathVariable id: Long,
               redirectAttributes: RedirectAttributes): String {
        val user: User = authentication.principal as User

        val sequence = sequenceService.get(user, id)
        assignmentService.removeSequence(user, sequence)

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

}
