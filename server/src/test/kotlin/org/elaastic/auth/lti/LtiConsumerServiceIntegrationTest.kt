/*
 * Elaastic - formative assessment system
 * Copyright (C) 2020. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.auth.lti

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.charset.Charset
import java.util.logging.Logger
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class LtiConsumerServiceIntegrationTest(
    @Autowired val ltiConsumerService: LtiConsumerService,
    @Autowired val ltiConsumerRepository: LtiConsumerRepository
) {

    internal var logger = Logger.getLogger(LtiConsumerServiceIntegrationTest::class.java.name)

    @Test
    fun generateLtiConsumerListFromCSVFile() {
        // given an input stream reader on a CSV file
        val fileReader = FileReader("src/test/resources/lti-samples.csv", Charset.forName("UTF-8"))
        // and a suffix
        val suffix = ".elaastic.org"
        // when we trigger the list generation with a suffix
        val ltiList = ltiConsumerService.generateLtiConsumerListFromCSVFile(fileReader, suffix)
        //then the list contains 3 elements
        assertEquals(3, ltiList.size)
        assertEquals("0541357G.elaastic.org", ltiList[0].key)
        assertEquals("LPP  SAINT ÉTIENNE ; site de Méjanès", ltiList[0].consumerName)
        assertEquals("0570100Z.elaastic.org", ltiList[1].key)
        assertEquals("LP Simon LAZARD", ltiList[1].consumerName)
        assertEquals("0540015Y.elaastic.org", ltiList[2].key)
        assertEquals("LP ENTRE MEURTHE ET SANON", ltiList[2].consumerName)
        ltiList.forEach {
            assertNotNull(it.secret)
            logger.severe("secret: ${it.secret}")
        }
    }

    @Test
    fun generateLtiConsumerListFromCSVFileWithExistingLtiConsumer() {
        // given an input stream reader on a CSV file
        val fileReader = FileReader("src/test/resources/lti-samples.csv", Charset.forName("UTF-8"))
        // and a lti consumer already saved
        LtiConsumer("LP Simon LAZARD", "already saved secret", "0570100Z.elaastic.org" ).let {
            ltiConsumerRepository.save(it)
        }
        // and a suffix
        val suffix = ".elaastic.org"
        // when we trigger the list generation with a suffix
        val ltiList = ltiConsumerService.generateLtiConsumerListFromCSVFile(fileReader, suffix)
        //then the list contains 3 elements
        assertEquals(3, ltiList.size)
        assertEquals("0541357G.elaastic.org", ltiList[0].key)
        assertEquals("LPP  SAINT ÉTIENNE ; site de Méjanès", ltiList[0].consumerName)
        assertEquals("0570100Z.elaastic.org", ltiList[1].key)
        assertEquals("LP Simon LAZARD", ltiList[1].consumerName)
        assertEquals("already saved secret", ltiList[1].secret)
        assertEquals("0540015Y.elaastic.org", ltiList[2].key)
        assertEquals("LP ENTRE MEURTHE ET SANON", ltiList[2].consumerName)
        ltiList.forEach {
            assertNotNull(it.secret)
            logger.severe("secret: ${it.secret}")
        }
    }

    @Test
    fun generateLtiConsumerCSVFileFromList() {
        // given an input stream reader on a CSV file
        val fileReader = FileReader("src/test/resources/lti-samples.csv")
        // and a suffix
        val suffix = ".elaastic.org"
        // and the generated list
        val ltiList = ltiConsumerService.generateLtiConsumerListFromCSVFile(fileReader, suffix)
        // and the target file
        val fileWriter = FileWriter("src/test/resources/lti-consumers-export-file.csv")
        //when we generate the export file
        ltiConsumerService.printLtiConsumerListInCsvFile(ltiList,fileWriter)
        assertEquals(3, ltiList.size)
        try {
            fileWriter.flush()
            fileWriter.close()
        } catch (e: IOException) {
            logger.severe("Error while flushing/closing fileWriter !!!")
            logger.severe(e.message)
        }
    }
}
