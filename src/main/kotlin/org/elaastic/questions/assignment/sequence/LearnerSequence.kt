/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.elaastic.questions.player.phase.LearnerPhase
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*


@Entity
@EntityListeners(AuditingEntityListener::class)

class LearnerSequence(

    @field:ManyToOne
    override val learner: User,

    @field:ManyToOne
    override val sequence: Sequence,

    @field:ManyToOne
    override var activeInteraction: Interaction? = null
) : AbstractJpaPersistable<Long>(),
    ILearnerSequence,
    SequenceProgress by sequence {

    @Version
    var version: Long? = null

    @Column(name = "date_created")
    @CreatedDate
    lateinit var dateCreated: Date

    @LastModifiedDate
    @Column(name = "last_updated")
    var lastUpdated: Date? = null

    @Transient
    override var phaseList: Array<LearnerPhase?> = arrayOf<LearnerPhase?>(null, null, null)
        get() { // Needed because of JPA using a default empty constructor that bypass the var initialization...
            if (field == null) {
                field = arrayOf<LearnerPhase?>(null, null, null)
            }
            return field
        }

    @Transient
    override fun loadPhase(learnerPhase: LearnerPhase) {
        phaseList[learnerPhase.index - 1] = learnerPhase
    }

    @Transient
    override fun getPhase(index: Int) = phaseList[index]!!
}
