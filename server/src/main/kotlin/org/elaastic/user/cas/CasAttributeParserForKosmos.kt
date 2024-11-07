package org.elaastic.user.cas

import org.elaastic.user.Role
import org.jasig.cas.client.authentication.AttributePrincipal

/**
 * Parser for CAS attributes from Kosmos.
 */
class CasAttributeParserForKosmos : CasAttributeParser {
    override fun parseFirstName(principal: AttributePrincipal): String {
        return parseStringAttribute(principal, "prenom")
    }

    override fun parseLastName(principal: AttributePrincipal): String {
        return parseStringAttribute(principal, "nom")
    }

    override fun parseEmail(principal: AttributePrincipal): String? {
        return parseOptionalStringAttribute(principal, "mail")
    }

    override fun parseRoleId(principal: AttributePrincipal): Role.RoleId =
        parseStringAttribute(principal, "profil").let { profile ->
            when (profile) {
                TEACHER -> return Role.RoleId.TEACHER
                STUDENT -> return Role.RoleId.STUDENT
                else -> throw IllegalArgumentException("The profile '$profile' is not supported")
            }
        }

    companion object {
        private const val TEACHER = "Professeur"
        private const val STUDENT = "Eleve"
    }
}