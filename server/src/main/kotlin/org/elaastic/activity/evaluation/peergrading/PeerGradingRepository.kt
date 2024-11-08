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

package org.elaastic.activity.evaluation.peergrading

import org.elaastic.activity.response.Response
import org.elaastic.moderation.ReportCandidateRepository
import org.elaastic.activity.evaluation.peergrading.draxo.DraxoPeerGrading
import org.elaastic.user.User
import org.springframework.data.jpa.repository.JpaRepository


interface PeerGradingRepository : JpaRepository<PeerGrading, Long>, ReportCandidateRepository {
    fun findByGraderAndResponse(grader: User, response: Response): PeerGrading?

    fun findAllByResponseIn(response: List<Response>): List<PeerGrading>

    fun findAllByResponseAndType(response: Response, type: PeerGradingType): List<PeerGrading>
}
