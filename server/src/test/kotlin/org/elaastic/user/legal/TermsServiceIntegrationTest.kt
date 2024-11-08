package org.elaastic.user.legal

import org.elaastic.test.directive.tExpect
import org.elaastic.test.directive.tGiven
import org.elaastic.test.directive.tThen
import org.elaastic.test.directive.tWhen
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*
import java.util.logging.Logger
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class TermsServiceIntegrationTest(
    @Autowired val termsService: TermsService,
    @Autowired val entityManager: EntityManager
) {

    internal val logger = Logger.getLogger(TermsServiceIntegrationTest::class.java.name)

    @Test
    fun testTermsAndTermsContentCreation() {
        tGiven {
            // valid terms and terms contents
            Terms().let {
                TermsContent("Terms in english", it, "en")
                TermsContent("Terms en français", it)
                it
            }.tWhen {
                // saving the terms
                termsService.save(it)
            }.tThen {
                // Terms object is saved as expected
                entityManager.refresh(it)
                MatcherAssert.assertThat(it.id, CoreMatchers.notNullValue())
                MatcherAssert.assertThat(it.version, CoreMatchers.equalTo(0L))
                MatcherAssert.assertThat(it.startDate, CoreMatchers.notNullValue())
                MatcherAssert.assertThat(it.endDate, CoreMatchers.nullValue())
                Assertions.assertTrue(it.isActive)
                it
            }.tExpect {
                // fetching terms contents by language as expected
                it.termsContentsByLanguage["fr"].let { tc ->
                    MatcherAssert.assertThat(tc!!.id, CoreMatchers.notNullValue())
                    MatcherAssert.assertThat(tc.content, CoreMatchers.equalTo("Terms en français"))
                }
                it.termsContentsByLanguage["en"].let { tc ->
                    MatcherAssert.assertThat(tc!!.id, CoreMatchers.notNullValue())
                    MatcherAssert.assertThat(tc.content, CoreMatchers.equalTo("Terms in english"))
                }
            }
        }
    }

    @Test
    fun testFindByActiveTerms() {
        tGiven {
            // a non active terms
            listOf<Terms>(
                Terms(startDate = Date(), isActive = false).let {
                    TermsContent("Terms in english", it, "en")
                    TermsContent("Terms en français", it)
                    it
                },
                // and an active terms
                Terms().let {
                    TermsContent("Active Terms in english", it, "en")
                    TermsContent("Terms en français", it)
                    it
                }
            ).forEach {
                termsService.save(it)
            }.tExpect {
                termsService.findActive()!!.let {
                    entityManager.refresh(it)
                    Assertions.assertTrue(it.isActive)
                    logger.info(it.termsContentsByLanguage["en"]?.content)
                    Assertions.assertTrue(it.termsContentsByLanguage["en"]?.content?.startsWith("Active") ?: false)
                }
            }

        }
    }

    @Test
    fun testUpdateOfInactiveTermsWhenANewOneIsComing() {
        tGiven {
            listOf(
                // a terms
                Terms().let {
                    TermsContent("Terms in english", it, "en")
                    TermsContent("Terms en français", it)
                    termsService.save(it)
                },
                // and a new one
                Terms().let {
                    TermsContent("New Terms in english", it, "en")
                    TermsContent("Nouveaux Terms en français", it)
                    termsService.save(it)
                }.tWhen {
                    // updating now inactive terms
                    termsService.updateInactiveTermsList(it)
                    it
                })
        }.tThen {
            MatcherAssert.assertThat(it[0].endDate, CoreMatchers.equalTo(it[1].startDate))
            Assertions.assertFalse(it[0].isActive)
            MatcherAssert.assertThat(it[1].endDate, CoreMatchers.nullValue())
            Assertions.assertTrue(it[1].isActive)
        }
    }

    @Test
    fun testGetTermsContentByLanguage() {
        tGiven {
            // the active terms
            termsService.getActive()
        }.tExpect {
            // french and content is expectable
            MatcherAssert.assertThat(
                termsService.getTermsContentByLanguage("fr"),
                CoreMatchers.equalTo(it.termsContentsByLanguage["fr"]!!.content)
            )
            MatcherAssert.assertThat(
                termsService.getTermsContentByLanguage("en"),
                CoreMatchers.equalTo(it.termsContentsByLanguage["en"]!!.content)
            )
            // German language is not supported and then terms are in english
            MatcherAssert.assertThat(
                termsService.getTermsContentByLanguage("ge"),
                CoreMatchers.equalTo(it.termsContentsByLanguage["en"]!!.content)
            )
        }
    }

}