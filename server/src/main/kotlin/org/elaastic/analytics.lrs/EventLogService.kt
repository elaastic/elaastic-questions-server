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

package org.elaastic.analytics.lrs

import org.elaastic.questions.assignment.sequence.ILearnerSequence
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.user.Role
import org.elaastic.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class EventLogService(
        @Autowired val eventLogRepository: EventLogRepository
) {

    fun create(sequence: Sequence,
               user: User,
               action: Action,
               obj: ObjectOfAction,
               userAgent : String? = null): EventLog =
            EventLog(sequence = sequence,
                    user = user,
                    role = if(user != sequence.owner) Role.RoleId.STUDENT else Role.RoleId.TEACHER,
                    action = action,
                    obj = obj,
                    userAgent = userAgent
            ).let(eventLogRepository::save)

    fun findById(id: Long): EventLog =
            eventLogRepository.findById(id).get()

    fun save(eventLog: EventLog){
        eventLogRepository.save(eventLog)
    }

    fun saveActionsAfterClosingConfigurePopup(sequence : Sequence){
        create(sequence, sequence.owner, Action.CLOSE, ObjectOfAction.CONFIGURE_POPUP)
        create(sequence, sequence.owner, Action.START, ObjectOfAction.SEQUENCE)
        create(sequence, sequence.owner, Action.START, ObjectOfAction.PHASE_1)
    }

    fun stopSequence(sequence : Sequence){
        create(sequence, sequence.owner, Action.STOP, ObjectOfAction.SEQUENCE)
    }

    fun reopenSequence(sequence : Sequence){
        create(sequence, sequence.owner, Action.RESTART, ObjectOfAction.SEQUENCE)
    }

    fun startPhase(sequence : Sequence, phaseNumber: Int){
        create(sequence, sequence.owner, Action.START, ObjectOfAction.from("phase_$phaseNumber"))
    }

    fun stopPhase(sequence : Sequence, phaseNumber: Int){
        create(sequence, sequence.owner, Action.STOP, ObjectOfAction.from("phase_$phaseNumber"))
    }

    fun skipPhase(sequence : Sequence, phaseNumber: Int){
        create(sequence, sequence.owner, Action.SKIP, ObjectOfAction.from("phase_$phaseNumber"))
    }

    fun restartPhase(sequence : Sequence, phaseNumber: Int){
        create(sequence, sequence.owner, Action.RESTART, ObjectOfAction.from("phase_$phaseNumber"))
    }

    fun publishResults(sequence: Sequence) {
        create(sequence, sequence.owner, Action.PUBLISH, ObjectOfAction.RESULT)
    }

    fun unpublishResults(sequence: Sequence) {
        create(sequence, sequence.owner, Action.UNPUBLISH, ObjectOfAction.RESULT)
    }

    fun refreshResults(sequence: Sequence) {
        create(sequence, sequence.owner, Action.UPDATE, ObjectOfAction.RESULT)
    }

    fun consultResults(sequence: Sequence, user: User, userAgent: String?) {
        create(sequence, user, Action.CONSULT, ObjectOfAction.RESULT, userAgent)
    }

    fun consultPlayer(sequence: Sequence, user: User, learnerSequence: ILearnerSequence, userAgent: String?) {
        if (learnerSequence.sequence.resultsArePublished)
            if (learnerSequence.sequence.executionIsFaceToFace())
                consultResults(sequence, user, userAgent)
            else
                if (learnerSequence.activeInteraction?.isRead() == true)
                    consultResults(sequence, user, userAgent)
    }
}