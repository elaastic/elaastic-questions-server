package org.elaastic.questions.assignment.sequence.peergrading.draxo

import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.peergrading.PeerGrading
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingRepository
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingType
import org.springframework.data.jpa.repository.JpaRepository

interface DraxoPeerGradingRepository : JpaRepository<PeerGrading, Long>, PeerGradingRepository {
    override fun findAllByResponseAndType(response: Response, type: PeerGradingType): List<DraxoPeerGrading>

    fun findByIdAndType(id: Long, type: PeerGradingType): DraxoPeerGrading?

    fun countAllByHiddenByTeacherIsFalseAndReportReasonsIsNotEmptyAndResponseIn(responses: List<Response>): Int

    override fun findAllByResponseIn(response: List<Response>): List<DraxoPeerGrading>
}