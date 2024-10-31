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

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.text.Normalizer
import javax.transaction.Transactional

@Service
class AnonymousUserService(
    @Autowired val userService: UserService,
    @Autowired val roleService: RoleService
) {
    companion object {
        const val ANONYMOUS_NAME = "Anonymous"
        const val ANONYMOUS_PWD = "Anonymous"
    }

    @Transactional
    fun authenticateAnonymousUser(nickname: String): Authentication {
        return UsernamePasswordAuthenticationToken(
            addAnonymousUser(nickname),
            null,
            arrayListOf(SimpleGrantedAuthority(Role.RoleId.STUDENT.roleName))
        )
    }

    /**
     * Create a user entity for an anonymous user
     * Note : the account must be locked to prevent authentication with username / password
     */
    private fun addAnonymousUser(nickname: String): User {
        userService.addUser(
            User( 
                firstName = nickname,
                lastName = ANONYMOUS_NAME,
                username = "${normalizeNickname(nickname)}-${System.currentTimeMillis()}",
                plainTextPassword = ANONYMOUS_PWD,
                email = null,
                source = UserSource.ANONYMOUS,
            ).addRole(roleService.roleStudent())

        ).let {
            it.accountLocked = true
            return it
        }
    }

    private fun normalizeNickname(nickname: String) =
        Normalizer.normalize(
            nickname.substring(0, 15.coerceAtMost(nickname.length - 1)),
            Normalizer.Form.NFD
        ).replace("[^a-zA-Z0-9]".toRegex(), "")

}