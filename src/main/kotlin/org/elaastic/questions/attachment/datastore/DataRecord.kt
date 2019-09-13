package org.elaastic.questions.attachment.datastore

import java.io.InputStream

/**
 * Immutable data record that consists of a binary stream.
 */
interface DataRecord {

    /**
     * Returns the identifier of this record.
     *
     * @return data identifier
     */
    val identifier: DataIdentifier

    /**
     * Returns the length of the binary stream in this record.
     *
     * @return length of the binary stream
     * @throws DataStoreException if the record could not be accessed
     */
    val length: Long

    /**
     * Returns the the binary stream in this record.
     *
     * @return binary stream
     * @throws DataStoreException if the record could not be accessed
     */
    val stream: InputStream

    /**
     * Returns the last modified of the record.
     *
     * @return last modified time of the binary stream
     */
    val lastModified: Long
}
