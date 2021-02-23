package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.Game
import dev.gleroy.ivanachess.dto.GameDto
import dev.gleroy.ivanachess.dto.MoveDto
import dev.gleroy.ivanachess.dto.PieceDto
import org.springframework.stereotype.Component

/**
 * Default implementation of game summary converter.
 */
@Component
class DefaultGameSummaryConverter : GameSummaryConverter {
    override fun convert(gameSummary: GameSummary) = GameDto.Summary(
        id = gameSummary.id,
        whiteToken = gameSummary.whiteToken,
        blackToken = gameSummary.blackToken,
        turnColor = PieceDto.Color.from(gameSummary.turnColor),
        state = GameDto.State.from(gameSummary.state)
    )

    override fun convert(gameSummary: GameSummary, game: Game) = GameDto.Complete(
        id = gameSummary.id,
        whiteToken = gameSummary.whiteToken,
        blackToken = gameSummary.blackToken,
        turnColor = PieceDto.Color.from(gameSummary.turnColor),
        state = GameDto.State.from(gameSummary.state),
        pieces = game.board.pieces().map { PieceDto.from(it) }.toSet(),
        moves = game.moves.map { MoveDto.from(it) },
        possibleMoves = game.nextPossibleMoves.map { MoveDto.from(it.move) }.toSet()
    )
}
