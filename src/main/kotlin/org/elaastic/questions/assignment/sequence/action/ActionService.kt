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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class ActionService(
        @Autowired val actionRepository: ActionRepository
) {

    fun create(sequence: Sequence,
               subject: Subject,
               actionType: ActionType,
               obj: ObjectOfAction): Action =
            Action(sequence = sequence,
                    subject = subject,
                    actionType = actionType,
                    obj = obj
            ).let(actionRepository::save)

    fun findById(id: Long): Action =
            actionRepository.findById(id).get()

    fun save(action: Action){
        actionRepository.save(action)
    }

}