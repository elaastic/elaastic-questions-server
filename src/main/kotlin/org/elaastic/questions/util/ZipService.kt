package org.elaastic.questions.util

import org.springframework.stereotype.Service
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

@Service
class ZipService {

    fun zip(out: OutputStream, entriesData: List<ZipEntryData>) {
        ZipOutputStream(BufferedOutputStream(out)).use { zip ->
            entriesData.forEach { entryData ->
                BufferedInputStream(entryData.inputStream).use { origin ->
                    zip.putNextEntry(
                        ZipEntry(entryData.name)
                    )
                    origin.copyTo(zip, 1024)
                    zip.closeEntry()
                }
            }
        }
    }

    open class ZipEntryData(
        val name: String,
        val inputStream: InputStream,
    )

    open class ExtractFileData(
        val name: String,
        val file: File,
    )

    fun unzip(zipFile: ZipFile): List<ExtractFileData> =
        zipFile.use { zip ->
            zip.entries().asSequence().map { entry ->
                zip.getInputStream(entry).use { input ->
                    val extractedFile = File.createTempFile(entry.name, null)

                    extractedFile.outputStream().use { output ->
                        input.copyTo(output, 16*1024)
                    }

                    ExtractFileData(name = entry.name, file = extractedFile)
                }
            }.toList()
        }

}