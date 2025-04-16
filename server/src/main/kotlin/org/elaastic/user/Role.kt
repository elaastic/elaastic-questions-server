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

package org.elaastic.user

import org.elaastic.common.persistence.AbstractJpaPersistable
import org.elaastic.user.Role.RoleId
import org.elaastic.user.Role.RoleId.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.core.GrantedAuthority
import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import kotlin.collections.Collection


/** Role entity */
@Entity
@Cacheable("roles")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
class Role(
    @field:Column(name = "authority")
    var name: String
) : AbstractJpaPersistable<Long>(), Serializable, GrantedAuthority {

    constructor(role: RoleId) : this(role.roleName)

    /**
     * All possible roles in the application
     *
     * @property STUDENT student role
     * @property TEACHER teacher role
     * @property ADMIN admin role
     */
    enum class RoleId(val roleName: String) {
        STUDENT("STUDENT_ROLE"),
        TEACHER("TEACHER_ROLE"),
        ADMIN("ADMIN_ROLE"),
    }

    /** @return the name of the role */
    override fun getAuthority(): String {
        return name
    }

    /**
     * Check if the role is equal to another object
     *
     * This function will accept a String, a RoleId or another Role object.
     *
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        return when (other) {
            is String -> this.name == other
            is RoleId -> this.name == other.roleName
            is Role -> super.equals(other) && this.name == other.name
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}

/**
 * Extension function to check if a list of roles contains a specific role
 *
 * @see Role.equals
 */
fun Collection<Role>.contains(role: RoleId): Boolean {
    return this.any { it.equals(role) }
}