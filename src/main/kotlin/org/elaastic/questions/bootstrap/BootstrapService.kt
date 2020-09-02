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

package org.elaastic.questions.bootstrap

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserService
import org.elaastic.questions.lti.*
import org.elaastic.questions.subject.SubjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional


@Service
class BootstrapService(
        @Autowired val userService: UserService,
        @Autowired val roleService: RoleService,
        @Autowired val ltiConsumerRepository: LtiConsumerRepository,
        @Autowired val subjectService: SubjectService
) {

    var mailServer: GreenMail? = null

    @Transactional
    fun initializeDevUsers() {
        listOf(
                User(
                        firstName = "Franck",
                        lastName = "Sil",
                        username = "fsil",
                        plainTextPassword = "1234",
                        email = "fsil@elaastic.org"
                ).addRole(roleService.roleTeacher()),
                User(
                        firstName = "Albert",
                        lastName = "Ein",
                        username = "aein",
                        plainTextPassword = "1234",
                        email = "aein@elaastic.org"
                ).addRole(roleService.roleTeacher()),
                User(
                        firstName = "Mary",
                        lastName = "Sil",
                        username = "msil",
                        plainTextPassword = "1234",
                        email = "msil@elaastic.org"
                ).addRole(roleService.roleStudent()),
                User(
                        firstName = "Thom",
                        lastName = "Sil",
                        username = "tsil",
                        plainTextPassword = "1234",
                        email = "tsil@elaastic.org"
                ).addRole(roleService.roleStudent()),
                User(
                        firstName = "John",
                        lastName = "Tra",
                        username = "jtra",
                        plainTextPassword = "1234",
                        email = "jtra@elaastic.org"
                ).addRole(roleService.roleStudent()),
                User(
                        firstName = "Erik",
                        lastName = "Erik",
                        username = "erik",
                        plainTextPassword = "1234",
                        email = "erik@elaastic.org"
                ).addRole(roleService.roleStudent())
        ).map {
            userService.findByUsername(it.username) ?: userService.addUser(it)
        }
    }

    fun startDevLocalSmtpServer() {
        mailServer = GreenMail(ServerSetup(10025, "localhost", "smtp"))
        try {
            with(mailServer!!) {
                setUser("elaastic", "elaastic")
                start()
            }
        } catch (e: Exception) {
        }
    }

    fun stopDevLocalSmtpServer() {
        try {
            mailServer?.stop()
        } catch (e: Exception) {
        }
    }

    fun initializeDevLtiObjects() {

        LtiConsumer( // a lti consumer aka an LMS
                consumerName = "Moodle",
                secret = "secret pass",
                key = "abcd1234").let {
            it.enableFrom = Date()
            if (!ltiConsumerRepository.existsById(it.key)) {
                ltiConsumerRepository.saveAndFlush(it)
            }
            it
        }

    }

    fun migrateTowardVersion400() {
        subjectService.migrateAssignmentsTowardSubjects()
    }

}
