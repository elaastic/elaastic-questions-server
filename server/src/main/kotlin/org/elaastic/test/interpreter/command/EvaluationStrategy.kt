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

package org.elaastic.test.interpreter.command

import org.elaastic.activity.response.Response
import java.math.BigDecimal
import kotlin.random.Random

enum class EvaluationStrategy(val evaluate: (Response) -> BigDecimal) {
    RANDOM(
        fun(_: Response): BigDecimal = (1 + Random.nextInt(4)).toBigDecimal()
    ),
    ALWAYS_MAX(
        fun(_: Response): BigDecimal = 5.toBigDecimal()
    ),
    ALWAYS_MIN(
        fun(_: Response): BigDecimal = 1.toBigDecimal()
    ),
    RELEVANT(
        fun(response: Response): BigDecimal {
            val score = response.score?.toInt() ?: 0
            return (1 + score * 4 / 100).toBigDecimal()
        }
    ),
    IRRELEVANT(
        fun(response: Response): BigDecimal {
            val score = response.score?.toInt() ?: 0
            return (5 - score * 4 / 100).toBigDecimal()
        }
    ),
}