package org.elaastic.questions.directory.controller.command

import org.elaastic.questions.directory.HasEmailOrHasOwner
import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.validation.ValidateHasEmailOrHasOwner
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

@ValidateHasEmailOrHasOwner
data class UserData(
        val id: Long?,
        @field:NotBlank val firstName: String,
        @field:NotBlank val lastName: String,
        val role: String,

        @field:NotBlank
        @field:Pattern(regexp = "^[a-zA-Z0-9_-]{1,15}$")
        val username: String,

        @field:Email val email: String?,
        @field:NotNull val hasOwner: Boolean
) : HasEmailOrHasOwner {


    constructor(user: User) : this(
            user.id,
            user.firstName,
            user.lastName,
            user.roles.first().name,
            user.username,
            user.email,
            user.hasOwner()
    )

    fun populateUser(user: User, roleService: RoleService): User {
        user.firstName = firstName
        user.lastName = lastName
        user.replaceRolesWithMainRole(roleService.roleForName(role, true))
        user.username = username
        user.email = email
        return user
    }

    override fun hasEmail(): Boolean {
        return !email.isNullOrBlank()
    }

    override fun hasOwner(): Boolean {
        return hasOwner
    }
}
