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

package org.elaastic.player.steps

import org.elaastic.player.dashboard.DashboardPhaseState
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class StepsModelTest {

    @Test
    fun `test sequenceStarted with no steps`() {
        val stepsModel = StepsModel(
            responseSubmission = null,
            evaluation = null,
            read = null,
            showStatistics = false,
            phase2Skipped = false
        )
        assertFalse(stepsModel.sequenceStarted)
    }

    @Test
    fun `test sequenceStarted with steps`() {
        val stepsModel = StepsModel(
            responseSubmission = PhaseStep(StepsModel.PhaseState.ACTIVE, 1),
            evaluation = null,
            read = null,
            showStatistics = true,
            phase2Skipped = false
        )
        assertTrue(stepsModel.sequenceStarted)
    }

    @Test
    fun `test phaseState conversion to DashboardPhaseState`() {
        val activeState = StepsModel.PhaseState.ACTIVE
        val completedState = StepsModel.PhaseState.COMPLETED
        val disabledState = StepsModel.PhaseState.DISABLED
        val noneState = StepsModel.PhaseState.NONE

        assertEquals(DashboardPhaseState.IN_PROGRESS, activeState.getDashboardState())
        assertEquals(DashboardPhaseState.STOPPED, completedState.getDashboardState())
        assertEquals(DashboardPhaseState.NOT_STARTED, disabledState.getDashboardState())
        assertEquals(DashboardPhaseState.NONE, noneState.getDashboardState())
    }
}