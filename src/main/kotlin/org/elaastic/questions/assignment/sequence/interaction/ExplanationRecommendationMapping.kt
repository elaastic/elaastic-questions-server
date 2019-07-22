package org.elaastic.questions.assignment.sequence.interaction

/**
 * @author John Tranier
 */
// TODO This type should be clarified...
class ExplanationRecommendationMapping : HashMap<String, List<Long>> {

    constructor() : super()

    constructor(value: Map<String, List<Long>>) : super(value)

}