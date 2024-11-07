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

package org.elaastic.sequence.interaction

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.sequence.interaction.response.Response
import org.elaastic.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query


interface InteractionRepository : JpaRepository<Interaction, Long> {

    fun findAllBySequence(sequence: Sequence): List<Interaction>

    @Query("SELECT DISTINCT r " +
           "FROM Interaction i " +
           "INNER JOIN Response r " +
           "ON i = r.interaction " +
           "WHERE i.sequence = :sequence " +
           "AND i.interactionType = 'ResponseSubmission'")
    fun findAllResponsesBySequenceAndType(sequence: Sequence): List<Response>

    @Query("SELECT DISTINCT r " +
           "FROM Interaction i " +
           "INNER JOIN Response r " +
           "ON i = r.interaction " +
           "WHERE i.owner = :owner " +
           "AND i.sequence = :sequence " +
           "AND i.interactionType = 'ResponseSubmission'")
    fun findResponseByOwnerAndSequenceAndType(owner: User,
                                              sequence: Sequence): Response?
}
