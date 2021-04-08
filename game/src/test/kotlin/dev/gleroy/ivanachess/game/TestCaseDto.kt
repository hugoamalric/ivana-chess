package dev.gleroy.ivanachess.game

internal data class TestCaseDto(
    val state: GameStateDto,
    val turnColor: PieceDto.Color,
    val winnerColor: PieceDto.Color? = null,
    val pieces: Set<PieceDto>,
    val moves: List<MoveDto>,
    val possibleMoves: Set<MoveDto>,
)
