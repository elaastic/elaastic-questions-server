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

package org.elaastic.sequence

import io.mockk.mockk
import org.elaastic.material.instructional.statement.Statement
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.sequence.interaction.InteractionType
import org.elaastic.user.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SequenceTest {
    @Test
    fun getNextInteraction() {
        // Given a sequence with three interactions of different types
        val user = mockk<User>()
        val sequence = Sequence(user, mockk<Statement>())
        val interactions = listOf(
            Interaction(InteractionType.ResponseSubmission, 1, owner = user, sequence = sequence),
            Interaction(InteractionType.Evaluation, 2, owner = user, sequence = sequence),
            Interaction(InteractionType.Read, 3, owner = user, sequence = sequence),
        )
        sequence.interactions =
            interactions.shuffled().associateBy { it.interactionType } as MutableMap<InteractionType, Interaction>

        // When we get the next interaction for each interaction
        // Then the next interaction is returned correctly
        assertEquals(interactions[1], sequence.getNextInteraction(interactions[0]))
        assertEquals(interactions[2], sequence.getNextInteraction(interactions[1]))
        assertThrows<IllegalStateException> {
            sequence.getNextInteraction(interactions[2])
        }
    }
}