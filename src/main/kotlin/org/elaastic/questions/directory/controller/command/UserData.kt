/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.elaastic.questions.directory.controller.command

import org.elaastic.questions.directory.HasEmailOrHasOwnerOrIsAnonymous
import org.elaastic.questions.directory.HasPasswords
import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.validation.PasswordsMustBeIdentical
import org.elaastic.questions.directory.validation.UserHasGivenConsent
import org.elaastic.questions.directory.validation.ValidateHasEmailOrHasOwnerOrIsAnonymous
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.validation.BindingResult
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

@ValidateHasEmailOrHasOwnerOrIsAnonymous
@PasswordsMustBeIdentical
@UserHasGivenConsent
data class UserData(
        val id: Long?,
        @field:NotBlank val firstName: String,
        @field:NotBlank val lastName: String,
        val role: String,

        @field:NotBlank
        @field:Pattern(regexp = "^[a-zA-Z0-9_-]{1,15}$")
        val username: String,

        @field:Email val email: String?,
        @field:NotNull val hasOwner: Boolean = false,
        override val password1: String? = null,
        override val password2: String? = null,
        val language:String = "fr",

        @field:NotNull
        var userHasGivenConsent: Boolean = false
) : HasEmailOrHasOwnerOrIsAnonymous, HasPasswords {

    constructor(user: User, userHasGivenConsent: Boolean) : this(
            user.id,
            user.firstName,
            user.lastName,
            user.roles.first().name,
            user.username,
            user.email,
            user.hasOwner(),
            userHasGivenConsent = userHasGivenConsent
    )

    override fun isAnonymous(): Boolean {
        return false
    }

    fun populateUser(user: User, roleService: RoleService): User {
        user.firstName = firstName
        user.lastName = lastName
        user.replaceRolesWithMainRole(roleService.roleForName(role, true))
        user.username = username
        user.email = email
        return user
    }

    fun populateNewUser(roleService: RoleService): User {
        User(
                firstName,
                lastName,
                username,
                password1,
                email
        ).addRole(roleService.roleForName(role, true)).let {
            return it
        }
    }

    fun catchDataIntegrityViolationException(e: DataIntegrityViolationException, result: BindingResult) {
        when {
            e.message == null -> throw e
            e.message!!.contains("username") -> result.rejectValue("username", "Unique")
            e.message!!.contains("email") -> result.rejectValue("email", "Unique")
            else -> throw e
        }
    }

    override fun hasEmail(): Boolean {
        return !email.isNullOrBlank()
    }

    override fun hasOwner(): Boolean {
        return hasOwner
    }
}
