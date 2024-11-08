package org.elaastic.analytics.lrs

import org.elaastic.sequence.ILearnerSequence
import org.elaastic.sequence.Sequence
import org.elaastic.user.Role
import org.elaastic.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class EventLogService(
        @Autowired val eventLogRepository: EventLogRepository
) {

    fun create(sequence: Sequence,
               user: User,
               action: Action,
               obj: ObjectOfAction,
               userAgent : String? = null): EventLog =
            EventLog(
                sequence = sequence,
                user = user,
                role = if (user != sequence.owner) Role.RoleId.STUDENT else Role.RoleId.TEACHER,
                action = action,
                obj = obj,
                userAgent = userAgent
            ).let(eventLogRepository::save)

    fun findById(id: Long): EventLog =
            eventLogRepository.findById(id).get()

    fun save(eventLog: EventLog){
        eventLogRepository.save(eventLog)
    }

    fun saveActionsAfterClosingConfigurePopup(sequence : Sequence){
        create(sequence, sequence.owner, Action.CLOSE, ObjectOfAction.CONFIGURE_POPUP)
        create(sequence, sequence.owner, Action.START, ObjectOfAction.SEQUENCE)
        create(sequence, sequence.owner, Action.START, ObjectOfAction.PHASE_1)
    }

    fun stopSequence(sequence : Sequence){
        create(sequence, sequence.owner, Action.STOP, ObjectOfAction.SEQUENCE)
    }

    fun reopenSequence(sequence : Sequence){
        create(sequence, sequence.owner, Action.RESTART, ObjectOfAction.SEQUENCE)
    }

    fun startPhase(sequence : Sequence, phaseNumber: Int){
        create(sequence, sequence.owner, Action.START, ObjectOfAction.from("phase_$phaseNumber"))
    }

    fun stopPhase(sequence : Sequence, phaseNumber: Int){
        create(sequence, sequence.owner, Action.STOP, ObjectOfAction.from("phase_$phaseNumber"))
    }

    fun skipPhase(sequence : Sequence, phaseNumber: Int){
        create(sequence, sequence.owner, Action.SKIP, ObjectOfAction.from("phase_$phaseNumber"))
    }

    fun restartPhase(sequence : Sequence, phaseNumber: Int){
        create(sequence, sequence.owner, Action.RESTART, ObjectOfAction.from("phase_$phaseNumber"))
    }

    fun publishResults(sequence: Sequence) {
        create(sequence, sequence.owner, Action.PUBLISH, ObjectOfAction.RESULT)
    }

    fun unpublishResults(sequence: Sequence) {
        create(sequence, sequence.owner, Action.UNPUBLISH, ObjectOfAction.RESULT)
    }

    fun refreshResults(sequence: Sequence) {
        create(sequence, sequence.owner, Action.UPDATE, ObjectOfAction.RESULT)
    }

    fun consultResults(sequence: Sequence, user: User, userAgent: String?) {
        create(sequence, user, Action.CONSULT, ObjectOfAction.RESULT, userAgent)
    }

    fun consultPlayer(sequence: Sequence, user: User, learnerSequence: ILearnerSequence, userAgent: String?) {
        if (learnerSequence.sequence.resultsArePublished)
            if (learnerSequence.sequence.executionIsFaceToFace())
                consultResults(sequence, user, userAgent)
            else
                if (learnerSequence.activeInteraction?.isRead() == true)
                    consultResults(sequence, user, userAgent)
    }
}