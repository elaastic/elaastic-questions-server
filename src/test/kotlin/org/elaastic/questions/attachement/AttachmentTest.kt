package org.elaastic.questions.attachement

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Test
import java.util.logging.Logger
import javax.validation.Validation
import javax.validation.Validator


internal class AttachmentTest {

    val logger = Logger.getLogger(AttachmentTest::class.java.name)
    lateinit var validator: Validator

    @Before
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `test dimension comparison`() {
        // given: A dimension
        val dimension1 = Dimension(width = 100, height = 100)

        // expect: comparison with another equal dimension return 0
        assertThat(dimension1.compareTo(Dimension(width = 100, height = 100)), `is`(equalTo(0)))
        // expect: comparison with a smaller dimension return 1
        assertThat(dimension1.compareTo(Dimension(width = 50, height = 50)), `is`(equalTo(1)))
        // expect: comparison with a bigger dimension return -1
        assertThat(dimension1.compareTo(Dimension(width = 150, height = 150)), `is`(equalTo(-1)))
    }

    @Test
    fun `test dimension to string `() {
        // given: A dimension
        val dimension1 = Dimension(width = 100, height = 100)

        // expect: display of the dimension is correct when call to to string
        assertThat(dimension1.toString(), `is`(equalTo("dim    h: 100     l: 100")))
    }

    @Test
    fun `test attachement is a displayable image`() {
        // given an attachment corresponding to a jpeg image
        val attachment = Attachment(path = "a/path", name = "myImage")
        attachment.mimeType = MimeType(MimeType.MimeTypesOfDisplayableImage.jpeg.label)
        // expect attachment is acknowledge to be displayable
        assertThat(attachment.imageIsDisplayable(), `is`(equalTo(true)))

        // given an attachment corresponding to a gif image
        attachment.mimeType = MimeType(MimeType.MimeTypesOfDisplayableImage.gif.label)
        // expect attachment is acknowledge to be displayable
        assertThat(attachment.imageIsDisplayable(), `is`(equalTo(true)))

        // given an attachment corresponding to a png image
        attachment.mimeType = MimeType(MimeType.MimeTypesOfDisplayableImage.png.label)
        // expect attachment is acknowledge to be displayable
        assertThat(attachment.imageIsDisplayable(), `is`(equalTo(true)))

    }

    @Test
    fun `test attachement is a displayable text`() {
        // given an attachment corresponding to a jpeg image
        val attachment = Attachment(path = "a/path", name = "myText")
        attachment.mimeType = MimeType("text/html")
        // expect attachment is acknowledge to be displayable
        assertThat(attachment.textIsDisplayable(), `is`(equalTo(true)))
    }

    @Test
    fun `test attachement is not displayable`() {
        // given an attachment corresponding to something not displayable
        val attachment = Attachment(path = "a/path", name = "myAttach")
        attachment.mimeType = MimeType("truc/som")
        // expect attachment is acknowledge to be not displayable
        assertThat(attachment.textIsDisplayable(), `is`(equalTo(false)))
        assertThat(attachment.imageIsDisplayable(), `is`(equalTo(false)))
    }

    @Test
    fun `test validation on valid attachment`() {
        // given a valid attachment
        val attachment = Attachment(path = "a/path", name = "myAttach")
        logger.info(attachment.toString())
        logger.info(validator.validate(attachment).toString())
        // expect validation succeeds
        assertThat(validator.validate(attachment).isEmpty(), `is`(true))
        // when setting correctly properties
        attachment.mimeType = MimeType("text/text")
        attachment.dimension = Dimension(width = 100, height = 100)
        attachment.originalName = "oldName"
        attachment.size = 125
        // then validation still succeeds
        assertThat(validator.validate(attachment).isEmpty(), `is`(true))
    }

    @Test
    fun `test validation on invalid attachment`() {
        // given a valid attachment
        val attachment = Attachment(path = "a/path", name = "")
        // expect validation fails
        assertThat(validator.validate(attachment).isEmpty(), `is`(false))
        // when setting incorrectly originalName
        attachment.name = "myAttach"
        attachment.originalName = ""
        // then validation still succeeds
        assertThat(validator.validate(attachment).isEmpty(), `is`(false))
    }

}
