package org.elaastic.questions.bootstrap

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

/**
 * @author John Tranier
 */
@Service
class BootstrapService(
        @Autowired val userService: UserService,
        @Autowired val roleService: RoleService
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

}
