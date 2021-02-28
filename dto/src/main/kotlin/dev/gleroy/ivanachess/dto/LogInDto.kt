package dev.gleroy.ivanachess.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

/**
 * Sign-in DTO.
 *
 * @param pseudo User pseudo.
 * @param password User password.
 */
data class LogInDto(
    @JsonDeserialize(using = JacksonTrimStringDeserializer::class)
    val pseudo: String,

    val password: String
)
