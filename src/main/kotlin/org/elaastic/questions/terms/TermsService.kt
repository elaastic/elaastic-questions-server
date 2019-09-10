package org.elaastic.questions.terms

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.*
import javax.annotation.PostConstruct
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Service
@Transactional
class TermsService(
        @Autowired val termsRepository: TermsRepository,
        @Autowired val termsContentRepository: TermsContentRepository,
        @Autowired val templateEngine: TemplateEngine
) {

    // will be initialized by addTermsIfNotActiveOneAvailableAndSetIds
    companion object Cache {
        var ACTIVE_TERM_ID: Long = -1
        var TERM_CONTENT_EN_ID: Long = -1
        var TERM_CONTENT_FR_ID: Long = -1
    }

    /**
     * get active terms
     */
    fun getActive(): Terms {
        return termsRepository.getOne(ACTIVE_TERM_ID)
    }


    /**
     * Get terms content by language
     * @param language the expected language
     * @return the terms content in expected language or terms content in english if language is not supported
     */
    fun getTermsContentByLanguage(language: String): String {
        return when(language) {
            Locale.FRENCH.language -> termsContentRepository.getOne(TERM_CONTENT_FR_ID).content
            Locale.ENGLISH.language -> termsContentRepository.getOne(TERM_CONTENT_EN_ID).content
            else -> termsContentRepository.getOne(TERM_CONTENT_EN_ID).content
        }
    }

    @PostConstruct
    @Transactional
    fun addTermsIfNotActiveOneAvailableAndSetIds() {
        var activeTerms = termsRepository.findByIsActive(true)
        if (activeTerms == null) {
            val startDate = Date()
            with(Context()) {
                setVariable("startDate", startDate)
                listOf(
                        templateEngine.process("terms/terms_fr", this),
                        templateEngine.process("terms/terms_en", this)
                )
            }.let {
                Terms(startDate).let { terms ->
                    TermsContent(it[0], terms)
                    TermsContent(it[1], terms, "en")
                    activeTerms = save(terms)
                }
            }
        }
        ACTIVE_TERM_ID = activeTerms!!.id!!
        TERM_CONTENT_EN_ID = activeTerms!!.termsContentsByLanguage["en"]!!.id!!
        TERM_CONTENT_FR_ID = activeTerms!!.termsContentsByLanguage["fr"]!!.id!!
    }

    /**
     * Save the given terms
     * @param terms the given terms
     * @return the saved terms
     */
    internal fun save(terms: Terms): Terms {
        with(terms) {
            termsRepository.save(this)
            termsContentsByLanguage.forEach { (_, tc) ->
                termsContentRepository.save(tc)
            }
            updateInactiveTermsList(this)
        }.let {
            return it
        }
    }

    /**
     * Update inactive terms list when a new active terms has been added
     * @param activeTerms the new active terms
     * @return the active terms
     */
    internal fun updateInactiveTermsList(activeTerms: Terms): Terms {
        termsRepository.findAllByIdIsNot(activeTerms.id!!).forEach {
            with(it) {
                endDate = activeTerms.startDate
                isActive = false
                termsRepository.save(this)
            }
        }
        return activeTerms
    }

    fun findActive(): Terms? {
        return termsRepository.findByIsActive(true)
    }
}
