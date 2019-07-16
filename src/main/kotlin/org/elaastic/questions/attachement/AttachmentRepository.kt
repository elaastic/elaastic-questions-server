package org.elaastic.questions.attachement

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.QueryByExampleExecutor
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

interface AttachmentRepository : PagingAndSortingRepository<Attachment, Long>, QueryByExampleExecutor<Attachment>, JpaRepository<Attachment, Long> {
}

