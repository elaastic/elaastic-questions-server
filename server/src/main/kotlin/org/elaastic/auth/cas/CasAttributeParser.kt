/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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
 * Describe a parser for CAS attributes.
 */
interface CasAttributeParser {
    /**
     * Parse the first name of the user.
     */
    fun parseFirstName(principal: AttributePrincipal): String

    /**
     * Parse the last name of the user.
     */
    fun parseLastName(principal: AttributePrincipal): String

    /**
     * Parse the email of the user.
     */
    fun parseEmail(principal: AttributePrincipal): String?

    /**
     * Parse the role id of the user.
     */
    fun parseRoleId(principal: AttributePrincipal): Role.RoleId

    fun parseStringAttribute(principal: AttributePrincipal, attributeName: String): String =
        principal.attributes[attributeName].let { value ->
            when (value) {
                is String -> value
                null -> throw IllegalArgumentException("The attribute '$attributeName' is mandatory")
                else -> throw IllegalArgumentException("The attribute '$attributeName' is expected of type 'String' ; provided ${value::class.java}")
            }
        }

    fun parseOptionalStringAttribute(principal: AttributePrincipal, attributeName: String): String? =
        principal.attributes[attributeName].let { value ->
            if (value is String?)
                value
            else throw IllegalArgumentException("The attribute '$attributeName' is expected of type 'String?' ; provided ${value!!::class.java}")
        }


}

