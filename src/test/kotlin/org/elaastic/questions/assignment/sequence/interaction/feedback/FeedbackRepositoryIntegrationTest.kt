package org.elaastic.questions.assignment.sequence.interaction.feedback

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

import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseRepository
import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import java.math.BigDecimal


@SpringBootTest
@Transactional
class FeedbackRepositoryIntegrationTest(
        @Autowired val feedbackRepository: FeedbackRepository,
        @Autowired val testingService: TestingService,
        @Autowired val entityManager: EntityManager
) {
    @Test
    fun `save a valid sequence feedback`() {
        val learner = testingService.getAnyUser()
        val interaction = testingService.getAnyInteraction()
        tGiven("a sequence feedback") {
            Feedback(
                    learner = learner,
                    sequence = interaction.sequence,
                    rating = 5,
                    explanation = "an explanation"
            )
        }.tWhen("saved in DB and refreshed") {
            feedbackRepository.saveAndFlush(it)
            entityManager.refresh(it)
            it
        }.tThen("every field is set and valid") {
            assertThat(it.id, notNullValue())
            assertThat(it.learner, equalTo(learner))
            assertThat(it.sequence, equalTo(interaction.sequence))
            assertThat(it.rating, equalTo(5))
            assertThat(it.explanation, equalTo("an explanation"))
            assertThat(it.version, equalTo(0L))
            assertThat(it.dateCreated, notNullValue())
        }
    }

    @Test
    fun `save a sequence feedback and retrieve it`() {
        val learner = testingService.getAnyUser()
        val interaction = testingService.getAnyInteraction()
        tGiven("a valid sequence feedback in DB") {
            Feedback(
                    learner = learner,
                    sequence = interaction.sequence,
                    rating = 5,
                    explanation = "an explanation"
            ).let {
                feedbackRepository.saveAndFlush(it)
            }
        }.tWhen("we try to retrieve it from DB") {
            feedbackRepository.findByLearnerAndSequence(learner, interaction.sequence)
        }.tThen("the sequence feedback is not null") {
            assertThat(it, notNullValue())
        }
    }

    @Test
    fun `try to retrieve an absent sequence feedback`() {
        val learner = testingService.getAnyUser()
        val interaction = testingService.getAnyInteraction()
        tGiven("no sequence feedback in DB") {
        }.tWhen("we try to retrieve it from DB") {
            feedbackRepository.findByLearnerAndSequence(learner, interaction.sequence)
        }.tThen("the sequence feedback is not null") {
            assertThat(it, nullValue())
        }
    }
}
