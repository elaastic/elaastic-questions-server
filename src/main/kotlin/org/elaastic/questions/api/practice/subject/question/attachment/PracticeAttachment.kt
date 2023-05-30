package org.elaastic.questions.api.practice.subject.question.attachment

import com.toedter.spring.hateoas.jsonapi.JsonApiId
import com.toedter.spring.hateoas.jsonapi.JsonApiType
import org.elaastic.questions.attachment.Attachment
import org.elaastic.questions.attachment.Dimension

/**
 * Represent an Attachment bound to a PracticeQuestion
 * It will be rendered as json:api with its self URL allowing for downloading its metadata & File
 *
 * @author John Tranier
 */
class PracticeAttachment(
    @JsonApiId val id: Long,
    val name: String,
    val originalFileName: String? = null,
    val size: Long? = null,
    val dimension: Dimension? = null
    ) {

    @JsonApiType
    val type = "practice-attachment"

    constructor(attachment: Attachment): this(
        attachment.id!!,
        attachment.name,
        attachment.originalFileName,
        attachment.size,
        attachment.dimension,
    )
}