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

/**
 * Abstract data record base class. This base class contains only
 * a reference to the data identifier of the record and implements
 * the standard [Object] equality, hash code, and string
 * representation methods based on the identifier.
 */
abstract class AbstractDataRecord(override val identifier: DataIdentifier) : DataRecord {

    /**
     * Returns the string representation of the data identifier.
     *
     * @return string representation
     */
    override fun toString(): String {
        return identifier.toString()
    }

    /**
     * Checks if the given object is a data record with the same identifier
     * as this one.
     *
     * @param other other object
     * @return `true` if the other object is a data record and has
     * the same identifier as this one, `false` otherwise
     */
    override fun equals(other: Any?): Boolean {
        return other is DataRecord && identifier == other.identifier
    }

    /**
     * Returns the hash code of the data identifier.
     *
     * @return hash code
     */
    override fun hashCode(): Int {
        return identifier.hashCode()
    }

}

