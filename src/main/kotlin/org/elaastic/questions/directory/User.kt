package org.elaastic.questions.directory

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.io.Serializable
import javax.persistence.*
import javax.validation.*
import javax.validation.constraints.*
import kotlin.jvm.Transient
import kotlin.reflect.KClass

/**
 * @author John Tranier
 */
@Entity
@NamedEntityGraph(name = "User.roles", attributeNodes = [NamedAttributeNode("roles")])
@User.HasEmailOrIsOwner
@User.PlainTextPasswordIsNotShort
class User(
        @field:NotBlank var firstName: String,
        @field:NotBlank var lastName: String,

        @field:NotBlank
        @field:Column(unique = true, length = 16)
        @field:Pattern(regexp = "^[a-zA-Z0-9_-]{1,15}$")
        private var username: String,

        @Transient
        val plainTextPassword: String,

        email: String
) : AbstractJpaPersistable<Long>(), Serializable, UserDetails {

    @Version
    var version: Long? = null

    @Column(unique = true)
    @Email
    var email: String? = email

    @NotNull @Size(min = 1)
    private var password: String? = null


    var enabled: Boolean = true
    var accountExpired: Boolean = false
    var accountLocked: Boolean = false
    var passwordExpired: Boolean = false

    @NotNull
    var canBeUserOwner: Boolean = false

    @ManyToOne(fetch = FetchType.LAZY)
    var owner: User? = null

    fun getFullname(): String {
        return "${this.firstName} ${this.lastName}"
    }

    fun hasEmail(): Boolean {
        return !email.isNullOrBlank()
    }

    fun hasOwner(): Boolean {
        return owner != null
    }

    @ManyToMany(cascade = [CascadeType.ALL],
            targetEntity = Role::class
    )
    @JoinTable(
            name = "user_role",
            joinColumns = [JoinColumn(name = "user_id")],
            inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: MutableSet<Role> = HashSet()

    fun addRole(role: Role): User {
        roles.add(role)
        return this
    }

    @OneToOne(mappedBy = "user")
    var settings: Settings? = null

    fun isLearner(): Boolean {
        return roles.map { it.name }.contains(Role.RoleId.STUDENT.roleName)
    }

    fun isTeacher(): Boolean {
        return roles.map { it.name }.contains(Role.RoleId.TEACHER.roleName)
    }

    fun isAdmin(): Boolean {
        return roles.map { it.name }.contains(Role.RoleId.ADMIN.roleName)
    }

    fun isRegisteredInAssignment(assignment: Assignment): Boolean {
        return false
        // TODO("Must find out how to implement it")
        // LearnerAssignment.findByLearnerAndAssignment(this,assignment)
    }

    override fun getUsername(): String {
        return username
    }

    fun setUsername(value: String) {
        username = value
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return roles
    }

    override fun isEnabled(): Boolean {
        return enabled
    }

    override fun isCredentialsNonExpired(): Boolean {
        return !passwordExpired
    }

    override fun getPassword(): String? {
        return password
    }

    fun setPassword(value: String?) {
        password = value
    }

    override fun isAccountNonExpired(): Boolean {
        return !accountExpired
    }

    override fun isAccountNonLocked(): Boolean {
        return !accountLocked
    }

    override fun toString(): String {
        return username
    }

    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    @Constraint(validatedBy = [HasEmailOrIsOwnerValidator::class])
    annotation class HasEmailOrIsOwner(
            val message: String = "user.hasEmailOrIsOwner",
            val groups: Array<KClass<*>> = [],
            val payload: Array<KClass<out Payload>> = []
    )

    class HasEmailOrIsOwnerValidator : ConstraintValidator<HasEmailOrIsOwner, User> {

        override fun isValid(user: User?, context: ConstraintValidatorContext?): Boolean {
            return user?.let { it.hasEmail() || it.hasOwner() } ?: false
        }
    }


    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    @Constraint(validatedBy = [PlainTextPasswordIsNotShortValidator::class])
    annotation class PlainTextPasswordIsNotShort(
            val message: String = "user.plainTextPassword.short",
            val groups: Array<KClass<*>> = [],
            val payload: Array<KClass<out Payload>> = []
    )

    class PlainTextPasswordIsNotShortValidator : ConstraintValidator<PlainTextPasswordIsNotShort, User> {

        override fun isValid(user: User?, context: ConstraintValidatorContext?): Boolean {
            return user?.let { it.plainTextPassword.length > 3 } ?: false
        }
    }
}
