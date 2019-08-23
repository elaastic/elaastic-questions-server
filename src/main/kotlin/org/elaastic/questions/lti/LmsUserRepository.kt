package org.elaastic.questions.lti

import org.springframework.data.jpa.repository.JpaRepository

interface LmsUserRepository: JpaRepository<LmsUser, Long> {

    fun findByLmsUserIdAndAndLms(lmsUserId: String, lms: LtiConsumer): LmsUser?

}
