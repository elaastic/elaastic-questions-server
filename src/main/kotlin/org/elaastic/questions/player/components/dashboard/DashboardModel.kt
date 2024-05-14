/*
 *
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
 *
 */

package org.elaastic.questions.player.components.dashboard

import org.elaastic.questions.assignment.LearnerAssignment
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.interaction.response.Response

class DashboardModel (
    var sequence: Sequence,
    var attendees: List<LearnerAssignment>,
    var attendeesCount: Int,
    var attendeesResponses: Map<Long, List<Response>>,
    var responsePhaseAttendees: List<LearnerAssignment>,
    var evaluationPhaseAttendees: List<LearnerAssignment>,
    var openedPane: String,
    var previousSequence: Long?,
    var nextSequence: Long?
)