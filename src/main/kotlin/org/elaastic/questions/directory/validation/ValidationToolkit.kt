package org.elaastic.questions.directory.validation

import org.elaastic.questions.directory.HasEmailOrHasOwner
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.controller.command.UserData
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [HasEmailOrHasOwnerValidator::class])
annotation class ValidateHasEmailOrHasOwner(
        val message: String = "user.hasEmailOrHasOwner",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
)

class HasEmailOrHasOwnerValidator : ConstraintValidator<ValidateHasEmailOrHasOwner, HasEmailOrHasOwner> {

    override fun isValid(user: HasEmailOrHasOwner?, context: ConstraintValidatorContext?): Boolean {
        return user?.let { it.hasEmail() || it.hasOwner() } ?: false
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

class PasswordsMustBeIdenticalValidator : ConstraintValidator<PasswordsMustBeIdentical, UserData> {

    override fun isValid(user: UserData?, context: ConstraintValidatorContext?): Boolean {
        return user?.let {
            (it.password1 == null && it.password2 == null) ||
            (it.password1 != null && it.password2 != null && it.password1 == it.password2)
        } ?: false
    }
}
