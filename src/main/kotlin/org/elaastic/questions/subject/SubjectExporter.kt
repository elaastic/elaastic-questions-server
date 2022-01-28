package org.elaastic.questions.subject

import com.fasterxml.jackson.databind.ObjectMapper
import org.elaastic.questions.attachment.AttachmentService
import org.elaastic.questions.directory.User
import org.elaastic.questions.util.ZipService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Service
import java.io.*
import java.util.*
import java.util.zip.ZipFile

@Service
class SubjectExporter(
    // Inject that bean to get the mapper used by Spring MVC (that is properly configured)
    @Qualifier("mappingJackson2HttpMessageConverter") @Autowired springMvcJacksonConverter: MappingJackson2HttpMessageConverter,
    @Autowired val zipService: ZipService,
    @Autowired val attachmentService: AttachmentService,
    @Autowired val subjectService: SubjectService,
) {

    val mapper: ObjectMapper = springMvcJacksonConverter.objectMapper

    fun exportToPojo(subject: Subject) =
        ExportSubjectData(subject)

    fun exportToJson(subject: Subject) = exportToJson(exportToPojo(subject))

    fun exportToJson(exportSubjectData: ExportSubjectData): String =
        mapper.writeValueAsString(exportSubjectData)

    fun exportToZip(subject: Subject, filename: String, outputStream: OutputStream) {
        val exportSubjectData = exportToPojo(subject)

        zipService.zip(
            outputStream,
            listOf(
                ZipService.ZipEntryData(
                    "${filename}.elaastic.json",
                    exportToJson(exportSubjectData).byteInputStream()
                )

            ) +
                    exportSubjectData.getAttachmentList().map { exportAttachment ->
                        ZipService.ZipEntryData(
                            "resources/${exportAttachment.path}",
                            attachmentService.getInputStreamForPath(exportAttachment.path)
                        )
                    }
        )
    }

    fun parseFromJson(jsonReader: Reader): ExportSubjectData =
        mapper.readValue(jsonReader, ExportSubjectData::class.java)

    fun importFromJson(user: User, jsonReader: Reader): Subject =
        subjectService.createFromExportData(
            user,
            parseFromJson(jsonReader)
        )

    fun importFromZip(user: User, inputStream: InputStream): Subject {
        val zip = File.createTempFile(UUID.randomUUID().toString(), null)

        inputStream.use { input ->
            zip.outputStream().use { output ->
                input.copyTo(output, 16 * 1024)
            }
        }

        val extractedFiles = zipService.unzip(ZipFile(zip))

        // Attachments
        val exportSubjectData = parseFromJson(
            InputStreamReader(extractedFiles.first().file.inputStream())
        )
        exportSubjectData.getAttachmentList().forEach { exportAttachment ->
            exportAttachment.attachmentFile = extractedFiles.find { it.name == "resources/"+exportAttachment.path }?.file
        }

        val subject = subjectService.createFromExportData(
            user,
            exportSubjectData
        )

        zip.delete()

        return subject
    }


}