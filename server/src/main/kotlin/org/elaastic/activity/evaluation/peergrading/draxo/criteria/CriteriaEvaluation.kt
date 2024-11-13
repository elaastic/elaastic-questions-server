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