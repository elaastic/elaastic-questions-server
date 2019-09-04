package org.elaastic.questions.lti

import org.springframework.data.jpa.repository.JpaRepository

interface LtiConsumerRepository : JpaRepository<LtiConsumer, String> {
    fun findByKey(key: String): LtiConsumer?
}
