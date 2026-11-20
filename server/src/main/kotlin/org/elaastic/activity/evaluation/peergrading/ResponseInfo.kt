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

package org.elaastic.activity.evaluation.peergrading

import org.elaastic.activity.response.Response
import java.math.BigDecimal

class ResponseInfo(
        val id: Long,
        val correct: Boolean,
        val evaluable: Boolean = true,
        var nbSelection: Int = 0
) : Comparable<ResponseInfo> {

    constructor(response: Response) : this(
            response.id!!,
            response.score?.compareTo(BigDecimal(100)) == 0,
        (response.explanation?.length ?: 0) > ResponseRecommendationService.MIN_SIZE_OF_EXPLANATION_TO_BE_EVALUATED
    )

    override fun compareTo(other: ResponseInfo): Int =
            this.nbSelection.compareTo(other.nbSelection)

    override fun toString(): String =
            "<$id, $correct, $nbSelection>"
}
