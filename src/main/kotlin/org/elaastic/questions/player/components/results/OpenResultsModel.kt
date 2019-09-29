package org.elaastic.questions.player.components.results

import org.elaastic.questions.player.PlayerController

data class OpenResultsModel(
        override val sequenceIsStopped: Boolean,
        override val sequenceId: Long,
        override val interactionId: Long,
        override val interactionRank: Int,
        override val hasExplanations: Boolean,
        override val explanationViewerModel: PlayerController.ExplanationViewerModel? = null
) : ResultsModel {
    override fun getHasChoices() = false
}