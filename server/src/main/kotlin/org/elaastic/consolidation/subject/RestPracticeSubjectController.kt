package org.elaastic.consolidation.subject

import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder.jsonApiModel
import org.elaastic.material.instructional.question.attachment.AttachmentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import javax.persistence.EntityNotFoundException

/**
 * REST controller of the practice-api that allows to retrieve practice subjects built from
 * assignments & their sequences.
 *
 * A practice subject is a subject, composed of questions, on which a learner can practice itself.
 * A practice subject is built from an assignment that has been already played by its learners (i.e. at least
 * some of its sequences has been terminated).
 *
 * Each question is associated with
 * - the expected explanation provided by the teacher
 * - the 3 best ranked explanations provided by learner that have played the associated sequence
 *
 * @author John Tranier
 *
 */
@RestController
@RequestMapping(RestPracticeSubjectController.PRACTICE_API_URL)
class RestPracticeSubjectController(
    @Autowired val practiceSubjectService: PracticeSubjectService,
    @Autowired val attachmentService: AttachmentService,
) {

    companion object {
        const val PRACTICE_API_URL = "/api/practice/v1"
    }

    /**
     * Returns the list of available practice subjects that has been created or updated since the provided date
     * @param since : a date at the ISO 8601 format
     */
    @GetMapping("subjects")
    fun findAllPracticeSubject(@RequestParam since: String): RepresentationModel<*> {
        val sinceDate = try {
            OffsetDateTime.parse(since, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: DateTimeParseException) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "'since' parameter must be an ISO_DATE_TIME ; provided : '${since}'",
                e
            )
        }

        return jsonApiModel()
            .model(
                CollectionModel.of(
                    practiceSubjectService.findAllPracticeSubject(sinceDate.toLocalDateTime())
                        .map(PracticeSubjectModelBuilder::buildSummary)
                )
            )
            .link(linkTo<RestPracticeSubjectController> { findAllPracticeSubject(since) }.withSelfRel())
            .build()

    }

    /**
     * Returns the detailed representation of a practice subject from its uuid
     */
    @GetMapping("subjects/{uuid}")
    fun getPracticeSubject(@PathVariable uuid: UUID): RepresentationModel<*> {
        return try {
            practiceSubjectService
                .getPracticeSubject(uuid)
                .let(PracticeSubjectModelBuilder::buildDetailed)
        } catch (e: EntityNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "There is no subject for id='$uuid'",
                e
            )
        }
    }

    /**
     * Retrieve the blob of an attachment bound to a question itself bound to a practice subject
     */
    @GetMapping("subjects/{subjectUuid}/questions/{questionUuid}/attachment/{attachmentUuid}/blob")
    @ResponseBody
    fun getAttachmentBlob(
        @PathVariable subjectUuid: UUID,
        @PathVariable questionUuid: UUID,
        @PathVariable attachmentUuid: UUID
    ): ResponseEntity<Resource>? {
        if (!practiceSubjectService.isAttachmentReadyToPractice(
                subjectUuid,
                questionUuid,
                attachmentUuid
            )
        ) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "The attachment does not exists or is not bound to a practice question"
            )
        }

        val attachment = attachmentService.getAttachmentByUuid(attachmentUuid)

        InputStreamResource(attachmentService.getInputStreamForAttachment(attachment)).let {
            return ResponseEntity.ok().header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"${attachment.originalFileName}\""
            ).body(it)
        }
    }

}