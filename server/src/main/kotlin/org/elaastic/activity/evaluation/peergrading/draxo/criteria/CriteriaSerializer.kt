package org.elaastic.activity.evaluation.peergrading.draxo.criteria

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException

class CriteriaSerializer: JsonSerializer<Criteria>() {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(criteria: Criteria, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
        jsonGenerator.writeFieldName(criteria.name)
    }
}