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

package org.elaastic.questions.assignment.sequence.interaction.response

import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.results.AttemptNum
import org.elaastic.questions.directory.User
import org.springframework.data.jpa.repository.JpaRepository


interface ResponseRepository : JpaRepository<Response, Long> {

    fun getAllByIdIn(ids: List<Long>): List<Response>

    fun findAllByInteraction(interaction: Interaction): List<Response>

    fun findAllByInteractionAndAttempt(interaction: Interaction, attempt: Int = 1): List<Response>

    fun findByInteractionAndAttemptAndLearner(interaction: Interaction, attempt: AttemptNum, learner: User): Response?

    fun findAllByInteractionAndAttemptOrderByMeanGradeDesc(interaction: Interaction, attempt: Int = 1): List<Response>

    // Correct responses
    fun findAllByInteractionAndAttemptAndScoreOrderByScoreDesc(interaction: Interaction,
                                                               attempt: AttemptNum = 1,
                                                               score: Float = 100f)

    // Incorrect responses
    fun findAllByInteractionAndAttemptAndScoreLessThanOrderByScoreDesc(interaction: Interaction,
                                                                       attempt: AttemptNum = 1,
                                                                       score: Float = 100f)

    fun countByLearnerAndInteractionAndAttempt(learner: User,
                                               interaction: Interaction,
                                               attempt: AttemptNum): Int
}
