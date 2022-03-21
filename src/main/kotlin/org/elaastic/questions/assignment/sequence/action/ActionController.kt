/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.questions.assignment.sequence.action

import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/action")
class ActionController(
        @Autowired val assignmentService: AssignmentService,
        @Autowired val sequenceService: SequenceService,
        @Autowired val messageBuilder: MessageBuilder,
        @Autowired val actionService: ActionService
) {
    @GetMapping("/{id}/saveAction/{subject}/{action}/{obj}")
    fun saveAction(
            authentication: Authentication,
            @PathVariable id: Long,
            @PathVariable subject: String,
            @PathVariable action: String,
            @PathVariable obj: String
    ) {
        val user: User = authentication.principal as User
        if(sequenceService.existsById(id)){
            sequenceService.get(user, id, false).let {
                actionService.create(it, Subject.from(subject), ActionType.from(action), ObjectOfAction.from(obj))
            }
        }
    }

}
