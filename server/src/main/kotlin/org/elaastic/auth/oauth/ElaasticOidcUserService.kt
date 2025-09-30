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

package org.elaastic.auth.oauth

import org.elaastic.auth.UserLinkService
import org.elaastic.common.util.alsoThrowIf
import org.elaastic.user.Role.RoleId
import org.elaastic.user.UserCreateCommand
import org.elaastic.user.UserSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service
import java.util.Locale
import java.util.Locale.getDefault

/** Key to get the realm access from the OIDC user's claims */
private const val REALM_ACCESS_KEY = "realm_access"

/**
 * Key to get the roles from the realm access
 *
 * @see REALM_ACCESS_KEY
 */
private const val ROLES_KEY = "roles"

/**
 * Service dedicated to retrieve the Elaastic User bound to an OidcUser
 *
 * Implementation note: This service overrides OidcUserService, and, as
 * such, must return an OidcUser. The ElaasticOidcUser is an OidcUser bound
 * to its corresponding Elaastic User
 *
 * @author John Tranier
 */
@Service
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(prefix = "elaastic.openid", name = ["enabled"], havingValue = "true")
open class ElaasticOidcUserService(
    private val userLinkService: UserLinkService,
) : OidcUserService() {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    // TODO I do not understand why getting statically the OIDC provider ; it should be extracted from the OIDC user
    @Value("\${spring.security.oauth2.client.registration.keycloak.provider:}")
    val oidcProvider: String = "oidcProvider default value"


    override fun loadUser(userRequest: OidcUserRequest?): OidcUser {
        val oidcUser = super.loadUser(userRequest)

        requireNotNull(userRequest)

        try {
            val role = getRoleFromOidcUser(oidcUser)
            val user = userLinkService.loadUserLinkByUsername(
                oidcProvider,
                oidcUser.name
            )?.alsoThrowIf({ !(it.user hasRole role) }, RoleException::class.java) {
                "ElaasticUser ${it.user.username} does not have the role $role but the OIDC user ${oidcUser.name} has it. " +
                        "ElaasticUser ${it.user.username} has the roles ${it.user.roles.joinToString(", ") { role -> role.name }}"
            }?.user ?: userLinkService.registerNewExternalUser(
                oidcProvider,
                oidcUser.name,
                UserCreateCommand(
                    oidcUser.givenName,
                    oidcUser.familyName,
                    oidcUser.email,
                    role,
                    UserSource.OIDC,
                    getLanguage(oidcUser, "fr")
                )
            )

            return ElaasticOidcUser(oidcUser, user)
        } catch (e: RoleException) {
            throw RoleException(
                userRequest,
                e.message ?: "There is a problem with the role of the OIDC user ${oidcUser.name}",
                e
            )
        }
    }

    /**
     * Get the role from the OIDC user
     *
     * We search the role in the realm access of the OIDC user. The realm
     * access is a map containing the roles of the user.
     *
     * We expected a claims like this :
     * ```json
     * "claims": {
     *     [...]
     *     "realm_access": {
     *         [...]
     *         "roles": [
     *             "student"
     *         ]
     *     }
     * }
     * ```
     *
     * An [IllegalStateException] is thrown if:
     * - there is more than one role in the realm access
     * - there is no role in the realm access
     * - the role is not one of the following: "admin", "teacher", "student"
     *
     * @param oidcUser the OIDC user
     * @return the role of the OIDC user
     * @throws IllegalStateException if there is not exactly one role in the
     *    realm access and the role is unknown
     * @see REALM_ACCESS_KEY
     * @see ROLES_KEY
     */
    private fun getRoleFromOidcUser(oidcUser: OidcUser): RoleId {
        val realmRoles: List<String> = (oidcUser.getClaimAsMap(REALM_ACCESS_KEY)[ROLES_KEY] as List<*>)
            .also { logger.info(it.toString()) }
            .filterIsInstance<String>()
            .map { it.lowercase() }
            .alsoThrowIf({ it.isEmpty() }, RoleException::class.java) {
                "There should be at least one role in the realm roles: $it"
            }

        return realmRoles
            .associateWith { keycloakToElaasticRole[it] }
            // We check that there is at least one Elaastic role in the realm roles
            .alsoThrowIf({ it.values.all { role -> role == null } }, RoleException::class.java) {
                "There should be at least one Elaastic role in the realm roles: $realmRoles. " +
                        "The roles are: ${keycloakToElaasticRole.keys.joinToString(", ")}"
            }
            .mapNotNull { it.value }
            .alsoThrowIf({ 1 < it.size }, RoleException::class.java) {
                "There should be exactly one Elaastic role in the realm roles: $realmRoles. Found roles: $it"
            }
            .first()
    }

    /**
     * Return the language of the user.
     *
     * See [OIDC Standard claims documentation](https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims)
     * for more information about the locale.
     *
     * If no locale is found, the default is returned.
     *
     * The default is given in the parameter, or it's the JVM default locale.
     *
     * @param oidcUser The OIDC user to get the locale from
     * @param default The default language to use if no locale is found
     * @see OidcUser.getLocale
     * @see Locale.getDefault
     */
    private fun getLanguage(oidcUser: OidcUser, default: String = Locale.getDefault().language): String {
        val localeClaim = oidcUser.locale

        return if (!localeClaim.isNullOrBlank()) {
            Locale.forLanguageTag(localeClaim).language
        } else {
            default
        }
    }
}

val keycloakToElaasticRole: Map<String, RoleId> = mapOf(
    "student" to RoleId.STUDENT,
    "teacher" to RoleId.TEACHER,
    "admin" to RoleId.ADMIN
)