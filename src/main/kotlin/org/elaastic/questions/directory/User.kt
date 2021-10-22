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

package org.elaastic.questions.directory

import org.elaastic.questions.directory.validation.PlainTextPasswordIsTooShort
import org.elaastic.questions.directory.validation.ValidateHasEmailOrHasOwnerOrIsAnonymous
import org.elaastic.questions.onboarding.OnboardingChapter
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.*
import kotlin.jvm.Transient


@Entity
@NamedEntityGraph(name = "User.roles", attributeNodes = [NamedAttributeNode("roles")])
@ValidateHasEmailOrHasOwnerOrIsAnonymous
@PlainTextPasswordIsTooShort
class User(
    @field:NotBlank var firstName: String,
    @field:NotBlank var lastName: String,

    @field:NotBlank
        @field:Column(unique = true, length = 32)
        @field:Pattern(regexp = "^[a-zA-Z0-9_-]{1,31}$")
        private var username: String,

    @Transient
        var plainTextPassword: String?,

    @field:Column(unique = true)
        @field:Email
        var email: String? = null,

    private var isAnonymous: Boolean = false,

    @field:Enumerated(EnumType.STRING)
    private var onboardingChapter: OnboardingChapter? = OnboardingChapter.INTRODUCTION
) : AbstractJpaPersistable<Long>(), Serializable, UserDetails, HasEmailOrHasOwnerOrIsAnonymous {

    @Version
    var version: Long? = null

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
        return if(isAnonymous) firstName else "${firstName} ${lastName}"
    }

    override fun hasEmail(): Boolean {
        return !email.isNullOrBlank()
    }

    override fun hasOwner(): Boolean {
        return owner != null
    }

    override fun isAnonymous(): Boolean {
        return isAnonymous
    }

    fun getOnboardingChapter(): OnboardingChapter? {
        return onboardingChapter
    }

    fun setOnboardingChapter(value: OnboardingChapter?) {
        onboardingChapter = value
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

    /**
     * Replace the main role
     * @param role the new main role
     * @return the user
     */
    fun replaceRolesWithMainRole(role: Role): User {
        roles = HashSet()
        roles.add(role)
        return this
    }

    @OneToOne(mappedBy = "user")
    var settings: Settings? = null

    @OneToOne(mappedBy = "user")
    var unsubscribeKey: UnsubscribeKey? = null

    @OneToOne(mappedBy = "user")
    var activationKey: ActivationKey? = null

    fun isLearner(): Boolean {
        return roles.map { it.name }.contains(Role.RoleId.STUDENT.roleName)
    }

    fun isTeacher(): Boolean {
        return roles.map { it.name }.contains(Role.RoleId.TEACHER.roleName)
    }

    fun isAdmin(): Boolean {
        return roles.map { it.name }.contains(Role.RoleId.ADMIN.roleName)
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

}
