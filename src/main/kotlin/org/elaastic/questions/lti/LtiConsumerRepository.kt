package org.elaastic.questions.lti

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface LtiConsumerRepository : PagingAndSortingRepository<LtiConsumer, String>, QueryByExampleExecutor<LtiConsumer>, JpaRepository<LtiConsumer, String>
