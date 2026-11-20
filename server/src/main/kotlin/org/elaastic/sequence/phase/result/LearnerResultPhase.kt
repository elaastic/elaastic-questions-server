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

package org.elaastic.sequence.phase.result

import org.elaastic.player.results.ResultsModelFactory
import org.elaastic.sequence.ILearnerSequence
import org.elaastic.sequence.State
import org.elaastic.sequence.phase.LearnerPhase
import org.elaastic.sequence.phase.LearnerPhaseExecution
import org.elaastic.sequence.phase.LearnerPhaseType
import org.elaastic.sequence.phase.PhaseTemplate

class LearnerResultPhase(
    learnerSequence: ILearnerSequence,
    index: Int,
    active: Boolean,
    state: State,
) : LearnerPhase(
    learnerSequence,
    index,
    active,
    state,
    PhaseTemplate(
        "${PhaseTemplate.TEMPLATE_PACKAGE}/result/_learner-result-phase.html",
        "resultPhase"
    )
) {
    override val phaseType = LearnerPhaseType.RESULT
    override var learnerPhaseExecution: LearnerResultPhaseExecution? = null

    override fun loadPhaseExecution(learnerPhaseExecution: LearnerPhaseExecution) {
        if (learnerPhaseExecution is LearnerResultPhaseExecution)
            this.learnerPhaseExecution = learnerPhaseExecution
        else throw IllegalArgumentException()
    }

    override fun getViewModel(): LearnerResultPhaseViewModel {

        val idFirstResponse = try {
            learnerPhaseExecution?.responseSet?.get(1)?.first()?.id
        } catch (e: Exception) {
            null
        }
        val idSecondResponse = try {
            learnerPhaseExecution?.responseSet?.get(2)?.first()?.id
        } catch (e: Exception) {
            null
        }

        val explanationHasChatGPTEvaluationMap: Map<Long, Boolean> =
            listOfNotNull(idFirstResponse, idSecondResponse)
                //TODO Fill explanationHasChatGPTEvaluationMap with the real data
                .associate { (it to false) }


        return LearnerResultPhaseViewModel(
            learnerSequence.sequence.resultsArePublished,
            learnerPhaseExecution!!.myResultsModel,
            ResultsModelFactory.build(
                teacher = false,
                sequence = this.learnerSequence.sequence,
                responseSet = learnerPhaseExecution!!.responseSet,
                userCanRefreshResults = learnerPhaseExecution!!.userCanRefreshResults,
                featureManager = learnerPhaseExecution!!.featureManager,
                messageBuilder = learnerPhaseExecution!!.messageBuilder,
                explanationHasChatGPTEvaluationMap = explanationHasChatGPTEvaluationMap
            ),
            learnerPhaseExecution!!.myChatGptEvaluationModel,
        )
    }

    // Note JT : results are displayed even when the sequence is closed
    override fun isVisible() = this.active

}