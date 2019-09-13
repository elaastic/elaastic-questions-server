package org.elaastic.questions.attachment

class AttachmentUploadException(override val message: String): IllegalArgumentException(
        message
) {

    override var cause: Throwable? = null

    constructor(message: String, cause: Throwable) : this(message) {
        this.cause = cause
    }
}
