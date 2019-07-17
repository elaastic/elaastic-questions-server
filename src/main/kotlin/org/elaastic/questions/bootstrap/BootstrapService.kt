package org.elaastic.questions.bootstrap

import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @author John Tranier
 */
@Service
class BootstrapService(
        @Autowired val userService: UserService
) {

    fun initializeDevUsers() {
        listOf(
                User(
                        firstName = "Franck",
                        lastName = "Sil",
                        username = "fsil",
                        password = "1234",
                        email = "fsil@elaastic.org"
                ),
                User(
                        firstName = "Mary",
                        lastName = "Sil",
                        username = "msil",
                        password = "1234",
                        email = "msil@elaastic.org"
                ),
                User(
                        firstName = "Thom",
                        lastName = "Sil",
                        username = "tsil",
                        password = "1234",
                        email = "tsil@elaastic.org"
                ),
                User(
                        firstName = "John",
                        lastName = "Tra",
                        username = "jtra",
                        password = "1234",
                        email = "jtra@elaastic.org"
                ),
                User(
                        firstName = "Erik",
                        lastName = "Erik",
                        username = "erik",
                        password = "1234",
                        email = "erik@elaastic.org"
                )
        ).map {
            userService.findByUsername(it.username) ?: userService.addUser(it)
        }
    }

}