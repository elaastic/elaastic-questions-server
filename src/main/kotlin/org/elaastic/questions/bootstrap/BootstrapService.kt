package org.elaastic.questions.bootstrap

import org.elaastic.questions.directory.Role
import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.DependsOn
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

    fun initializeRoles() {
        
    }

    @Transactional
    fun initializeDevUsers() {
        listOf(
                User(
                        firstName = "Franck",
                        lastName = "Sil",
                        username = "fsil",
                        password = "1234",
                        email = "fsil@elaastic.org"
                ).addRole(roleService.roleTeacher()),
                User(
                        firstName = "Mary",
                        lastName = "Sil",
                        username = "msil",
                        password = "1234",
                        email = "msil@elaastic.org"
                ).addRole(roleService.roleStudent()),
                User(
                        firstName = "Thom",
                        lastName = "Sil",
                        username = "tsil",
                        password = "1234",
                        email = "tsil@elaastic.org"
                ).addRole(roleService.roleStudent()),
                User(
                        firstName = "John",
                        lastName = "Tra",
                        username = "jtra",
                        password = "1234",
                        email = "jtra@elaastic.org"
                ).addRole(roleService.roleStudent()),
                User(
                        firstName = "Erik",
                        lastName = "Erik",
                        username = "erik",
                        password = "1234",
                        email = "erik@elaastic.org"
                ).addRole(roleService.roleStudent())
        ).map {
            userService.findByUsername(it.username) ?: userService.addUser(it)
        }
    }

}