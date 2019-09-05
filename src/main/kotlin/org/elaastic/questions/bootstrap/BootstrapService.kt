package org.elaastic.questions.bootstrap

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserService
import org.elaastic.questions.lti.*
import org.elaastic.questions.terms.Terms
import org.elaastic.questions.terms.TermsContent
import org.elaastic.questions.terms.TermsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.*
import javax.transaction.Transactional


@Service
class BootstrapService(
        @Autowired val userService: UserService,
        @Autowired val roleService: RoleService,
        @Autowired val ltiConsumerRepository: LtiConsumerRepository,
        @Autowired val termsService: TermsService,
        @Autowired val templateEngine: TemplateEngine
) {

    var mailServer: GreenMail? = null

    @Transactional
    fun addTermsIfNotActiveOneAvailable() {
        if (termsService.getActive() == null) {
            val startDate = Date()
            with(Context()) {
                setVariable("startDate", startDate)
                listOf(
                        templateEngine.process("terms/terms_fr", this),
                        templateEngine.process("terms/terms_en", this)
                )
            }.let {
                Terms(startDate).let { terms ->
                    TermsContent(it[0], terms)
                    TermsContent(it[1], terms, "en")
                    termsService.save(terms)
                }
            }
        }
    }


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


}
