package org.elaastic.questions.attachment

import org.springframework.data.jpa.repository.JpaRepository

interface AttachmentRepository : JpaRepository<Attachment, Long> {

    fun findAllByToDelete(toDelete: Boolean): MutableList<Attachment>
    fun findAllByPathAndIdNot(path: String, id: Long): Collection<Attachment>

}
