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

package org.elaastic.material.instructional.question.attachment

import org.elaastic.material.instructional.question.attachment.Attachment
import org.elaastic.material.instructional.question.attachment.Dimension
import org.elaastic.material.instructional.question.attachment.MimeType
import org.elaastic.material.instructional.question.attachment.MimeTypesOfDisplayableImage
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.logging.Logger
import javax.validation.Validation
import javax.validation.Validator


internal class AttachmentTest {

    val logger = Logger.getLogger(AttachmentTest::class.java.name)
    lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `test dimension comparison`() {
        // given: A dimension
        val dimension1 = Dimension(width = 100, height = 100)

        // expect: comparison with another equal dimension return 0
        assertThat(dimension1.compareTo(Dimension(width = 100, height = 100)), equalTo(0))
        // expect: comparison with a smaller dimension return 1
        assertThat(dimension1.compareTo(Dimension(width = 50, height = 50)), equalTo(1))
        // expect: comparison with a bigger dimension return -1
        assertThat(dimension1.compareTo(Dimension(width = 150, height = 150)), equalTo(-1))
    }


    @Test
    fun `test attachement is a displayable image`() {
        // given an attachment corresponding to a jpeg image
        val attachment = Attachment(
                name = "MyAttach",
                originalFileName = "originalName",
                size = 1024,
                mimeType = MimeTypesOfDisplayableImage.jpeg.toMimeType()
        )
        attachment.path = "/to/path"
        attachment.dimension = Dimension(width = 100, height = 100)
        // expect attachment is acknowledge to be displayable
        assertThat(attachment.isDisplayableImage(), equalTo(true))

        // given an attachment corresponding to a gif image
        attachment.mimeType = MimeTypesOfDisplayableImage.gif.toMimeType()
        // expect attachment is acknowledge to be displayable
        assertThat(attachment.isDisplayableImage(), equalTo(true))

        // given an attachment corresponding to a png image
        attachment.mimeType = MimeTypesOfDisplayableImage.png.toMimeType()
        // expect attachment is acknowledge to be displayable
        assertThat(attachment.isDisplayableImage(), equalTo(true))

    }

    @Test
    fun `test attachement is a displayable text`() {
        // given an attachment corresponding to a jpeg image
        val attachment = Attachment(
                name = "MyAttach",
                originalFileName = "originalName",
                size = 1024,
                mimeType = MimeType("text/html")
        )
        attachment.path = "/to/path"
        attachment.dimension = Dimension(width = 100, height = 100)
        // expect attachment is acknowledge to be displayable
        assertThat(attachment.isDisplayableText(), equalTo(true))
    }

    @Test
    fun `test attachement is not displayable`() {
        // given an attachment corresponding to something not displayable
        val attachment = Attachment(
                name = "MyAttach",
                originalFileName = "originalName",
                size = 1024,
                mimeType = MimeType("truc/som")
        )
        attachment.path = "/to/path"
        attachment.dimension = Dimension(width = 100, height = 100)
        // expect attachment is acknowledge to be not displayable
        assertThat(attachment.isDisplayableText(), equalTo(false))
        assertThat(attachment.isDisplayableImage(), equalTo(false))
    }

    @Test
    fun `test validation on valid attachment`() {
        // given a valid attachment
        val attachment = Attachment(
                name = "MyAttach",
                originalFileName = "originalName",
                size = 1024,
                mimeType = MimeType("text/html")
        )
        attachment.path = "/to/path"
        attachment.dimension = Dimension(width = 100, height = 100)
        logger.info(attachment.toString())
        logger.info(validator.validate(attachment).toString())
        // expect validation succeeds
        assertThat(validator.validate(attachment).isEmpty(), equalTo(true))
        // when setting correctly properties
        attachment.mimeType = MimeType("text/text")
        attachment.dimension = Dimension(width = 100, height = 100)
        attachment.originalFileName = "oldName"
        attachment.size = 125
        // then validation still succeeds
        assertThat(validator.validate(attachment).isEmpty(), equalTo(true))
    }

    @Test
    fun `test validation on invalid attachment`() {
        // given a valid attachment
        val attachment = Attachment(
                name = "",
                originalFileName = "originalName",
                size = 1024,
                mimeType = MimeType("text/html")
        )
        attachment.path = "/to/path"
        attachment.dimension = Dimension(width = 100, height = 100)
        // expect validation fails
        assertThat(validator.validate(attachment).isEmpty(), equalTo(false))
        // when setting incorrectly originalName
        attachment.name = "myAttach"
        attachment.originalFileName = ""
        // then validation still fails
        assertThat(validator.validate(attachment).isEmpty(), equalTo(false))
    }

}
