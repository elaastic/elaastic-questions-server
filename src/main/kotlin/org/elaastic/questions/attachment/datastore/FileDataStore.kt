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

package org.elaastic.questions.attachment.datastore

import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import java.io.*
import java.lang.ref.WeakReference
import java.security.DigestOutputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.sql.Timestamp
import java.util.*
import javax.annotation.PostConstruct

/**
 * Simple file-based data store. Data records are stored as normal files
 * named using a message digest of the contained binary stream.
 *
 *
 * A three level directory structure is used to avoid placing too many
 * files in a single directory. The chosen structure is designed to scale
 * up to billions of distinct records.
 *
 *
 * This implementation relies on the underlying file system to support
 * atomic O(1) move operations with [File.renameTo].
 */
@Component
class FileDataStore : DataStore {

    /**
     * The minimum modified date. If a file is accessed (read or write) with a modified date
     * older than this value, the modified date is updated to the current time.
     */
    private var minModifiedDate: Long = 0

    /**
     * The directory that contains all the data record files. The structure
     * of content within this directory is controlled by this class.
     */
    private var directory: File? = null

    /**
     * The name of the directory that contains all the data record files. The structure
     * of content within this directory is controlled by this class.
     */
    @Value("\${elaastic.datastore.path}")
    lateinit var path: String

    /**
     * The minimum size of an object that should be stored in this data store.
     */
    override var minRecordLength = DEFAULT_MIN_RECORD_LENGTH

    /**
     * All data identifiers that are currently in use are in this set until they are garbage collected.
     */
    protected var inUse: MutableMap<DataIdentifier, WeakReference<DataIdentifier>> = Collections.synchronizedMap(WeakHashMap())

    override val allIdentifiers: Iterator<DataIdentifier>
        get() {
            val files = ArrayList<File>()
            listRecursive(files, directory!!)
            val identifiers = ArrayList<DataIdentifier>()
            for (f in files) {
                val name = f.name
                if (!name.startsWith(TMP)) {
                    val id = DataIdentifier(name)
                    identifiers.add(id)
                }
            }
            return identifiers.iterator()
        }

    /**
     * Initialized the data store.
     * If the path is not set, an exception is thrown.
     * This directory is automatically created if it does not yet exist.
     *
     */
    @PostConstruct
    override fun initDataStore() {
        directory = File(path)
        directory!!.mkdirs()
    }

    @Throws(DataStoreException::class)
    override fun getRecordIfStored(identifier: DataIdentifier): DataRecord? {
        return getRecord(identifier, true)
    }

    /**
     * Get a data record for the given identifier.
     * This method only checks if the file exists if the verify flag is set.
     * If the verify flag is set and the file doesn't exist, the method returns null.
     *
     * @param identifier the identifier
     * @param verify     whether to check if the file exists
     * @return the data record or null
     */
    @Throws(DataStoreException::class)
    private fun getRecord(identifier: DataIdentifier, verify: Boolean): DataRecord? {
        val file = getFile(identifier)
        synchronized(this) {
            if (verify && !file.exists()) {
                return null
            }
            if (minModifiedDate != 0L) {
                // only check when running garbage collection
                if (getLastModified(file) < minModifiedDate) {
                    setLastModified(file, System.currentTimeMillis() + ACCESS_TIME_RESOLUTION)
                }
            }
            usesIdentifier(identifier)
            return FileDataRecord(identifier, file)
        }
    }

    /**
     * Returns the record with the given identifier. Note that this method
     * performs no sanity checks on the given identifier. It is up to the
     * caller to ensure that only identifiers of previously created data
     * records are used.
     *
     * @param identifier data identifier
     * @return identified data record
     */
    @Throws(DataStoreException::class)
    override fun getRecord(identifier: DataIdentifier): DataRecord? {
        return getRecord(identifier, false)
    }

    private fun usesIdentifier(identifier: DataIdentifier) {
        inUse[identifier] = WeakReference(identifier)
    }

    /**
     * Creates a new data record.
     * The stream is first consumed and the contents are saved in a temporary file
     * and the SHA-1 message digest of the stream is calculated. If a
     * record with the same SHA-1 digest (and length) is found then it is
     * returned. Otherwise the temporary file is moved in place to become
     * the new data record that gets returned.
     *
     * @param stream binary stream
     * @return data record that contains the given stream
     * @throws DataStoreException if the record could not be created
     */
    @Throws(DataStoreException::class)
    override fun addRecord(stream: InputStream): DataRecord {
        var temporary: File? = null
        try {
            temporary = newTemporaryFile()
            val tempId = DataIdentifier(temporary.name)
            usesIdentifier(tempId)
            // Copy the stream to the temporary file and calculate the
            // stream length and the message digest of the stream
            val length: Long
            val digest = MessageDigest.getInstance(DIGEST)
            val output = DigestOutputStream(
                    FileOutputStream(temporary), digest)
            try {
                length = IOUtils.copyLarge(stream, output)
            } finally {
                output.close()
            }
            val identifier = DataIdentifier(digest.digest())

            synchronized(this) {
                // Check if the same record already exists, or
                // move the temporary file in place if needed
                usesIdentifier(identifier)
                val file = getFile(identifier)
                if (!file.exists()) {
                    val parent = file.parentFile
                    parent.mkdirs()
                    if (temporary!!.renameTo(file)) {
                        // no longer need to delete the temporary file
                        temporary = null
                    } else {
                        throw IOException(
                                "Can not rename " + temporary!!.absolutePath
                                        + " to " + file.absolutePath
                                        + " (media read only?)")
                    }
                } else {
                    val now = System.currentTimeMillis()
                    if (getLastModified(file) < now + ACCESS_TIME_RESOLUTION) {
                        setLastModified(file, now + ACCESS_TIME_RESOLUTION)
                    }
                }
                if (file.length() != length) {
                    // Sanity checks on the record file. These should never fail,
                    // but better safe than sorry...
                    if (!file.isFile) {
                        throw IOException("Not a file: $file")
                    }
                    throw IOException("$DIGEST collision: $file")
                }
            }
            // this will also make sure that
            // tempId is not garbage collected until here
            inUse.remove(tempId)
            return FileDataRecord(identifier, getFile(identifier))
        } catch (e: NoSuchAlgorithmException) {
            throw DataStoreException("$DIGEST not available", e)
        } catch (e: IOException) {
            throw DataStoreException("Could not add record", e)
        } finally {
            temporary?.let { it.delete() }
        }
    }

