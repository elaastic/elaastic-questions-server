package org.elaastic.questions.assignment.sequence.peergrading

import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.directory.User
import java.math.BigDecimal
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

/**
 * LikertPeerGrading is a PeerGrading with a Likert scale.
 *
 * See [Wikipedia](https://en.wikipedia.org/wiki/Likert_scale) for more information about this scale.
 *
 * @see PeerGrading
 */
@Entity
@DiscriminatorValue("LIKERT")
class LikertPeerGrading(
    grader: User,
    response: Response,
    grade: BigDecimal?,
    annotation: String? = null
) : PeerGrading(
    type = PeerGradingType.LIKERT,
    grader = grader,
    response = response,
    grade = grade,
    annotation = annotation
)