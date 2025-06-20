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

import org.elaastic.player.dashboard.DashboardPhaseState.COMPLETED


/**
 * Enum defining states of a phase
 *
 * @property NOT_STARTED the phase has not started
 * @property IN_PROGRESS the phase is in progress
 * @property STOPPED the phase has been stopped
 * @property COMPLETED the phase has been completed
 * @property NONE the phase is not enabled
 */
enum class DashboardPhaseState {
    /**
     * The phase has not started
     *
     * Applicable to all phases. During phase 3 (evaluation), the phase is not started when the results are not
     * displayed yet
     */
    NOT_STARTED,

    /**
     * The phase is in progress
     *
     * Applicable to all phases. During phase 3 (evaluation), the phase is in progress when the results are being
     * displayed
     */
    IN_PROGRESS,

    /**
     * The phase has been stopped
     *
     * Only applicable to phase 1 and 2.
     */
    STOPPED,

    /**
     * The phase has been completed
     *
     * Applicable to all phases.
     */
    COMPLETED,

    /** The phase is not enabled */
    NONE
}