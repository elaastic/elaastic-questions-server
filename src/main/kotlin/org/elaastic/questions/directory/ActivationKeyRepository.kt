package org.elaastic.questions.directory

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.QueryByExampleExecutor
import java.util.*

interface ActivationKeyRepository : JpaRepository<ActivationKey, Long> {

    fun findByUser(user: User): ActivationKey?
    fun findByActivationKey(activationKey: String): ActivationKey?
    fun findAllByDateCreatedLessThan(expirationDate:Date): Collection<ActivationKey>

}
