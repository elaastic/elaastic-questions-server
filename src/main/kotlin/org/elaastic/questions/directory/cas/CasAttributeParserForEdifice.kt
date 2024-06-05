package org.elaastic.questions.directory.cas

import org.elaastic.questions.directory.Role
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
                "Teacher" -> return Role.RoleId.TEACHER
                "Student" -> return Role.RoleId.STUDENT
                else -> throw IllegalArgumentException("The profile '$profile' is not supported")
            }
        }

}