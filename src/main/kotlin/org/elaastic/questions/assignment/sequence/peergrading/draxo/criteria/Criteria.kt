package org.elaastic.questions.assignment.sequence.peergrading.draxo.criteria

import org.elaastic.questions.assignment.sequence.peergrading.draxo.option.OptionId as Id
import org.elaastic.questions.assignment.sequence.peergrading.draxo.option.OptionSpecification
import org.elaastic.questions.assignment.sequence.peergrading.draxo.option.OptionType as Type
import java.math.BigDecimal

enum class Criteria(val scale: Map<Id, OptionSpecification>) {

    D(
        mapOf(
            Id.NO to OptionSpecification(Type.NEGATIVE),
            Id.PARTIALLY to OptionSpecification(Type.NEGATIVE),
            Id.YES to OptionSpecification(Type.POSITIVE),
        )
    ),
    R(
        mapOf(
            Id.NO to OptionSpecification(Type.NEGATIVE, BigDecimal(1)),
            Id.PARTIALLY to OptionSpecification(Type.NEGATIVE, BigDecimal("1.5")),
            Id.YES to OptionSpecification(Type.POSITIVE, BigDecimal(2)),
            Id.DONT_KNOW to OptionSpecification(Type.UNKNOWN)
        )
    ),
    A(
        mapOf(
            Id.NO to OptionSpecification(Type.NEGATIVE),
            Id.PARTIALLY to OptionSpecification(Type.NEGATIVE, BigDecimal(1)),
            Id.YES to OptionSpecification(Type.POSITIVE, BigDecimal(2)),
            Id.NO_OPINION to OptionSpecification(Type.UNKNOWN),
        )
    ),
    X(
        mapOf(
            Id.NO to OptionSpecification(Type.NEGATIVE),
            Id.YES to OptionSpecification(Type.POSITIVE, BigDecimal("0.5")),
            Id.DONT_KNOW to OptionSpecification(Type.UNKNOWN),
        )
    ),
    O(
        mapOf(
            Id.YES to OptionSpecification(Type.NEGATIVE),
            Id.NO to OptionSpecification(Type.POSITIVE, BigDecimal("0.5")),
            Id.DONT_KNOW to OptionSpecification(Type.UNKNOWN)
        )
    );

    fun getMessageI18nKey(key: CriteriaMessageKey) =
        "draxo.criteria.$name.${key.code}"


    fun next() =
        when (ordinal) {
            values().size - 1 -> null
            else -> values()[ordinal + 1]
        }

    operator fun get(optionId: Id) =
        if (!scale.containsKey(optionId)) {
            error("$optionId is not a valid option for criteria $name")
        } else optionId

    fun isNegativeOption(optionId: Id) =
        scale.getValue(optionId).type == Type.NEGATIVE

    fun isPositiveOption(optionId: Id) =
        scale.getValue(optionId).type == Type.POSITIVE

    fun getOptionType(optionId: Id?) =
        if(optionId == null) null else scale.getValue(optionId).type

    fun value(optionId: Id?) =
        if (optionId == null)
            BigDecimal(0)
        else
            scale.getValue(optionId).value
}
