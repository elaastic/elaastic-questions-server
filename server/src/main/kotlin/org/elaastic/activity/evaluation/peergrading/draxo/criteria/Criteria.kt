package org.elaastic.activity.evaluation.peergrading.draxo.criteria

import org.elaastic.activity.evaluation.peergrading.draxo.criteria.Criteria.*
import org.elaastic.activity.evaluation.peergrading.draxo.option.OptionSpecification
import java.math.BigDecimal
import org.elaastic.activity.evaluation.peergrading.draxo.option.OptionId as Id
import org.elaastic.activity.evaluation.peergrading.draxo.option.OptionType as Type

/**
 * The criteria of the Draxo evaluation
 *
 * @property D the criteria unDerstandable
 * @property R the criteria Relevant
 * @property A the criteria Agreed
 * @property X the criteria Exhaustive
 * @property O the criteria Optimal
 */
enum class Criteria(val scale: Map<Id, OptionSpecification>) {

    /**
     * unDerstandable
     *
     * See the possible option :
     *
     * | Option    | Type     | Value |
     * |-----------|----------|-------|
     * | NO        | NEGATIVE | 0     |
     * | PARTIALLY | NEGATIVE | 0     |
     * | YES       | POSITIVE | 0     |
     */
    D(
        mapOf(
            Id.NO to OptionSpecification(Type.NEGATIVE),
            Id.PARTIALLY to OptionSpecification(Type.NEGATIVE),
            Id.YES to OptionSpecification(Type.POSITIVE),
        )
    ),

    /**
     * Relevant
     *
     * See the possible option :
     *
     * | Option    | Type     | Value |
     * |-----------|----------|-------|
     * | NO        | NEGATIVE | 1     |
     * | PARTIALLY | NEGATIVE | 1.5   |
     * | YES       | POSITIVE | 2     |
     * | DONT_KNOW | UNKNOWN  | 0     |
     */
    R(
        mapOf(
            Id.NO to OptionSpecification(Type.NEGATIVE, BigDecimal(1)),
            Id.PARTIALLY to OptionSpecification(Type.NEGATIVE, BigDecimal("1.5")),
            Id.YES to OptionSpecification(Type.POSITIVE, BigDecimal(2)),
            Id.DONT_KNOW to OptionSpecification(Type.UNKNOWN)
        )
    ),

    /**
     * Agreed
     *
     * See the possible option :
     *
     * | Option     | Type     | Value |
     * |------------|----------|-------|
     * | NO         | NEGATIVE | 0     |
     * | PARTIALLY  | NEGATIVE | 1     |
     * | YES        | POSITIVE | 2     |
     * | NO_OPINION | UNKNOWN  | 0     |
     */
    A(
        mapOf(
            Id.NO to OptionSpecification(Type.NEGATIVE),
            Id.PARTIALLY to OptionSpecification(Type.NEGATIVE, BigDecimal(1)),
            Id.YES to OptionSpecification(Type.POSITIVE, BigDecimal(2)),
            Id.NO_OPINION to OptionSpecification(Type.UNKNOWN),
        )
    ),

    /**
     * Exhaustive
     *
     * See the possible option :
     *
     * | Option    | Type     | Value |
     * |-----------|----------|-------|
     * | NO        | NEGATIVE | 0     |
     * | YES       | POSITIVE | 0.5   |
     * | DONT_KNOW | UNKNOWN  | 0     |
     */
    X(
        mapOf(
            Id.NO to OptionSpecification(Type.NEGATIVE),
            Id.YES to OptionSpecification(Type.POSITIVE, BigDecimal("0.5")),
            Id.DONT_KNOW to OptionSpecification(Type.UNKNOWN),
        )
    ),

    /**
     * Optimal
     *
     * See the possible option :
     *
     * | Option    | Type     | Value |
     * |-----------|----------|-------|
     * | YES       | NEGATIVE | 0     |
     * | NO        | POSITIVE | 0.5   |
     * | DONT_KNOW | UNKNOWN  | 0     |
     */
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
        if (optionId == null) null else scale.getValue(optionId).type

    /**
     * Get the value of the option if the option is null, return 0.
     *
     * @param optionId the id of the option
     * @return the value of the option
     * @see OptionSpecification.value
     */
    fun value(optionId: Id?) =
        if (optionId == null)
            BigDecimal(0)
        else
            scale.getValue(optionId).value
}
