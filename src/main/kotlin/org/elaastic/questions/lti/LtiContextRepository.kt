package org.elaastic.questions.lti

import org.springframework.data.jpa.repository.JpaRepository

interface LtiContextRepository: JpaRepository<LtiContext, LtiContext.LtiContextId> {

}
