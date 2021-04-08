package dev.gleroy.ivanachess.game

internal data class PositionDto(
    val col: Int,
    val row: Int
) {
    fun convert() = Position(
        col = col,
        row = row
    )
}
