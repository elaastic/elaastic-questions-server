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
package org.elaastic.questions.player.components.explanationViewer

import org.elaastic.questions.assignment.choice.ChoiceSpecification
import org.elaastic.questions.assignment.choice.ExclusiveChoiceSpecification
import org.elaastic.questions.assignment.choice.MultipleChoiceSpecification
import org.elaastic.questions.assignment.sequence.interaction.response.Response

class ChoiceExplanationStore(choiceSpecification: ChoiceSpecification) : ExplanationStore,
        HashMap<ResponseData, MutableList<ExplanationData>>() {

    init {
        when (choiceSpecification) {
            is ExclusiveChoiceSpecification ->

                // Create all the entries for exclusive choice
                repeat(choiceSpecification.nbCandidateItem) {
                    this.put(
                            ResponseData(
                                    choices = listOf(it + 1),
                                    correct = (it + 1) == choiceSpecification.expectedChoice.index,
                                    score = if ((it + 1) == choiceSpecification.expectedChoice.index) 100 else 0
                            ),
                            mutableListOf()
                    )
                }

            is MultipleChoiceSpecification ->

                // Create only the correct entry for multiple choices
                this.put(
                        ResponseData(
                                choices = choiceSpecification.expectedChoiceList.map { it.index },
                                correct = true,
                                score = 100
                        ),
                        mutableListOf()
                )

            else -> error("Unsupported type of ChoiceSpecification: ${choiceSpecification.javaClass}")
        }

    }

    constructor(choiceSpecification: ChoiceSpecification,
                responseList: List<Response>) : this(choiceSpecification) {
        responseList.forEach { add(it) }
    }

    fun add(response: Response) {
        if (response.learnerChoice != null)
            add(
                    ResponseData(response),
                    ExplanationData(response)
            )
    }

    fun add(responseData: ResponseData, explanationData: ExplanationData) {
        this[responseData]?.add(explanationData)
                ?: this.put(responseData, mutableListOf(explanationData))

    }

}