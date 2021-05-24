package org.elaastic.questions.player.phase

import org.springframework.stereotype.Service

@Service
class LearnerPhaseFactoryResolver {

    private val registry: MutableMap<PhaseType, LearnerPhaseFactory> =
        mutableMapOf()

    fun registerFactory(
        phaseType: PhaseType,
        learnerPhaseFactory: LearnerPhaseFactory
    ) = registry.put(phaseType, learnerPhaseFactory)

    fun resolve(phaseType: PhaseType) = registry[phaseType]
        ?: throw IllegalStateException("There is no factory for $phaseType")

}