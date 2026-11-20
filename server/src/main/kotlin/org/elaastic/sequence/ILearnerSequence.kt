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

package org.elaastic.sequence

import org.elaastic.sequence.interaction.Interaction
import org.elaastic.user.User
import org.elaastic.sequence.phase.LearnerPhase

interface ILearnerSequence : SequenceProgress {

    val learner: User
    val sequence: Sequence

    var activeInteraction: Interaction?

    fun loadPhase(learnerPhase: LearnerPhase)

    fun getPhase(index: Int): LearnerPhase

    val phaseList: Array<LearnerPhase?>
}