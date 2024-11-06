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

package org.elaastic.questions.directory.validation

import org.elaastic.questions.directory.HasEmailOrHasOwnerOrHasExternalSource
import org.elaastic.questions.directory.HasPasswords
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserSource
import org.elaastic.questions.directory.controller.command.UserData
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [HasEmailOrHasOwnerValidatorOrIsAnonymous::class])
annotation class ValidateHasEmailOrHasOwnerOrHasExternalSource(
        val message: String = "user.hasEmailOrHasOwner",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)

class HasEmailOrHasOwnerValidatorOrIsAnonymous : ConstraintValidator<ValidateHasEmailOrHasOwnerOrHasExternalSource, HasEmailOrHasOwnerOrHasExternalSource> {

    override fun isValid(user: HasEmailOrHasOwnerOrHasExternalSource?, context: ConstraintValidatorContext?): Boolean {
        return user?.let { it.hasEmail() || it.hasOwner() || it.getSource() != UserSource.ELAASTIC } ?: false
    }
}


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PlainTextPasswordIsTooShortValidator::class])
annotation class PlainTextPasswordIsTooShort(
        val message: String = "user.plainTextPassword.short",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)

class PlainTextPasswordIsTooShortValidator : ConstraintValidator<PlainTextPasswordIsTooShort, User> {

    override fun isValid(user: User?, context: ConstraintValidatorContext?): Boolean {
        return user?.let {
            (it.plainTextPassword == null && it.password != null) ||
                    (it.plainTextPassword != null && it.plainTextPassword!!.length > 3)
        } ?: false
    }
}


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PasswordsMustBeIdenticalValidator::class])
annotation class PasswordsMustBeIdentical(
        val message: String = "useraccount.form.password.identical",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)

class PasswordsMustBeIdenticalValidator : ConstraintValidator<PasswordsMustBeIdentical, HasPasswords> {

    override fun isValid(user: HasPasswords?, context: ConstraintValidatorContext?): Boolean {
        return user?.let {
            (it.password1 == null && it.password2 == null) ||
            (it.password1 != null && it.password2 != null && it.password1 == it.password2)
        } ?: false
    }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [UserHasGivenConsentValidator::class])
annotation class UserHasGivenConsent(
        val message: String = "Consent",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)

class UserHasGivenConsentValidator : ConstraintValidator<UserHasGivenConsent, UserData> {

    override fun isValid(user: UserData?, context: ConstraintValidatorContext?): Boolean {
        return user?.let {
            it.userHasGivenConsent
        } ?: false
    }
}
