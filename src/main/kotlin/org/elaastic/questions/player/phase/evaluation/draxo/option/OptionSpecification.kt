package org.elaastic.questions.player.phase.evaluation.draxo.option

import java.math.BigDecimal

data class OptionSpecification(
    val type: OptionType,
    val value: BigDecimal = BigDecimal(0),
)