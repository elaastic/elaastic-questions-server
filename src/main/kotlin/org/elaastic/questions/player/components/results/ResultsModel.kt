package org.elaastic.questions.player.components.results

import org.elaastic.questions.player.PlayerController

interface ResultsModel {
    val sequenceIsStopped: Boolean
    val sequenceId: Long
    val interactionId: Long
    val interactionRank: Int
    fun getHasChoices(): Boolean
    val hasExplanations: Boolean
    val explanationViewerModel: PlayerController.ExplanationViewerModel?
}