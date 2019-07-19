package org.elaastic.questions.attachement

import org.elaastic.questions.assignment.Statement
import org.elaastic.questions.persistence.AbstractJpaPersistable
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import javax.persistence.AttributeConverter



@Entity(name = "attachement")
class Attachment(
        @field:NotBlank var path: String,
        @field:NotBlank var name: String
) : AbstractJpaPersistable<Long>() {

    @Version
    var version: Long? = null

    @Size(min = 1)
    var originalName: String? = null

    var size: Int? = null

    @Column(name = "type_mime")
    var mimeType: MimeType? = null

    @Embedded
    var dimension: Dimension? = null

    var toDelete: Boolean = false

    @ManyToOne
    var statement: Statement? = null


    fun isDisplayableImage():Boolean {
        return mimeType?.correspondsToDisplayableImage() ?: false
    }

    fun isDisplayableText():Boolean {
        return mimeType?.correspondsToDisplayableText() ?: false
    }

    override fun toString(): String {
        return "Attachment(path='$path', name='$name', id=$$id, version=$version, originalName=$originalName, size=$size, mimeType=$mimeType, dimension=$dimension, toDelete=$toDelete)"
    }
}


class MimeType(val label: String = "application/octet-stream") {

    fun correspondsToDisplayableImage():Boolean {
        return label in MimeTypesOfDisplayableImage.values().map { it.label }
    }

    fun correspondsToDisplayableText():Boolean {
        return label.startsWith("text/")
    }

    enum class MimeTypesOfDisplayableImage(val label: String) {
        gif("image/gif"),
        jpeg("image/jpeg"),
        png("image/png")
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

    override fun toString(): String {
        return "dim    h: $height     l: $width"
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
