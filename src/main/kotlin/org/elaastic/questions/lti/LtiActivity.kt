package org.elaastic.questions.lti

data class LtiActivity(
        val lmsKey: String,
        val lmsActivityId: String,
        val lmsCourseId: String,
        val lmsCourseTitle: String,
        val title: String,
        val globalId: String? = null
)
