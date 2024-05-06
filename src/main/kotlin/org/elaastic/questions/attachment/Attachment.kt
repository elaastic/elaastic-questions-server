/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.questions.attachment

import org.elaastic.questions.subject.statement.Statement
import org.elaastic.questions.persistence.AbstractJpaPersistable
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import javax.persistence.AttributeConverter
import javax.validation.constraints.NotNull
import kotlin.math.roundToInt


/**
 * Entity class for an attachment file
 *
 * An attachment is a file that can be attached to a statement
 *
 * @param name the name of the attachment
 * @param originalFileName the original name of the file
 * @param size the size of the file
 * @param mimeType the mime type of the file
 * @param toDelete a boolean to know if the file has to be deleted
 * @see MimeType for the possible type of attachment
 */
@Entity(name = "attachement")
class Attachment(
    @field:NotBlank var name: String,

    @field:Size(min = 1)
    @Column(name = "original_name")
    var originalFileName: String? = null,

    var size: Long? = null,

    @Column(name = "type_mime")
    var mimeType: MimeType? = null,

    var toDelete: Boolean = false
) : AbstractJpaPersistable<Long>() {

    @Version
    var version: Long? = null

    @field:NotNull
    @Column(columnDefinition = "BINARY(16)")
    var uuid: UUID = UUID.randomUUID()

    @NotNull
    @NotBlank
    var path: String? = null

    @Embedded
    var dimension: Dimension? = null

    @OneToOne
    var statement: Statement? = null


    fun isDisplayableImage(): Boolean {
        return mimeType?.correspondsToDisplayableImage() ?: false
    }

    fun isDisplayableText(): Boolean {
        return mimeType?.correspondsToDisplayableText() ?: false
    }

    override fun toString(): String {
        return "Attachment(path='$path', name='$name', id=$$id, version=$version, originalName=$originalFileName, size=$size, mimeType=$mimeType, dimension=$dimension, toDelete=$toDelete)"
    }

    fun getDimensionForDisplay(widthMax: Int, heightMax: Int): Dimension {
        return Companion.getDimensionForDisplay(dimension, widthMax, heightMax)
    }

    companion object {
        fun getDimensionForDisplay(dimension: Dimension?, widthMax: Int, heightMax: Int): Dimension {
            return if (dimension != null) {
                var l = dimension.width
                var h = dimension.height
                val ratio = listOf(l / widthMax.toDouble(), h / heightMax.toDouble()).maxOrNull()!!

                if (ratio > 1) {
                    l = (l / ratio).roundToInt()
                    h = (h / ratio).roundToInt()
                }
                Dimension(l, h)
            } else Dimension(widthMax, heightMax)
        }
    }

}


/**
 * Class for the mime type of attachment
 * @param label the label of the mime type
 */
class MimeType(val label: String = "application/octet-stream") {

    /**
     * Check if the mime type corresponds to a displayable image
     *
     * @see MimeTypesOfDisplayableImage
     * @return true if the mime type corresponds to a displayable image
     */
    fun correspondsToDisplayableImage(): Boolean {
        return label in MimeTypesOfDisplayableImage.values().map { it.label }
    }

    /**
     * Check if the mime type corresponds to a displayable text
     * @return true if the mime type corresponds to a displayable text
     */
    fun correspondsToDisplayableText(): Boolean {
        return label.startsWith("text/")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MimeType) return false

        if (label != other.label) return false

        return true
    }

    override fun hashCode(): Int {
        return label.hashCode()
    }

}

/**
 * Enum class for the mime types of displayable images
 *
 * An image can be a `gif`, `jpeg` or `png`
 * @param label the label of the mime type
 */
enum class MimeTypesOfDisplayableImage(val label: String) {
    gif("image/gif"),
    jpeg("image/jpeg"),
    png("image/png");

    fun toMimeType(): MimeType {
        return MimeType(this.label)
    }
}

@Embeddable
class Dimension(
    @field:Column(name = "dimension_width") val width: Int,
    @field:Column(name = "dimension_height") val height: Int
) : Comparable<Dimension> {

    override fun compareTo(other: Dimension): Int {
        if (width == other.width && height == other.height) {
            return 0
        }

        if (width > other.width || height > other.height) {
            return 1
        }

        return -1
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Dimension) return false

        if (width != other.width) return false
        if (height != other.height) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        return result
    }

    override fun toString(): String {
        return "Dimension(width=$width, height=$height)"
    }


}

@Converter(autoApply = true)
class MimeTypeConverter : AttributeConverter<MimeType, String> {

    override fun convertToDatabaseColumn(mimeType: MimeType?): String? {
        return mimeType?.label
    }

    override fun convertToEntityAttribute(label: String?): MimeType? {
        return if (label != null) MimeType(label) else null
    }
}
