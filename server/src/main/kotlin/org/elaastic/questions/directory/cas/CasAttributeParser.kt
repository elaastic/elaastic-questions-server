package org.elaastic.questions.directory.cas

import org.elaastic.questions.directory.Role
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

