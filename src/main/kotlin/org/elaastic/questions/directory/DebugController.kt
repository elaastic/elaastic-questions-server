package org.elaastic.questions.directory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author John Tranier
 */
@RestController
class DebugController( // TODO Remove
        @Autowired val roleService: RoleService
) {

    @RequestMapping("/debug")
    fun debug(): String {
        return roleService.roleTeacher().name
    }
}