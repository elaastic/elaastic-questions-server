package org.elaastic.analytics.lrs

import org.elaastic.assignment.AssignmentService
import org.elaastic.user.PrincipalUserResolver
import org.elaastic.common.web.MessageBuilder
import org.elaastic.sequence.SequenceService
import org.elaastic.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/action")
class EventLogController(
    @Autowired val assignmentService: AssignmentService,
    @Autowired val sequenceService: SequenceService,
    @Autowired val messageBuilder: MessageBuilder,
    @Autowired val eventLogService: EventLogService
) {

    @GetMapping("/{sequenceId}/saveAction/{action}/{obj}")
    fun saveAction(
        authentication: Authentication,
        @PathVariable sequenceId: Long,
        @PathVariable action: String,
        @PathVariable obj: String
    ) {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser
        if(sequenceService.existsById(sequenceId)){
                    sequenceService.get(sequenceId, false).let {
                        eventLogService.create(sequence = it,
                                user = user,
                                action = Action.from(action),
                                obj = ObjectOfAction.from(obj)
                        )
            }
        }
    }

}