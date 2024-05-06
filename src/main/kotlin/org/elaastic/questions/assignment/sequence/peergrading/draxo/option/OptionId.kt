package org.elaastic.questions.assignment.sequence.peergrading.draxo.option

/**
 * OptionId is an enumeration of the possible values for a DraxoPeerGrading criteria.
 *
 * @see org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoPeerGrading
 */
enum class OptionId(val codeI18n: String) {
    YES("common.yes"),
    NO("common.no"),
    PARTIALLY("common.partially"),
    DONT_KNOW("draxo.value.dontKnow"),
    NO_OPINION("draxo.value.noOpinion"),

}