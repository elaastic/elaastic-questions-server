package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.sequence.explanation.FakeExplanation
import org.elaastic.questions.attachment.AttachmentService
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.logging.Logger
import javax.transaction.Transactional

@RestController
@RequestMapping("/api/assignment/{assignmentId}/sequence")
@Transactional
class RestSequenceController(
        @Autowired val sequenceService: SequenceService,
        @Autowired val attachmentService: AttachmentService
) {

    val logger:Logger = Logger.getLogger(RestSequenceController::class.java.name)

    @GetMapping("{id}/findAllFakeExplanation")
    fun findAllFakeExplanation(authentication: Authentication,
                               @PathVariable id: Long): List<FakeExplanationData> {
        val user: User = authentication.principal as User

        return sequenceService.findAllFakeExplanation(user, id).map {
            FakeExplanationData(it.correspondingItem ?: 1, it.content)
        }
    }

    @GetMapping("{id}/removeAttachment")
    fun removeAttachment(authentication: Authentication,
                         @PathVariable id: Long) {
        val user: User = authentication.principal as User
        sequenceService.get(user, id).let {
            attachmentService.detachAttachmentFromStatement(user, it.statement)
        }

    }
}

open class FakeExplanationData() {

    constructor(correspondingItem: Int?, content: String?) : this() {
        this.correspondingItem = correspondingItem
        this.content = content
    }

    constructor(fakeExplanation: FakeExplanation) :
            this(fakeExplanation.correspondingItem, fakeExplanation.content)

    var correspondingItem: Int? = null
    var content: String? = null
}
