package org.elaastic.questions.attachment.datastore

import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * Data record that is based on a normal file.
 */
class FileDataRecord(
        identifier: DataIdentifier,
        private val file: File
) : AbstractDataRecord(identifier) {

    /**
     * {@inheritDoc}
     */
    override val length: Long
        get() = file.length()

    /**
     * {@inheritDoc}
     */
    override val stream: InputStream
        @Throws(DataStoreException::class)
        get() {
            try {
                return LazyFileInputStream(file)
            } catch (e: IOException) {
                throw DataStoreException("Error opening input stream of " + file.absolutePath, e)
            }

        }

    /**
     * {@inheritDoc}
     */
    override val lastModified: Long
        get() = file.lastModified()

    init {
        assert(file.isFile)
    }
}

