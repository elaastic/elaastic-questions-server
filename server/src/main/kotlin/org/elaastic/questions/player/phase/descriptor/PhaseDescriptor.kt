package org.elaastic.questions.player.phase.descriptor

import org.elaastic.questions.player.phase.LearnerPhaseType

class PhaseDescriptor(
    val type: LearnerPhaseType,
    val config: PhaseConfig? = null
)