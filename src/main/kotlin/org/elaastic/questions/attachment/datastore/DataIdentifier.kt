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


import java.io.Serializable

/**
 * Opaque data identifier used to identify records in a data store.
 * All identifiers must be serializable and implement the standard
 * object equality and hash code methods.
 */
class DataIdentifier : Serializable {

    /**
     * Data identifier.
     */
    private val identifier: String

    /**
     * Creates a data identifier from the given string.
     *
     * @param identifier data identifier
     */
    constructor(identifier: String) {
        this.identifier = identifier
    }

    /**
     * Creates a data identifier from the hexadecimal string
     * representation of the given bytes.
     *
     * @param identifier data identifier
     */
    constructor(identifier: ByteArray) {
        val buffer = CharArray(identifier.size * 2)
        for (i in identifier.indices) {
            buffer[2 * i] = HEX[(identifier[i].toInt() shr 4) and 0x0f]
            buffer[2 * i + 1] = HEX[identifier[i].toInt() and 0x0f]
        }
        this.identifier = String(buffer)
    }


    /**
     * Returns the identifier string.
     *
     * @return identifier string
     */
    override fun toString(): String {
        return identifier
    }

    /**
     * Checks if the given object is a data identifier and has the same
     * string representation as this one.
     *
     * @param other other object
     * @return `true` if the given object is the same identifier,
     * `false` otherwise
     */
    override fun equals(other: Any?): Boolean {
        return other is DataIdentifier && identifier == other.toString()
    }

    /**
     * Returns the hash code of the identifier string.
     *
     * @return hash code
     */
    override fun hashCode(): Int {
        return identifier.hashCode()
    }

    companion object {

        /**
         * Serial version UID.
         */
        private const val serialVersionUID = -9197191401131100016L

        /**
         * Array of hexadecimal digits.
         */
        private val HEX = "0123456789abcdef".toCharArray()
    }

}
