package org.elaastic.questions.directory

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*


/**
 * @author John Tranier
 */
@SpringBootTest
@Transactional
internal class UserServiceIntegrationTest(
        @Autowired val userService: UserService,
        @Autowired val roleService: RoleService
) {

    @Test
    fun addUser() {
        // Given:
        val user = userService.addUser(
                User(
                        username = "foo",
                        firstName = "f",
                        lastName = "oo",
                        password = "1234",
                        email = "foo@elaastic.org"

                ).addRole(roleService.roleStudent())
        )

        // Expect:
        assertThat(user?.id, notNullValue())
        assertThat(user?.password, notNullValue())
        assertThat(user?.password, not(equalTo("1234")))

    }
}