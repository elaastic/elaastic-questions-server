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

package org.elaastic.player.evaluation

import org.elaastic.player.evaluation.chatgpt.ChatGptEvaluationModel
import org.elaastic.player.evaluation.draxo.DraxoEvaluationModel

/**
 * Evaluation model.
 *
 * This model is used to store the evaluation of a player's response. It
 * contains all the draxo peer grading under a list of
 * [DraxoEvaluationModel] and the [ChatGptEvaluationModel] of an player's
 * response It'd use with the
 * [DraxoPeerGradingController][org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoPeerGradingController].
 *
 * @param draxoEvaluationModels List of [DraxoEvaluationModel] of the
 *     player's response
 * @param chatGptEvaluationModel [ChatGptEvaluationModel] of the player's
 *     response
 * @param hideName Boolean to hide the name of the player's response
 * @param canSeeChatGPTEvaluation Boolean to see the
 *     [ChatGptEvaluationModel] of the player's response
 */
data class EvaluationModel(
    val draxoEvaluationModels: List<DraxoEvaluationModel>?,
    val chatGptEvaluationModel: ChatGptEvaluationModel?,
    val hideName: Boolean = false,
    val canSeeChatGPTEvaluation: Boolean = false,
) {
    constructor(
        draxoEvaluationModels: List<DraxoEvaluationModel>?,
        chatGptEvaluationModel: ChatGptEvaluationModel?,
        hideName: Boolean = false,
        canSeeChatGPTEvaluation: Boolean = false,
        isTeacher: Boolean = false
    ) : this(draxoEvaluationModels, chatGptEvaluationModel, hideName, canSeeChatGPTEvaluation) {
        this.chatGptEvaluationModel?.let {
            it.viewedByTeacher = isTeacher
        }
    }
}