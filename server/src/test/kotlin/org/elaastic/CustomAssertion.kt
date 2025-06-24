package org.elaastic

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue

/*
 * Custom assertion functions for testing.
 */

/**
 * Asserts that the given collection is empty.
 *
 * @param emptyCollection The collection to check.
 * @param message The message to display if the assertion fails.
 */
fun assertIsEmpty(emptyCollection: Collection<*>, message: String = "Collection is not empty") {
    assertTrue(emptyCollection.isEmpty(), message)
}

fun assertIsEmpty(emptyCollection: Map<*, *>, message: String = "Map is not empty") {
    assertTrue(emptyCollection.isEmpty(), message)
}

/**
 * Assert that the given Object is of the expected type.
 *
 * ```kotlin
 * assertInstanceOf<String>("Hello")
 * ```
 *
 * @param actualValue The object to check.
 * @param message The message to display if the assertion fails.
 * @see Assertions.assertInstanceOf
 */
inline fun <reified T> assertInstanceOf(actualValue: Any, message: String = "Value is not of type ${T::class}"):T =
        assertInstanceOf(T::class.java, actualValue, message)