package org.elaastic.questions.player.components.steps

object StepsModelFactory {

    fun build(): StepsModel = StepsModel(
            StepsModel.PhaseState.DISABLED,
            StepsModel.PhaseState.DISABLED,
            StepsModel.PhaseState.DISABLED
    )

}