package org.elaastic.questions.directory

import org.junit.jupiter.api.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.annotation.EnableCaching
import javax.transaction.Transactional



@SpringBootTest
@EnableCaching
@Transactional
internal class RoleServiceIntegrationTest(
        @Autowired val roleService: RoleService
) {

    @Test
    fun loadAllRole() {
        // Expect :
        assertThat(roleService.roleStudent().name, equalTo(Role.RoleId.STUDENT.roleName))
        assertThat(roleService.roleTeacher().name, equalTo(Role.RoleId.TEACHER.roleName))
        assertThat(roleService.roleAdmin().name, equalTo(Role.RoleId.ADMIN.roleName))
    }

}