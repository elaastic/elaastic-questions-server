package org.elaastic.questions.directory.cas

import org.elaastic.questions.directory.Role
import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserService
import org.jasig.cas.client.authentication.AttributePrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import javax.transaction.Transactional


@Service
class CasUserDetailService(
    @Autowired val casUserRepository: CasUserRepository,
    @Autowired val userService: UserService,
    @Autowired val roleService: RoleService,
)  {

    fun loadUserByUsername(casKey: String, username: String): UserDetails? {
        return casUserRepository.findByCasKeyAndCasUserId(casKey, username)?.user
    }

    private fun parseStringAttribute(principal: AttributePrincipal, attributeName: String): String =
        principal.attributes[attributeName].let { value ->
            when(value) {
                is String -> value
                null -> throw IllegalArgumentException("The attribute '$attributeName' is mandatory")
                else -> throw IllegalArgumentException("The attribute '$attributeName' is expected of type 'String' ; provided ${value!!::class.java}")
            }
        }

    private fun parseOptionalStringAttribute(principal: AttributePrincipal, attributeName: String): String? =
        principal.attributes[attributeName].let { value ->
            if(value is String?)
                value
            else throw IllegalArgumentException("The attribute '$attributeName' is expected of type 'String?' ; provided ${value!!::class.java}")
        }

    private fun parseRoleId(principal: AttributePrincipal): Role.RoleId =
        parseStringAttribute(principal, "profil").let { profile ->
            when(profile) {
                "Professeur" -> return Role.RoleId.TEACHER
                "Eleve" -> return Role.RoleId.STUDENT
                else -> throw IllegalArgumentException("The profile '$profile' is not supported")
            }
        }

    @Transactional
    fun registerNewCasUser(casKey: String, principal: AttributePrincipal): UserDetails {
        val firstName = parseStringAttribute(principal, "prenom")
        val lastName = parseStringAttribute(principal, "nom")
        val email = parseStringAttribute(principal, "mail")
        val roleId = parseRoleId(principal)
        val birthDate = parseOptionalStringAttribute(principal, "dateNaissance")
        val rne = parseOptionalStringAttribute(principal, "rneCourant")

        if(roleId == Role.RoleId.TEACHER) {
            // TODO ENTAuxEnsClasses
        }

        if(roleId == Role.RoleId.STUDENT) {
            // TODO ENTEleveClasses + ENTEleveNivFormation
        }

        val user = User(
            firstName = firstName,
            lastName = lastName,
            username = userService.generateUsername(firstName, lastName),
            plainTextPassword = userService.generatePassword(),
            email = email,
        ).let {
            userService.addUser(
                it.addRole(roleService.roleForName(roleId.roleName, true)),
                "fr",
                checkEmailAccount = false,
                enable = true,
                addUserConsent = true
            )
        }

        CasUser(
            casKey = casKey, // TODO Find out how to retrieve the proper casKey
            casUserId = principal.name,
            user = user,
            ).let { casUserRepository.save(it) }

        return user
    }
}