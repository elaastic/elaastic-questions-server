package org.elaastic.questions.directory

import org.elaastic.questions.assignment.Assignment
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
@User.HasEmailOrIsOwner
class User(
        @field:NotBlank var firstName: String,
        @field:NotBlank var lastName: String,
        username: String,
        password: String,
        email: String
) : AbstractJpaPersistable<Long>() {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @Version
    var version: Int? = null

    @Column(unique = true)
    @Email
    var email: String? = email

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

    @NotBlank
    @Column(unique = true, length = 16)
    @Pattern(regexp = "^[a-zA-Z0-9_-]{1,15}$")
    var username: String = username

    fun getFullname(): String {
        return "${this.firstName} ${this.lastName}"
    }

    fun hasEmail(): Boolean {
        return !email.isNullOrBlank()
    }

    fun hasOwner() : Boolean {
        return owner != null
    }

    @OneToMany(fetch = FetchType.EAGER,
            cascade = arrayOf(CascadeType.ALL),
            targetEntity = Role::class
    )
    @JoinTable(
            name = "user_role",
            joinColumns = arrayOf(JoinColumn(name = "user_id")),
            inverseJoinColumns = arrayOf(JoinColumn(name = "role_id"))
    )
    var roles: Set<Role> = HashSet()

    fun isLearner(): Boolean {
        TODO("Must find out how to implement it")
        // UserRole.get(this.id, RoleEnum.STUDENT_ROLE.id)
    }

    fun isTeacher(): Boolean {
        TODO("Must find out how to implement it")
        // UserRole.get(this.id, RoleEnum.TEACHER_ROLE.id)
    }

    fun isAdmin(): Boolean {
        TODO("Must find out how to implement it")
        // UserRole.get(this.id, RoleEnum.ADMIN_ROLE.id)
    }

    fun isRegisteredInAssignment(assignment: Assignment) {
        TODO("Must find out how to implement it")
        // LearnerAssignment.findByLearnerAndAssignment(this,assignment)
    }

    override fun toString(): String {
        return username
    }

    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    @Constraint(validatedBy = arrayOf(HasEmailOrIsOwnerValidator::class))
    annotation class HasEmailOrIsOwner(
            val message: String = "",
            val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
    )

    class HasEmailOrIsOwnerValidator : ConstraintValidator<HasEmailOrIsOwner, User> {

        override fun isValid(user: User?, context: ConstraintValidatorContext?): Boolean {
            return user?.let { it.hasEmail() || it.hasOwner() } ?: false
        }
    }

    // TODO Settings

}