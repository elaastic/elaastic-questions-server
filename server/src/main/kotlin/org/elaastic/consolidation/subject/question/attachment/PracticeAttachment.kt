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

package org.elaastic.consolidation.subject.question.attachment

import com.toedter.spring.hateoas.jsonapi.JsonApiId
import com.toedter.spring.hateoas.jsonapi.JsonApiType
import org.elaastic.material.instructional.question.attachment.Attachment
import org.elaastic.material.instructional.question.attachment.Dimension
import java.util.UUID

/**
 * Represent an Attachment bound to a PracticeQuestion
 * It will be rendered as json:api with its self URL allowing for downloading its metadata & File
 *
 * @author John Tranier
 */
class PracticeAttachment(
    @JsonApiId val id: UUID,
    val name: String,
    val originalFileName: String? = null,
    val size: Long? = null,
    val dimension: Dimension? = null,
    val mimeType: String? = null

    ) {

    @JsonApiType
    val type = "practice-attachment"

    constructor(attachment: Attachment): this(
        attachment.uuid,
        attachment.name,
        attachment.originalFileName,
        attachment.size,
        attachment.dimension,
        attachment.mimeType?.label
    )
}