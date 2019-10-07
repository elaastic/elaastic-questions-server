package org.elaastic.questions.lti

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

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

}
