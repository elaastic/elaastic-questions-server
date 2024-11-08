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

package org.elaastic.auth.lti

import org.elaastic.user.RoleService
import org.elaastic.user.User
import org.elaastic.user.UserService
import org.elaastic.user.UserSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import java.util.logging.Logger
import javax.persistence.EntityManager


@Service
class LmsUserAccountCreationService(
    @Autowired val userService: UserService,
    @Autowired val roleService: RoleService,
    @Autowired val entityManager: EntityManager
) {

    internal val logger = Logger.getLogger(LmsUserAccountCreationService::class.java.name)

    /**
     * Create User from lti data
     * @param ltiFirstName first name
     * @param ltiLastName last name
     * @param ltiEmail email
     * @param ltiRole the role to be assigned to the user
     * @return the created user
     */
    fun createUserFromLtiData(ltiUser: LtiUser): User {
        User(
            firstName = ltiUser.firstName,
            lastName = ltiUser.lastName,
            username = userService.generateUsername(ltiUser.firstName, ltiUser.lastName),
            plainTextPassword = userService.generatePassword(),
            email = ltiUser.email,
        source = UserSource.LMS,
    ).addRole(ltiUser.role).let {
            if (it.email == null) {
                it.owner = userService.getDefaultAdminUser()
            }
            userService.addUser(
                    it,
                    "fr",
                    checkEmailAccount = false,
                    enable = true,
                    addUserConsent = true)
            return it
        }
    }

}
