package org.elaastic.analytics.lrs

enum class ObjectOfAction(val propertyString: String) {
    CONFIGURE_POPUP("configure_popup"),
    SEQUENCE("sequence"),
    PHASE_1("phase_1"),
    PHASE_2("phase_2"),
    PHASE_3("phase_3"),
    RATIONALES_POPUP("rationales_popup"),
    EXPLANATION_POPUP("explanation_popup"),
    RESULT("results"),
    EXPLANATION_POPUP_PPEER("explanation_popup_ppeer"),
    EXPLANATION_POPUP_PCONF("explanation_popup_pconf"),
    EXPLANATION_POPUP_D("explanation_popup_d"),
    EXPLANATION_POPUP_P1("explanation_popup_p1"),
    RECOMMENDED_RATIONALES_ORDER("recommended_rationales_order"),
    RATIONALES_CORRECT_ANSWERS("rationales_correct_answers"),
    RATIONALES_INCORRECT_ANSWERS_0("rationales_incorrect_answers_0"), // When no choices have been made
    RATIONALES_INCORRECT_ANSWERS_1("rationales_incorrect_answers_1"),
    RATIONALES_INCORRECT_ANSWERS_2("rationales_incorrect_answers_2"),
    RATIONALES_INCORRECT_ANSWERS_3("rationales_incorrect_answers_3"),
    RATIONALES_INCORRECT_ANSWERS_4("rationales_incorrect_answers_4"),
    RATIONALES_INCORRECT_ANSWERS_5("rationales_incorrect_answers_5"),
    RATIONALES_INCORRECT_ANSWERS_6("rationales_incorrect_answers_6"),
    RATIONALES_INCORRECT_ANSWERS_7("rationales_incorrect_answers_7"),
    RATIONALES_INCORRECT_ANSWERS_8("rationales_incorrect_answers_8"),
    RATIONALES_INCORRECT_ANSWERS_9("rationales_incorrect_answers_9"),
    RATIONALES_INCORRECT_ANSWERS_10("rationales_incorrect_answers_10"),
    P1_P2_GRAPH("p1_p2_graph"),
    PCONF_GRAPH("pconf_graph"),
    PPEER_GRAPH("ppeer_graph");

    companion object {
        fun from(findValue: String): ObjectOfAction = values().first { it.propertyString == findValue }
    }
}