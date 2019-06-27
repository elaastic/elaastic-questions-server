package org.elaastic.questions

import org.elaastic.questions.persistence.AbstractJpaPersistable
import javax.persistence.*
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import javax.validation.constraints.*
import kotlin.reflect.KClass

/**
 * @author John Tranier
 */
@Entity
class User(
        firstName: String,
        lastName: String,
        username: String,
        password: String,
        email: String
) : AbstractJpaPersistable<Long>() {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @NotBlank
    var firstName: String = firstName

    @NotBlank
    var lastName: String = lastName

    @Column(unique = true)
    @ElaasticEmail
    @NotNull
    var email: String = email

    @Size(min = 4)
    var password: String? = password // TODO I don't like those 2 password fields ...

    @Transient
    var plainTextPassword: String? = null

    var enabled: Boolean = true
    var accountExpired: Boolean = false
    var accountLocked: Boolean = false
    var passwordExpired: Boolean = false

    @NotNull
    var canBeUserOwner: Boolean = false

    @ManyToOne
    var owner: User? = null
    // TODO Settings

    @NotBlank
    @Column(unique = true)
    @Pattern(regexp = "^[a-zA-Z0-9_-]{1,15}$")
    var username: String = username
        set(value) {
            field = value
            this.normalizedUsername = value.toLowerCase()
        }

    // TODO A simple getter should be sufficient no?
    @Column(unique = true)
    var normalizedUsername = username.toLowerCase()

    fun getFullname(): String {
        return "${this.firstName} ${this.lastName}"
    }

    @Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @Constraint(validatedBy = arrayOf(ElaasticEmailValidator::class))
    annotation class ElaasticEmail(
            val message: String = "",
            val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
    )

    class ElaasticEmailValidator : ConstraintValidator<ElaasticEmail, String> {


        override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    // TODO Implement methods

}