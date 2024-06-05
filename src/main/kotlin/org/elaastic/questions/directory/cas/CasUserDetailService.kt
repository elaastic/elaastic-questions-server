package org.elaastic.questions.directory.cas

import org.elaastic.questions.directory.*
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
        return casUserRepository.findByCasKeyAndCasUserId(casKey, username)?.user?.also { it.casKey = casKey }
    }


    private fun getCasAttributeParser(casProvider: String): CasAttributeParser {
        when(casProvider) {
            SupportedCasProvider.Kosmos.name -> return CasKosmosAttributeParser()
            else -> throw IllegalArgumentException("The CAS provider '$casProvider' is not supported")
        }
    }

    @Transactional
    fun registerNewCasUser(casKey: String, casProvider: String, principal: AttributePrincipal): UserDetails {
        val casAttributeParser = getCasAttributeParser(casProvider)
        val firstName = casAttributeParser.parseFirstName(principal)
        val lastName = casAttributeParser.parseLastName(principal)
        val email = casAttributeParser.parseEmail(principal)
        val roleId = casAttributeParser.parseRoleId(principal)

        val user = User(
            firstName = firstName,
            lastName = lastName,
            username = userService.generateUsername(firstName, lastName),
            plainTextPassword = userService.generatePassword(),
            email = email,
            source = UserSource.CAS,
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
            casKey = casKey,
            casUserId = principal.name,
            user = user,
            ).let { casUserRepository.save(it) }

        return user
    }
}