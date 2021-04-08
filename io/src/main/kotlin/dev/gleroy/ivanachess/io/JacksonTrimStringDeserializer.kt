package dev.gleroy.ivanachess.io

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

/**
 * Trim string implementation of JSON deserializer.
 */
class JacksonTrimStringDeserializer : JsonDeserializer<String>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext) = p.valueAsString?.trim()
}
