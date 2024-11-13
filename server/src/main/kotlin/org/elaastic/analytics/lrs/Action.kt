package org.elaastic.analytics.lrs

/**
 * An action that can be performed by a user.
 *
 * @property OPEN open action
 * @property CLOSE close action
 * @property START start action
 * @property STOP stop action
 * @property SKIP skip action
 * @property PUBLISH publish action
 * @property UNPUBLISH unpublish action
 * @property UPDATE update action
 * @property LOAD load action
 * @property CLICK click action
 * @property RESTART restart action
 * @property CONSULT consult action
 */
enum class Action(val propertyString: String) {
    OPEN("open"),
    CLOSE("close"),
    START("start"),
    STOP("stop"),
    SKIP("skip"),
    PUBLISH("publish"),
    UNPUBLISH("unpublish"),
    UPDATE("update"),
    LOAD("load"),
    CLICK("click"),
    RESTART("restart"),
    CONSULT("consult");

    companion object {
        fun from(findValue: String): Action = values().first { it.propertyString == findValue }
    }
}