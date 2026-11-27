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

package org.elaastic.activity.evaluation.peergrading.draxo.criteria

import com.fasterxml.jackson.annotation.JsonInclude
import org.elaastic.activity.evaluation.peergrading.draxo.option.OptionId

@JsonInclude(JsonInclude.Include.NON_NULL)
class CriteriaEvaluation(
    val criteria: Criteria,
    val optionId: OptionId,
    val explanation: String? = null
) {
    init {
        // It must be a valid option for this criteria
        require(criteria.scale.containsKey(optionId)) {
            "The criteria $criteria does not have the option $optionId"
        }

        // The explanation must be provided only for negative options
        require(explanation == null || criteria.isNegativeOption(optionId))
    }
}