/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.activity.evaluation.peergrading.draxo

import org.elaastic.activity.evaluation.peergrading.draxo.criteria.Criteria
import org.elaastic.activity.evaluation.peergrading.draxo.criteria.CriteriaMessageKey
import org.elaastic.activity.evaluation.peergrading.draxo.option.Option
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

/**
 * Warning : this service is actually used within Thymeleaf template.
 * It'd use to describe a DRAXO peer grading.
 */
@Service
class DraxoService(
    @Autowired val messageSource: MessageSource,
) {

    fun getCriteriaList() = Criteria.values()

    /**
     * Construct this map : { "header" : { 'D': "translated header for D", ...
     * }, "question": { ... } }
     */
    fun getCriteriaMessages() =
        CriteriaMessageKey.values()
            .associate { messageKey ->
                messageKey.code to
                        Criteria.values()
                            .associateWith { criteria ->
                                messageSource.getMessage(
                                    criteria.getMessageI18nKey(messageKey),
                                    null,
                                    LocaleContextHolder.getLocale()
                                )
                            }
            }

    fun getCriteriaScales() =
        Criteria.values()
            .associateWith { criteria ->
                criteria.scale.map { (optionId, optionSpecification) ->
                    Option(
                        optionId,
                        optionSpecification.type,
                        messageSource.getMessage(
                            optionId.codeI18n,
                            null,
                            LocaleContextHolder.getLocale()
                        )
                    )
                }
            }
}
