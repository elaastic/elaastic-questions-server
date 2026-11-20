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

package org.elaastic.sequence.phase

import org.elaastic.sequence.ILearnerSequence
import org.elaastic.sequence.LearnerSequenceService
import org.elaastic.sequence.State
import org.elaastic.sequence.phase.descriptor.PhaseDescriptor
import org.elaastic.sequence.phase.descriptor.SequenceDescriptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

/** @see LearnerPhase */
@Service
class LearnerPhaseService(
    @Autowired val learnerSequenceService: LearnerSequenceService,
    @Autowired val learnerPhaseFactory: LearnerPhaseFactory,
    @Autowired val sequenceDescriptor: SequenceDescriptor,
    @Autowired val ctx: ApplicationContext,
) {

    fun loadPhaseList(learnerSequence: ILearnerSequence) {
        // Get the active phase
        // TODO We use interaction now, but we should get this info from a Phase in the end...
        val activeInteractionForLearner = learnerSequenceService.getActiveInteractionForLearner(
            learnerSequence.learner,
            learnerSequence.sequence
        )

        sequenceDescriptor.phaseDescriptorList.forEachIndexed() { index, phaseDescriptor ->
            learnerSequence.loadPhase(
                buildPhase(
                    learnerSequence,
                    phaseDescriptor,
                    index + 1,
                    active = activeInteractionForLearner?.rank == (index + 1),
                )
            )
        }
    }

    fun buildPhase(
        learnerSequence: ILearnerSequence,
        phaseDescriptor: PhaseDescriptor,
        phaseIndex: Int,
        active: Boolean,
    ): LearnerPhase =
        run {

            val learnerPhase =
                learnerPhaseFactory.build(
                    phaseDescriptor,
                    learnerSequence,
                    phaseIndex = phaseIndex,
                    active = active,
                    state = if (learnerSequence.isNotStarted())
                        State.beforeStart
                    else learnerSequence.sequence.getInteractionAt(phaseIndex).state,
                )

            if (learnerSequence.hasStarted() && learnerPhase.isVisible()) {
                learnerPhase.loadPhaseExecution(
                    ctx.getBean<LearnerPhaseExecutionLoader>(
                        learnerPhase.getLearnerPhaseExecutionLoaderName()
                    ).build(learnerPhase)
                )
            }

            learnerPhase
        }

}