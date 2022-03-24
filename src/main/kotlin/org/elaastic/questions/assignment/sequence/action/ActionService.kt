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

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class ActionService(
        @Autowired val actionRepository: ActionRepository
) {

    fun create(sequence: Sequence,
               subject: Subject? = null,
               user: User? = null,
               actionType: ActionType,
               obj: ObjectOfAction): Action =
            Action(sequence = sequence,
                    user = user ?: sequence.owner,
                    subject = subject ?: if(sequence.owner != user) Subject.LEARNER else Subject.TEACHER,
                    actionType = actionType,
                    obj = obj
            ).let(actionRepository::save)

    fun findById(id: Long): Action =
            actionRepository.findById(id).get()

    fun save(action: Action){
        actionRepository.save(action)
    }

    fun saveActionsAfterClosingConfigurePopup(sequence : Sequence){
        create(sequence, Subject.TEACHER, sequence.owner, ActionType.CLOSE, ObjectOfAction.CONFIGURE_POPUP)
        create(sequence, Subject.TEACHER, sequence.owner, ActionType.START, ObjectOfAction.SEQUENCE)
        create(sequence, Subject.TEACHER, sequence.owner, ActionType.START, ObjectOfAction.PHASE_1)
    }

    fun stopSequence(sequence : Sequence){
        create(sequence, Subject.TEACHER, sequence.owner, ActionType.STOP, ObjectOfAction.SEQUENCE)
    }

    fun reopenSequence(sequence : Sequence){
        create(sequence, Subject.TEACHER, sequence.owner, ActionType.RESTART, ObjectOfAction.SEQUENCE)
    }

    fun startPhase(sequence : Sequence, phaseNumber: Int){
        create(sequence, Subject.TEACHER, sequence.owner, ActionType.START, ObjectOfAction.from("phase_$phaseNumber"))
    }

    fun stopPhase(sequence : Sequence, phaseNumber: Int){
        create(sequence, Subject.TEACHER, sequence.owner, ActionType.STOP, ObjectOfAction.from("phase_$phaseNumber"))
    }

    fun skipPhase(sequence : Sequence, phaseNumber: Int){
        create(sequence, Subject.TEACHER, sequence.owner, ActionType.SKIP, ObjectOfAction.from("phase_$phaseNumber"))
    }

    fun restartPhase(sequence : Sequence, phaseNumber: Int){
        create(sequence, Subject.TEACHER, sequence.owner, ActionType.RESTART, ObjectOfAction.from("phase_$phaseNumber"))
    }

    fun publishResults(sequence: Sequence) {
        create(sequence, Subject.TEACHER, sequence.owner, ActionType.PUBLISH, ObjectOfAction.RESULT)
    }

    fun unpublishResults(sequence: Sequence) {
        create(sequence, Subject.TEACHER, sequence.owner, ActionType.UNPUBLISH, ObjectOfAction.RESULT)
    }

    fun refreshResults(sequence: Sequence) {
        create(sequence, Subject.TEACHER, sequence.owner, ActionType.UPDATE, ObjectOfAction.RESULT)
    }
}