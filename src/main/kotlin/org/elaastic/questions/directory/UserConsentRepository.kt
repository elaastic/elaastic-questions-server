package org.elaastic.questions.directory


import org.elaastic.questions.terms.Terms
import org.springframework.data.jpa.repository.JpaRepository

interface UserConsentRepository : JpaRepository<UserConsent, Long> {

    fun existsByUsernameAndTerms(username: String, terms: Terms): Boolean

}
