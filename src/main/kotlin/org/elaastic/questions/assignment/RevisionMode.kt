package org.elaastic.questions.assignment

/**
 * The revision mode of an assignment.
 *
 * This revision mode is for the **Konsolidation** app.
 *
 * @property NotAtAll The assignment is not meant to be revised.
 * @property AfterTeachings The assignment is meant to be revised after
 *     teachings.
 * @property Immediately The assignment is meant to be revised immediately
 *     after answering.
 */
enum class RevisionMode {
    /** The assignment is not meant to be revised. */
    NotAtAll,

    /** The assignment is meant to be revised after teachings. */
    AfterTeachings,

    /** The assignment is meant to be revised immediately after answering. */
    Immediately
}