package dev.gleroy.ivanachess.io

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

/**
 * Credentials.
 *
 * @param pseudo User pseudo.
 * @param password User password.
 */
data class Credentials(
    @JsonDeserialize(using = JacksonTrimStringDeserializer::class)
    val pseudo: String,

    val password: String,
)
