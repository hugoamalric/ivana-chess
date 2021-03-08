package dev.gleroy.ivanachess.core

internal data class TestCaseDto(
    val state: GameStateDto,
    val turnColor: PieceDto.Color,
    val pieces: Set<PieceDto>,
    val moves: List<MoveDto>,
    val possibleMoves: Set<MoveDto>
)
