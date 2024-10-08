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

package org.elaastic.config

import org.elaastic.questions.player.phase.LearnerPhaseType
import org.elaastic.questions.player.phase.descriptor.PhaseDescriptor
import org.elaastic.questions.player.phase.descriptor.SequenceDescriptor
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling


@Configuration
@EnableCaching
@EnableJpaAuditing
@EnableScheduling
class ApplicationConfig {

    @Bean(name = ["sequenceDescriptor"])
    fun getSequenceDescriptor() = SequenceDescriptor(
        listOf(
            PhaseDescriptor(LearnerPhaseType.RESPONSE),
            PhaseDescriptor(LearnerPhaseType.EVALUATION),
            PhaseDescriptor(LearnerPhaseType.RESULT),
        )
    )
}
