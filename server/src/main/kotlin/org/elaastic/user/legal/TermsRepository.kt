package org.elaastic.user.legal

import org.springframework.data.jpa.repository.JpaRepository

interface TermsRepository : JpaRepository<Terms, Long> {

    fun findByIsActive(isActive: Boolean): Terms?

    fun findAllByIdIsNot(id: Long): List<Terms>

}