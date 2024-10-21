package org.elaastic.questions.assignment.sequence.peergrading.draxo

import org.elaastic.questions.assignment.sequence.peergrading.draxo.criteria.Criteria
import org.elaastic.questions.assignment.sequence.peergrading.draxo.criteria.CriteriaMessageKey
import org.elaastic.questions.assignment.sequence.peergrading.draxo.option.Option
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
     * Construct this map :
     * { "header" : { 'D': "translated header for D", ... }, "question": { ... } }
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
                criteria.scale.map {(optionId, optionSpecification) ->
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