    /**
     * Returns the identified file. This method implements the pattern
     * used to avoid problems with too many files in a single directory.
     *
     *
     * No sanity checks are performed on the given identifier.
     *
     * @param identifier data identifier
     * @return identified file
     */
     fun getFile(identifier: DataIdentifier): File {
        usesIdentifier(identifier)
        val string = identifier.toString()
        var file = directory
        file = File(file, string.substring(0, 2))
        file = File(file, string.substring(2, 4))
        file = File(file, string.substring(4, 6))
        return File(file, string)
    }

    /**
     * Returns a unique temporary file to be used for creating a new
     * data record.
     *
     * @return temporary file
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun newTemporaryFile(): File {
        // the directory is already created in the init method
        return File.createTempFile(TMP, null, directory)
    }

    override fun updateModifiedDateOnAccess(before: Long) {
        minModifiedDate = before
    }

    override fun deleteAllOlderThan(min: Long): Int {
        return deleteOlderRecursive(directory!!, min)
    }

    private fun deleteOlderRecursive(file: File, min: Long): Int {
        var count = 0
        if (file.isFile && file.exists() && file.canWrite()) {
            synchronized(this) {
                val lastModified: Long
                lastModified = try {
                    getLastModified(file)
                } catch (e: DataStoreException) {
                    log.warn("Failed to read modification date; file not deleted", e)
                    // don't delete the file, since the lastModified date is uncertain
                    min
                }

                if (lastModified < min) {
                    val id = DataIdentifier(file.name)
                    if (!inUse.containsKey(id)) {
                        if (log.isInfoEnabled) {
                            log.info(("Deleting old file " + file.absolutePath +
                                    " modified: " + Timestamp(lastModified).toString() +
                                    " length: " + file.length()))
                        }
                        if (!file.delete()) {
                            log.warn("Failed to delete old file " + file.absolutePath)
                        }
                        count++
                    }
                }
            }
        } else if (file.isDirectory) {
            var list = file.listFiles()
            if (list != null) for (f in list) {
                count += deleteOlderRecursive(f, min)
            }

            // JCR-1396: FileDataStore Garbage Collector and empty directories
            // Automatic removal of empty directories (but not the root!)
            synchronized(this) {
                if (file !== this.directory) {
                    list = file.listFiles()
                    if (list != null && list.isEmpty()) {
                        file.delete()
                    }
                }
            }
        }
        return count
    }

    private fun listRecursive(list: MutableList<File>, file: File) {
        val files = file.listFiles()
        if (files != null) {
            for (f in files) {
                if (f.isDirectory) {
                    listRecursive(list, f)
                } else {
                    list.add(f)
                }
            }
        }
    }

    override fun clearInUse() {
        inUse.clear()
    }

    override fun close() {
        // nothing to do
    }

    companion object {

        /**
         * Logger instance
         */
        private val log = LoggerFactory.getLogger(FileDataStore::class.java)

        /**
         * The digest algorithm used to uniquely identify records.
         */
        private const val DIGEST = "SHA-1"

        /**
         * The default value for the minimum object size.
         */
        private const val DEFAULT_MIN_RECORD_LENGTH = 100

        /**
         * The maximum last modified time resolution of the file system.
         */
        private const val ACCESS_TIME_RESOLUTION = 2000

        /**
         * Name of the directory used for temporary files.
         * Must be at least 3 characters.
         */
        private const val TMP = "tmp"

        /**
         * Get the last modified date of a file.
         *
         * @param file the file
         * @return the last modified date
         * @throws DataStoreException if reading fails
         */
        @Throws(DataStoreException::class)
        private fun getLastModified(file: File): Long {
            val lastModified = file.lastModified()
            if (lastModified == 0L) {
                throw DataStoreException("Failed to read record modified date: " + file.absolutePath)
            }
            return lastModified
        }

        /**
         * Set the last modified date of a file, if the file is writable.
         *
         * @param file the file
         * @param time the new last modified date
         * @throws DataStoreException if the file is writable but modifying the date fails
         */
        @Throws(DataStoreException::class)
        private fun setLastModified(file: File, time: Long) {
            if (!file.setLastModified(time)) {
                if (!file.canWrite()) {
                    // if we can't write to the file, so garbage collection will also not delete it
                    // (read only files or file systems)
                    return
                }
                try {
                    // workaround for Windows: if the file is already open for reading
                    // (in this or another process), then setting the last modified date
                    // doesn't work - see also JCR-2872
                    val r = RandomAccessFile(file, "rw")
                    r.use {
                        it.setLength(it.length())
                    }
                } catch (e: IOException) {
                    throw DataStoreException("An IO Exception occurred while trying to set the last modified date: " + file.absolutePath, e)
                }

            }
        }
    }

}

