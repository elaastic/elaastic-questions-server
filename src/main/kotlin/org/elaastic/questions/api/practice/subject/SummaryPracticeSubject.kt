package org.elaastic.questions.api.practice.subject

import com.toedter.spring.hateoas.jsonapi.JsonApiId
import com.toedter.spring.hateoas.jsonapi.JsonApiType
import org.elaastic.questions.assignment.Assignment

open class SummaryPracticeSubject(
    @JsonApiId
    val id: Long,
    val title: String,
) {
    @JsonApiType
    val type = "practice-subject"

    constructor(assignment: Assignment) : this(
        id = assignment.id!!,
        title = assignment.title,
    )
}