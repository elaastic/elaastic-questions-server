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

package org.elaastic.sequence.phase.evaluation

import org.elaastic.sequence.phase.descriptor.PhaseConfig

/**
 * Enumeration of the different configuration for the evaluation phase.
 *
 * @property ALL_AT_ONCE Implementation that shows all the alternative peer
 *    answers to evaluate
 * @property DRAXO ONE_BY_ONE with DRAXO Form evaluation
 */
enum class EvaluationPhaseConfig : PhaseConfig {
    /** Implementation that shows all the alternative peer answers to evaluate */
    ALL_AT_ONCE,

    /** ONE_BY_ONE with DRAXO Form evaluation */
    DRAXO;
}