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

package org.elaastic.questions.bootstrap

import org.elaastic.questions.player.phase.LearnerPhaseFactoryResolver
import org.elaastic.questions.player.phase.descriptor.SequenceDescriptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.logging.Logger
import javax.annotation.PostConstruct


@Component
class Bootstrap(
    @Autowired val bootstrapService: BootstrapService,
    @Autowired val sequenceDescriptor: SequenceDescriptor,
    @Autowired val learnerPhaseFactoryResolver: LearnerPhaseFactoryResolver,
) {

    val LOG: Logger = Logger.getLogger(Bootstrap::class.toString())

    @PostConstruct
    fun init() {
        LOG.info("Bootstrapping elaastic-questions in all modes...")
        LOG.info("Migrate to 4.0.0...")
        bootstrapService.migrateTowardVersion400()
        LOG.info("End of Migration to 4.0.0")
        LOG.info("End of the bootstrap")

        // Load phase factories
        sequenceDescriptor.phaseDescriptorList.forEach {
            learnerPhaseFactoryResolver.registerFactory(
                it.type,
                it.type.learnerPhaseFactory
            )
        }

    }

}
