package dev.gleroy.ivanachess.api

internal class InvalidRequest(
    val requestDto: Any,
    val responseDto: ErrorDto.Validation
)
