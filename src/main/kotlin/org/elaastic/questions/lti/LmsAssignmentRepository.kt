package org.elaastic.questions.lti

import org.springframework.data.jpa.repository.JpaRepository

interface LmsAssignmentRepository: JpaRepository<LmsAssignment, Long> {

    fun findByLmsActivityIdAndLmsCourseIdAndLms(lmsActivityId: String, lmsCourseId: String, lms: LtiConsumer): LmsAssignment?

}
