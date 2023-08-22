package org.elaastic.questions.assignment.sequence.peergrading.draxo.option

import java.math.BigDecimal

data class OptionSpecification(
    val type: OptionType,
    val value: BigDecimal = BigDecimal(0),
)