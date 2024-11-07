package org.elaastic.sequence.phase.descriptor

import org.elaastic.sequence.phase.LearnerPhaseType

class PhaseDescriptor(
    val type: LearnerPhaseType,
    val config: PhaseConfig? = null
)