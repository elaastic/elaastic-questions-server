package org.elaastic.questions.bootstrap

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserService
import org.elaastic.questions.lti.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional


@Service
class BootstrapService(
        @Autowired val userService: UserService,
        @Autowired val roleService: RoleService,
        @Autowired val ltiConsumerRepository: LtiConsumerRepository,
        @Autowired val ltiContextRepository: LtiContextRepository,
        @Autowired val ltiUserRepository: LtiUserRepository
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
        }catch(e: Exception) {}
    }

    fun stopDevLocalSmtpServer() {
        try {
            mailServer?.stop()
        } catch(e:Exception) {}
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
        }.let {
            LtiContext.LtiContextId( // a lti context aka a course from the LMS
                    it.key,
                    "The course").let { contextId ->
                // and building a valid context
                LtiContext(
                        contextId,
                        contextId.lmsActivityId,
                        "The course title",
                        it,
                        "course id"
                ).let{ context ->
                    if(!ltiContextRepository.existsById(contextId)) {
                        ltiContextRepository.saveAndFlush(context)
                    }
                    context
                }
            }
        }.let {
            LtiUser.LtiUserId(
                    it.lms.key,
                    it.lmsActivityId,
                    "bobdeniro"
            ).let {ltiUserId ->
                LtiUser(
                        ltiUserId,
                        it.lms,
                        it,
                        ltiUserId.lmsUserId
                ).let { ltiUser ->
                    if (!ltiUserRepository.existsById(ltiUserId)) {
                        ltiUserRepository.saveAndFlush(ltiUser)
                    }
                }
            }
        }

    }


}
