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
package org.elaastic.questions.assignment.sequence.peergrading.draxo

import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.peergrading.PeerGrading
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingType
import org.elaastic.questions.directory.User
import org.elaastic.questions.assignment.sequence.peergrading.draxo.criteria.Criteria
import org.elaastic.questions.assignment.sequence.peergrading.draxo.option.OptionId
import javax.persistence.*
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Entity
@DiscriminatorValue("DRAXO")
@ValidateDraxoPeerGrading
class DraxoPeerGrading(
    grader: User,
    response: Response,

    @Enumerated(EnumType.STRING)
    @Column(name = "criteria_D")
    var criteriaD: OptionId? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "criteria_R")
    var criteriaR: OptionId? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "criteria_A")
    var criteriaA: OptionId? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "criteria_X")
    var criteriaX: OptionId? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "criteria_O")
    var criteriaO: OptionId? = null,

    annotation: String? = null,

    lastSequencePeerGrading: Boolean
) : PeerGrading(
    type = PeerGradingType.DRAXO,
    grader = grader,
    response = response,
    annotation = annotation,
    grade = DraxoGrading.computeGrade(criteriaD, criteriaR, criteriaA, criteriaX, criteriaO),
    lastSequencePeerGrading = lastSequencePeerGrading
) {

    constructor(
        grader: User,
        response: Response,
        draxoEvaluation: DraxoEvaluation,
        lastSequencePeerGrading: Boolean
    ) : this(
        grader,
        response,
        criteriaD = draxoEvaluation.criteriaValuation[Criteria.D]?.optionId,
        criteriaR = draxoEvaluation.criteriaValuation[Criteria.R]?.optionId,
        criteriaA = draxoEvaluation.criteriaValuation[Criteria.A]?.optionId,
        criteriaX = draxoEvaluation.criteriaValuation[Criteria.X]?.optionId,
        criteriaO = draxoEvaluation.criteriaValuation[Criteria.O]?.optionId,
        annotation = draxoEvaluation.getExplanation(),
        lastSequencePeerGrading = lastSequencePeerGrading
    )

    operator fun set(criteria: Criteria, optionId: OptionId?) {
        when (criteria) {
            Criteria.D -> this.criteriaD = optionId
            Criteria.R -> this.criteriaR = optionId
            Criteria.A -> this.criteriaA = optionId
            Criteria.X -> this.criteriaX = optionId
            Criteria.O -> this.criteriaO = optionId

        }
    }

    operator fun get(criteria: Criteria) =
        when (criteria) {
            Criteria.D -> this.criteriaD
            Criteria.R -> this.criteriaR
            Criteria.A -> this.criteriaA
            Criteria.X -> this.criteriaX
            Criteria.O -> this.criteriaO
        }

    fun updateFrom(draxoEvaluation: DraxoEvaluation) {
        Criteria.values().forEach { criteria -> this[criteria] = draxoEvaluation[criteria] }
    }

    @Transient
    fun getDraxoEvaluation() =
        DraxoEvaluation().also { draxoEvaluation ->
            Criteria.values().forEach { criteria ->
                this[criteria]?.let { optionId ->
                    draxoEvaluation.addEvaluation(
                        criteria,
                        optionId,
                        if (criteria.isNegativeOption(optionId)) annotation else null
                    )
                }
            }
        }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [DraxoPeerGradingValidator::class])
annotation class ValidateDraxoPeerGrading(
    val message: String = "Invalid DRAXO Peer Grading",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class DraxoPeerGradingValidator : ConstraintValidator<ValidateDraxoPeerGrading, DraxoPeerGrading> {
    override fun isValid(draxoPeerGrading: DraxoPeerGrading?, context: ConstraintValidatorContext?): Boolean {
        check(draxoPeerGrading != null)
        return draxoPeerGrading.getDraxoEvaluation().isValid()
    }
}