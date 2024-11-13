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

package org.elaastic.filestore

import org.apache.commons.io.input.AutoCloseInputStream

import java.io.*

/**
 * This input stream delays opening the file until the first byte is read, and
 * closes and discards the underlying stream as soon as the end of input has
 * been reached or when the stream is explicitly closed.
 */
class LazyFileInputStream : AutoCloseInputStream {

    /**
     * The file descriptor to use.
     */
    protected val fd: FileDescriptor?

    /**
     * The file to read from.
     */
    protected val file: File?

    /**
     * True if the input stream was opened. It is also set to true if the stream
     * was closed without reading (to avoid opening the file after the stream
     * was closed).
     */
    protected var opened: Boolean = false

    /**
     * Creates a new `LazyFileInputStream` for the given file. If the
     * file is unreadable, a FileNotFoundException is thrown.
     * The file is not opened until the first byte is read from the stream.
     *
     * @param file the file
     * @throws java.io.FileNotFoundException
     */
    @Throws(FileNotFoundException::class)
    constructor(file: File) : super(null) {
        if (!file.canRead()) {
            throw FileNotFoundException(file.path)
        }
        this.file = file
        this.fd = null
    }

    /**
     * Creates a new `LazyFileInputStream` for the given file
     * descriptor.
     * The file is not opened until the first byte is read from the stream.
     *
     * @param fd Obj
     */
    constructor(fd: FileDescriptor) : super(null) {
        this.file = null
        this.fd = fd
    }

    /**
     * Creates a new `LazyFileInputStream` for the given file. If the
     * file is unreadable, a FileNotFoundException is thrown.
     *
     * @param name
     * @throws java.io.FileNotFoundException
     */
    @Throws(FileNotFoundException::class)
    constructor(name: String) : this(File(name)) {
    }

    /**
     * Open the stream if required.
     *
     * @throws java.io.IOException
     */
    @Throws(IOException::class)
    protected fun open() {
        if (!opened) {
            opened = true
            if (fd != null) {
                `in` = FileInputStream(fd)
            } else {
                `in` = FileInputStream(file!!)
            }
        }
    }

    @Throws(IOException::class)
    override fun read(): Int {
        open()
        return super.read()
    }

    @Throws(IOException::class)
    override fun available(): Int {
        open()
        return super.available()
    }

    @Throws(IOException::class)
    override fun close() {
        // make sure the file is not opened afterwards
        opened = true

        // only close the file if it was in fact opened
        if (`in` != null) {
            super.close()
        }
    }

    @Synchronized
    @Throws(IOException::class)
    override fun reset() {
        open()
        super.reset()
    }

    override fun markSupported(): Boolean {
        try {
            open()
        } catch (e: IOException) {
            throw IllegalStateException(e.toString())
        }

        return super.markSupported()
    }

    @Synchronized
    override fun mark(readlimit: Int) {
        try {
            open()
        } catch (e: IOException) {
            throw IllegalStateException(e.toString())
        }

        super.mark(readlimit)
    }

    @Throws(IOException::class)
    override fun skip(n: Long): Long {
        open()
        return super.skip(n)
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray): Int {
        open()
        return super.read(b, 0, b.size)
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        open()
        return super.read(b, off, len)
    }

}
