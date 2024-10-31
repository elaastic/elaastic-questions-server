package org.elaastic.common.util

import java.math.BigDecimal

fun BigDecimal.isNumericallyEqual(other: BigDecimal): Boolean {
    return this.compareTo(other) == 0
}
