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

package org.elaastic.common.util

import org.springframework.stereotype.Service
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

@Service
class ZipService {

    /**
     * Compress a list of file into a ZIP archive
     */
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

    /**
     * Extract files from a ZIP archive into the temp dir
     */
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