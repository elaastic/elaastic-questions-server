package org.elaastic.questions.assignment.sequence.interaction.response

import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Service
@Transactional
class ResponseService(
        @Autowired val responseRepository: ResponseRepository,
        @Autowired val entityManager: EntityManager
) {

    fun findAll(interaction: Interaction) : ResponseSet =
            ResponseSet(
                    responseRepository.findAllByInteraction(interaction)
            )

    fun updateMeanGrade(response: Response) {
        val meanGrade = entityManager.createQuery("select avg(pg.grade) from PeerGrading pg where pg.response = :response and pg.grade <> -1")
                .setParameter("response", response)
                .singleResult as Float

        response.meanGrade = meanGrade
        responseRepository.save(response)
    }
}