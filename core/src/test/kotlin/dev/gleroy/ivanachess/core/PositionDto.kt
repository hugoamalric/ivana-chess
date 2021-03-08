package dev.gleroy.ivanachess.core

internal data class PositionDto(
    val col: Int,
    val row: Int
) {
    fun convert() = Position(
        col = col,
        row = row
    )
}
