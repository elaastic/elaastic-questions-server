package org.elaastic.player.explanations

class OpenExplanationViewerModel(
    explanations: List<ExplanationData>,
    alreadySorted: Boolean = false,
    override val studentsIdentitiesAreDisplayable: Boolean = false
) : DefaultExplanationViewerModel(explanations, alreadySorted) {
    override val hasChoice = false
}