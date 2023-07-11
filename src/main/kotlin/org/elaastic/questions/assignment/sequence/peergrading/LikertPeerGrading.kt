package org.elaastic.questions.assignment.sequence.peergrading

import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.directory.User
import java.math.BigDecimal
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

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