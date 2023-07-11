package org.elaastic.questions.assignment.sequence.peergrading.draxo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.elaastic.questions.player.phase.evaluation.draxo.criteria.Criteria
import org.elaastic.questions.player.phase.evaluation.draxo.criteria.CriteriaEvaluation
import org.elaastic.questions.player.phase.evaluation.draxo.criteria.CriteriaSerializer
import org.elaastic.questions.player.phase.evaluation.draxo.option.OptionId
import org.elaastic.questions.player.phase.evaluation.draxo.option.OptionType

class DraxoEvaluation {

    @JsonSerialize(keyUsing = CriteriaSerializer::class)
    val criteriaValuation: MutableMap<Criteria, CriteriaEvaluation?> = Criteria.values().associateWith { null }.toMutableMap()
    var currentCriteria: Criteria? = Criteria.values().first() // D

    fun addEvaluation(evaluation: CriteriaEvaluation): DraxoEvaluation {
        check(evaluation.criteria == currentCriteria) { "This criteria cannot be evaluated now" }
        criteriaValuation[evaluation.criteria] = evaluation
        if(evaluation.criteria.isPositiveOption(evaluation.optionId)) {
            currentCriteria = currentCriteria?.next()
        }
        return this // For chaining calls
    }

    fun addEvaluation(criteria: Criteria, optionId: OptionId, explanation: String? = null) =
        addEvaluation(CriteriaEvaluation(criteria, optionId, explanation))

    fun addEvaluationList(evaluationList: List<CriteriaEvaluation>) =
        this.also { evaluationList.forEach(::addEvaluation) }

    @JsonIgnore
    fun getExplanation() =
        Criteria.values().firstNotNullOfOrNull { criteriaValuation[it]?.explanation }

    @JsonIgnore
    operator fun get(criteria: Criteria) =
        criteriaValuation[criteria]?.optionId

    @JsonIgnore
    fun isValid() =
        // Criteria are evaluated in the proper order
        Criteria.values().map { criteriaValuation[it] }.dropWhile { it != null }.all { it == null} &&

                // No criteria are evaluated after a non-positive evaluation
                Criteria.values().map { criteria ->
                    criteriaValuation[criteria]?.optionId?.let(criteria::getOptionType)
                }
                    .dropWhile { it == OptionType.POSITIVE }.drop(1).all { it == null}
}