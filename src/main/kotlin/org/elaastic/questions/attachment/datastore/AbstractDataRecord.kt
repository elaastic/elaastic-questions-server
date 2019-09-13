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
        return other is DataRecord && identifier.equals(other.identifier)
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

