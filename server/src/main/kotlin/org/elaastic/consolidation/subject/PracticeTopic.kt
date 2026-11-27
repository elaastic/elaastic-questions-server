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

package org.elaastic.consolidation.subject

import com.toedter.spring.hateoas.jsonapi.JsonApiId
import com.toedter.spring.hateoas.jsonapi.JsonApiType
import org.elaastic.material.instructional.course.Course
import java.util.UUID

/**
 * A practice topic is a folder of practice subjects allowing to organize
 * them.
 *
 * Practice topics are built based on the course notion of Elaastic
 *
 * @author John Tranier
 */
class PracticeTopic(
    @JsonApiId
    val id: UUID,
    val title: String
) {
    @JsonApiType
    val type = "practice-topic"

    constructor(course: Course): this(
        id = course.globalId,
        title = course.title
    )
}