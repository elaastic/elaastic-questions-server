package org.elaastic.questions.directory.controller.command

import org.elaastic.questions.directory.HasPasswords
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.validation.PasswordsMustBeIdentical
import org.springframework.validation.BindingResult
import javax.validation.constraints.Size

@PasswordsMustBeIdentical
class PasswordData(
        val id: Long?,
        val password: String? = null,
        @field:Size(min = 4) override val password1: String? = null,
        override val password2: String? = null
) : HasPasswords {

    constructor(user: User) : this(
            user.id
    )

    fun catchSecurityException(e: SecurityException, result: BindingResult) {
        when {
            "Bad.user.password" == e.message -> result.rejectValue("password", "Bad")
            else -> throw e
        }
    }
}
