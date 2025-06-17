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
package org.elaastic.sequence

import org.elaastic.sequence.phase.evaluation.EvaluationMethod
import org.elaastic.sequence.phase.evaluation.EvaluationPhaseConfig

/**
 * Configuration for a sequence.
 * @author John Tranier
 *
 * @property executionContext The execution context for the sequence.
 * @property studentsProvideExplanation Whether the students are allowed to provide an explanation for the results.
 * @property responseToEvaluateCount The number of responses to evaluate.
 * @property evaluationByIA Whether the evaluation is done by ChatGPT.
 * @property evaluationMethod The configuration for the evaluation phase.
 *
 */
data class SequenceConfig(
    val executionContext: ExecutionContext,
    val studentsProvideExplanation: Boolean? = false,
    val confrontingViewsPhaseConfig: EvaluationPhaseConfig? = null,
)