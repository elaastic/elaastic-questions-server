/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.elaastic.analytics.lrs

import org.elaastic.user.PrincipalUserResolver

import org.elaastic.common.web.MessageBuilder
import org.elaastic.assignment.AssignmentService
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
        val user: User = (authentication.principal as PrincipalUserResolver).elaasticUser
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