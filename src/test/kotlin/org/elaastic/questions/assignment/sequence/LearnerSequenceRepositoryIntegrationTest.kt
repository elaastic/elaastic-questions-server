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

package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import javax.persistence.EntityManager



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class LearnerSequenceRepositoryIntegrationTest(
        @Autowired val learnerSequenceRepository: LearnerSequenceRepository,
        @Autowired val testingService: TestingService,
        @Autowired val entityManager: EntityManager
) {

    @Test
    fun `save a valid learner sequence`() {
        val learner = testingService.getAnyUser()
        val sequence = testingService.getAnySequence()
        val interaction = testingService.getAnyInteraction()
        LearnerSequence(
                learner = learner,
                sequence = sequence,
                activeInteraction = interaction
        )
                .tWhen {
                    learnerSequenceRepository.saveAndFlush(it)
                    entityManager.refresh(it)
                    it
                }
                .tThen {
                    assertThat(it.id, notNullValue())
                    assertThat(it.version, equalTo(0L))
                    assertThat(it.dateCreated, notNullValue())
                    assertThat(it.lastUpdated, notNullValue())
                    assertThat(it.learner, equalTo(learner))
                    assertThat(it.sequence, equalTo(sequence))
                    assertThat(it.activeInteraction, equalTo(interaction))
                }
    }

}
