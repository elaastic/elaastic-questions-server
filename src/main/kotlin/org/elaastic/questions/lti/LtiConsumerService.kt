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

package org.elaastic.questions.lti

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.csv.CSVRecord

@Service
class LtiConsumerService(
        @Autowired val ltiConsumerRepository: LtiConsumerRepository
) {

    fun touchLtiConsumer(
            consumerKey: String,
            productName: String?,
            productVersion: String?,
            productGuid: String?,
            ltiVersion: String?) {
        ltiConsumerRepository.findByKey(consumerKey)?.let {
            it.productName = productName
            it.productVersion = productVersion
            it.productGuid = productGuid
            it.ltiVersion = ltiVersion
            it.lastAccess = Date()
            ltiConsumerRepository.save(it)
        }
    }

    /**
     * Generate LTI Consumer list from a CSV File containing the 2 attributes per raw : the key and the name
     * @param fileReader the input stream reader of the CSV file
     * @param suffix optional suffix to add to the key
     * @return the list of saved lti consumer
     */
    fun generateLtiConsumerListFromCSVFile(fileReader: InputStreamReader, suffix: String? = null): List<LtiConsumer> {
        var consumers = ArrayList<LtiConsumer>()
        val records = CSVFormat.DEFAULT.withDelimiter(';').withFirstRecordAsHeader().parse(fileReader)
        for(record in records) {
            var consumerKey = record.get(0)
            suffix?.let {
                consumerKey += suffix
            }
            var consumer = ltiConsumerRepository.findByKey(consumerKey)
            if (consumer == null) {
                val consumerName = if (record.get(1).length <= 45) record.get(1) else record.get(1).substring(0..44);
                val consumerSecret = UUID.randomUUID().toString().substring(0..31)
                LtiConsumer(consumerName, consumerSecret, consumerKey).let {
                    consumer = ltiConsumerRepository.save(it)
                }
            }
            consumers.add(consumer!!)
        }
        return consumers
    }

}
