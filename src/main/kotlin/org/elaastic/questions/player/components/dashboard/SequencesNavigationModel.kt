package org.elaastic.questions.player.components.dashboard

/**
 * Model for the sequences' navigation.
 *
 * @property currentSequenceStatement the statement of the current sequence
 * @property nextSequenceId the id of the next sequence use null if there is no next sequence
 * @property previousSequenceId the id of the previous sequence use null if there is no previous sequence
 */
class SequencesNavigationModel(
    val currentSequenceStatement: String,
    val nextSequenceId: Long ? = null,
    val previousSequenceId: Long ? = null
)
