package org.elaastic.questions.api.practice.subject

import com.toedter.spring.hateoas.jsonapi.JsonApiId
import com.toedter.spring.hateoas.jsonapi.JsonApiType
import org.elaastic.questions.course.Course

/**
 * A practice topic is a folder of practice subjects allowing to organize
 * them.
 *
 * Practice topics are built based on the course notion of Elaastic
 *
 * @author John Tranier
 */
class PracticeTopic(
    @JsonApiId
    val id: Long,
    val title: String
) {
    @JsonApiType
    val type = "practice-topic"

    constructor(course: Course): this(
        id = course.id!!,
        title = course.title
    )
}