package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.dto.ErrorDto

internal class InvalidRequest(
    val requestDto: Any,
    val responseDto: ErrorDto.Validation
)
