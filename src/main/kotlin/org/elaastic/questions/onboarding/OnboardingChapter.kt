package org.elaastic.questions.onboarding

import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.EntityListeners

enum class OnboardingChapter(val propertyString: String) {
    INTRODUCTION("introduction"),
    COURSE_CREATION("course_creation"),
    COURSE_CREATION_2("course_creation_2"),
    SUBJECT_CREATION("subject_creation"),
    SUBJECT_CREATION_2("subject_creation_2"),
    QUESTION_CREATION("question_creation"),
    QUESTION_CREATION_2("question_creation_2"),
    ASSIGNMENT_CREATION("assignment_creation"),
    ASSIGNMENT_CREATION_2("assignment_creation_2"),
    ASSIGNMENT_CREATION_3("assignment_creation_3"),
    PREPARE_SEQUENCE("prepare_sequence"),
    CONFIGURE_SEQUENCE("configure_sequence"),
    PLAY_SEQUENCE("play_sequence"),
    DONE("done");

    companion object {
        fun from(findValue: String): OnboardingChapter = values().first { it.propertyString == findValue }
    }
}