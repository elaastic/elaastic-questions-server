package org.elaastic.questions.directory

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.QueryByExampleExecutor
import java.util.*

interface PasswordResetKeyRepository : PagingAndSortingRepository<PasswordResetKey, Long>, QueryByExampleExecutor<PasswordResetKey>, JpaRepository<PasswordResetKey, Long> {

    fun findByUser(user: User): PasswordResetKey?

    fun findByPasswordResetKey(passwordResetKey: String): PasswordResetKey?

    @Query("select prk from PasswordResetKey prk left join prk.user user left join user.settings where prk.dateCreated > ?1 and prk.passwordResetEmailSent = false ")
    fun findAllPasswordResetKeys(expirationDate: Date):Collection<PasswordResetKey>

}
