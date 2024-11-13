package org.elaastic.activity.evaluation.peergrading.draxo.option

import java.math.BigDecimal

data class OptionSpecification(
    val type: OptionType,
    val value: BigDecimal = BigDecimal(0),
)