package org.elaastic.questions.directory

import org.elaastic.questions.assignment.AssignmentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author John Tranier
 */
@RestController
class DebugController( // TODO Remove
        @Autowired val assignmentService: AssignmentService
) {

    @RequestMapping("/debug/fetch")
    fun fetch(): String {
        return assignmentService.get(1, true).toString()
    }

    @RequestMapping("/debug/no-fetch")
    fun noFetch(): String {
        return assignmentService.get(1, false).toString()
    }
}