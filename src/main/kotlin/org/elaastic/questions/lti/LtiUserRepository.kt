package org.elaastic.questions.lti

import org.springframework.data.jpa.repository.JpaRepository

interface LtiUserRepository: JpaRepository<LtiUser, LtiUser.LtiUserId> {

}
