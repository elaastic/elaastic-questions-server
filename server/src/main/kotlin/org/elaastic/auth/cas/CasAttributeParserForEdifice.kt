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

package org.elaastic.auth.cas

import org.elaastic.user.Role
import org.jasig.cas.client.authentication.AttributePrincipal

/**
 * Parser for CAS attributes from Edifice.
 */
class CasAttributeParserForEdifice : CasAttributeParser {
    override fun parseFirstName(principal: AttributePrincipal): String {
        return parseStringAttribute(principal, "firstName")
    }

    override fun parseLastName(principal: AttributePrincipal): String {
        return parseStringAttribute(principal, "lastName")
    }

    override fun parseEmail(principal: AttributePrincipal): String? {
        return parseOptionalStringAttribute(principal, "email")
    }

    override fun parseRoleId(principal: AttributePrincipal): Role.RoleId =
        parseStringAttribute(principal, "profile").let { profile ->
            when (profile) {
                TEACHER, OTHER_KIND_OF_TEACHER -> return Role.RoleId.TEACHER
                STUDENT -> return Role.RoleId.STUDENT
                else -> throw IllegalArgumentException("The profile '$profile' is not supported")
            }
        }

    companion object {
        private const val TEACHER = "Teacher"
        private const val STUDENT = "Student"
        private const val OTHER_KIND_OF_TEACHER = "Personnel"
    }

}