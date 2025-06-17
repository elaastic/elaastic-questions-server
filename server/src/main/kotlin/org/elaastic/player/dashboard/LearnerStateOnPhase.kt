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

package org.elaastic.player.dashboard

import org.elaastic.player.dashboard.LearnerStateOnPhase.*


/**
 * Enum defining states of a learner on a phase
 *
 * @property ACTIVITY_NOT_TERMINATED the learner has not terminated the activity
 * @property ACTIVITY_TERMINATED the learner has terminated the activity
 * @property WAITING the learner is waiting for the next phase
 * @property NONE the selected phase isn't enabled, so the learner can't access it
 */
enum class LearnerStateOnPhase {
    /** The learner has not terminated the activity. */
    ACTIVITY_NOT_TERMINATED,

    /** The learner has terminated the activity. */
    ACTIVITY_TERMINATED,

    /** The learner is waiting for the next phase. */
    WAITING,

    /**
     * The selected phase isn't enabled, so the learner can't access it.
     *
     * @see DashboardPhaseState.NONE
     * @see org.elaastic.player.steps.StepsModel.PhaseState.NONE
     */
    NONE
}