package org.elaastic.questions.player.phase.descriptor

import org.elaastic.questions.player.phase.PhaseType

class PhaseDescriptor(
    val type: PhaseType,
    val config: PhaseConfig? = null
)