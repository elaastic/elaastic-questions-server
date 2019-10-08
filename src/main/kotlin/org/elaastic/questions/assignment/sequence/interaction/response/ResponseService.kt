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

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.results.AttemptNum
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Service
@Transactional
class ResponseService(
        @Autowired val responseRepository: ResponseRepository,
        @Autowired val entityManager: EntityManager
) {

    fun findAll(sequence: Sequence) =
            findAll(sequence.getResponseSubmissionInteraction())

    fun findAll(interaction: Interaction): ResponseSet =
            ResponseSet(
                    responseRepository.findAllByInteraction(interaction)
            )

    // TODO Need to fetch users with responses
    fun findAllChoiceResponse(interaction: Interaction, correct: Boolean, attempt: AttemptNum = 1) {
        if (correct)
            responseRepository.findAllByInteractionAndAttemptAndScoreOrderByScoreDesc(
                    interaction,
                    attempt
            )
        else responseRepository.findAllByInteractionAndAttemptAndScoreLessThanOrderByScoreDesc(
                interaction,
                attempt
        )
    }

    fun findAllOpenResponse(interaction: Interaction, attemptNum: AttemptNum = 1): List<Response> =
            responseRepository.findAllByInteractionAndAttemptOrderByMeanGradeDesc(interaction, attemptNum)


    fun updateMeanGrade(response: Response) {
        val meanGrade = entityManager.createQuery("select avg(pg.grade) from PeerGrading pg where pg.response = :response and pg.grade <> -1")
                .setParameter("response", response)
                .singleResult as Float

        response.meanGrade = meanGrade
        responseRepository.save(response)
    }
}
