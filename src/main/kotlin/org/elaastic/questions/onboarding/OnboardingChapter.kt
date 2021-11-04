package org.elaastic.questions.onboarding

import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.EntityListeners

enum class OnboardingChapter(val propertyString: String) {
    COURSE_PAGE("course_page"),
    COURSE_CREATION_PAGE("course_creation_page"),
    SUBJECT_PAGE("subject_page"),
    SUBJECT_CREATION_PAGE("subject_creation_page"),
    QUESTION_CREATION_PAGE("question_creation_page"),
    SUBJECT_EDITION_PAGE("subject_edition_page"),
    ASSIGNMENT_CREATION_PAGE("assignment_creation_page"),
    PLAYER_PAGE("player_page"),
    SHARED_SUBJECTS_PAGE("shared_subjects_page"),
    ONE_SHARED_SUBJECT_PAGE("one_shared_subject_page");

    companion object {
        fun from(findValue: String): OnboardingChapter = values().first { it.propertyString == findValue }
    }
}