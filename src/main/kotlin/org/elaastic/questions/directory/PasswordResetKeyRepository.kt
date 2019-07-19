package org.elaastic.questions.directory

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface PasswordResetKeyRepository : PagingAndSortingRepository<PasswordResetKey, Long>, QueryByExampleExecutor<PasswordResetKey>, JpaRepository<PasswordResetKey, Long> {
}
