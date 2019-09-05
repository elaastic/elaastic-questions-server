package org.elaastic.questions.lti

import org.elaastic.questions.directory.Role

data class LtiUser(
        val lmsKey: String,
        val lmsUserId: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val role: Role
)
