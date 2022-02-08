import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.interaction.results.ConfidencePercentage
import org.elaastic.questions.assignment.sequence.interaction.results.ItemIndex
import org.elaastic.questions.assignment.sequence.interaction.results.ResponsePercentage
import org.elaastic.questions.player.components.responseDistributionChart.ChoiceSpecificationData
import java.math.BigDecimal

typealias Grade = BigDecimal
typealias EvaluationPercentage = Float

data class EvaluationDistributionChartModel(
        val interactionId: Long,
        val choiceSpecification: ChoiceSpecificationData,
        val results: Map<ItemIndex, Map<BigDecimal, EvaluationPercentage>>
)