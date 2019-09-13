package org.elaastic.questions.attachment.datastore

import java.io.InputStream

/**
 * Append-only store for binary streams. A data store consists of a number
 * of identifiable data records that each contain a distinct binary stream.
 * New binary streams can be added to the data store, but existing streams
 * are never removed or modified.
 *
 *
 * A data store should be fully thread-safe, i.e. it should be possible to
 * add and access data records concurrently. Optimally even separate processes
 * should be able to concurrently access the data store with zero interprocess
 * synchronization.
 */
interface DataStore {

    /**
     * Get all identifiers.
     *
     * @return an iterator over all DataIdentifier objects
     * @throws DataStoreException if the list could not be read
     */
    val allIdentifiers: Iterator<DataIdentifier>

    /**
     * Get the minimum size of an object that should be stored in this data store.
     * Depending on the overhead and configuration, each store may return a different value.
     *
     * @return the minimum size in bytes
     */
    val minRecordLength: Int

    /**
     * Check if a record for the given identifier exists, and return it if yes.
     * If no record exists, this method returns null.
     *
     * @param identifier data identifier
     * @return the record if found, and null if not
     */
    @Throws(DataStoreException::class)
    fun getRecordIfStored(identifier: DataIdentifier): DataRecord?

    /**
     * Returns the identified data record. The given identifier should be
     * the identifier of a previously saved data record. Since records are
     * never removed, there should never be cases where the identified record
     * is not found. Abnormal cases like that are treated as errors and
     * handled by throwing an exception.
     *
     * @param identifier data identifier
     * @return identified data record
     * @throws DataStoreException if the data store could not be accessed,
     * or if the given identifier is invalid
     */
    @Throws(DataStoreException::class)
    fun getRecord(identifier: DataIdentifier): DataRecord?

    /**
     * Creates a new data record. The given binary stream is consumed and
     * a binary record containing the consumed stream is created and returned.
     * If the same stream already exists in another record, then that record
     * is returned instead of creating a new one.
     *
     *
     * The given stream is consumed and **not closed** by this
     * method. It is the responsibility of the caller to close the stream.
     * A typical call pattern would be:
     * <pre>
     * InputStream stream = ...;
     * try {
     * record = store.addRecord(stream);
     * } finally {
     * stream.close();
     * }
    </pre> *
     *
     * @param stream binary stream
     * @return data record that contains the given stream
     * @throws DataStoreException if the data store could not be accessed
     */
    @Throws(DataStoreException::class)
    fun addRecord(stream: InputStream): DataRecord

    /**
     * From now on, update the modified date of an object even when accessing it.
     * Usually, the modified date is only updated when creating a new object,
     * or when a new link is added to an existing object. When this setting is enabled,
     * even getLength() will update the modified date.
     *
     * @param before - update the modified date to the current time if it is older than this value
     */
    fun updateModifiedDateOnAccess(before: Long)

    /**
     * Delete objects that have a modified date older than the specified date.
     *
     * @param min the minimum time
     * @return the number of data records deleted
     * @throws DataStoreException
     */
    @Throws(DataStoreException::class)
    fun deleteAllOlderThan(min: Long): Int

    /**
     * Initialized the data store
     *
     * @param homeDir the home directory of the repository
     * @throws Exception
     */
    @Throws(Exception::class)
    fun init(homeDir: String)

    /**
     * Close the data store
     *
     * @throws DataStoreException if a problem occurred
     */
    @Throws(DataStoreException::class)
    fun close()

    /**
     * Clear the in-use list. This is only used for testing to make the the garbage collection
     * think that objects are no longer in use.
     */
    fun clearInUse()

}

