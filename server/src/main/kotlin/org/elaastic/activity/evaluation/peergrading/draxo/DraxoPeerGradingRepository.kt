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

package org.elaastic.questions.assignment.sequence.peergrading.draxo

import org.elaastic.activity.evaluation.peergrading.PeerGrading
import org.elaastic.activity.evaluation.peergrading.PeerGradingRepository
import org.elaastic.activity.evaluation.peergrading.PeerGradingType
import org.elaastic.activity.evaluation.peergrading.draxo.DraxoPeerGrading
import org.elaastic.activity.response.Response
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DraxoPeerGradingRepository : JpaRepository<PeerGrading, Long>, PeerGradingRepository {

    override fun findAllByResponseAndType(response: Response, type: PeerGradingType): List<DraxoPeerGrading>

    fun findByIdAndType(id: Long, type: PeerGradingType): DraxoPeerGrading?

    @Query(
        "SELECT draxo " +
                "FROM DraxoPeerGrading draxo " +
                "JOIN Response r on draxo.response = r " +
                "JOIN Interaction i on r.interaction = i " +
                "WHERE draxo.removedByTeacher = :removed " +
                "AND draxo.reportReasons IS NOT NULL " +
                "AND i = :interaction"
    )
    fun findAllReported(interaction: Interaction, removed: Boolean = false): List<DraxoPeerGrading>

    @Query(
        "SELECT COUNT(draxo) " +
                "FROM DraxoPeerGrading draxo " +
                "JOIN Response r on draxo.response = r " +
                "JOIN Interaction i on r.interaction = i " +
                "WHERE draxo.removedByTeacher = :removed " +
                "AND draxo.reportReasons IS NOT NULL " +
                "AND i = :interaction"
    )
    fun countAllReported(interaction: Interaction, removed: Boolean = false): Int

    override fun findAllByResponseIn(response: List<Response>): List<DraxoPeerGrading>

    @Query(
        "SELECT COUNT(draxo) " +
                "FROM DraxoPeerGrading draxo " +
                "JOIN Response r on draxo.response = r " +
                "JOIN Interaction i on r.interaction = i " +
                "WHERE draxo.removedByTeacher = false " +
                "AND draxo.reportReasons IS NOT NULL " +
                "AND i = :responseSubmissionInteraction " +
                "AND draxo.grader = :grader"
    )
    fun countAllReportedNotRemovedForGrader(responseSubmissionInteraction: Interaction, grader: User): Int
}