package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.dto.GameDto
import dev.gleroy.ivanachess.dto.MoveDto
import dev.gleroy.ivanachess.dto.PieceDto
import org.springframework.stereotype.Component

/**
 * Default implementation of game summary converter.
 */
@Component
class DefaultGameConverter : GameConverter {
    override fun convert(gameSummary: GameSummary) = GameDto.Summary(
        id = gameSummary.id,
        whiteToken = gameSummary.whiteToken,
        blackToken = gameSummary.blackToken,
        turnColor = PieceDto.Color.from(gameSummary.turnColor),
        state = GameDto.State.from(gameSummary.state)
    )

    override fun convert(gameAndSummary: GameAndSummary) = GameDto.Complete(
        id = gameAndSummary.summary.id,
        whiteToken = gameAndSummary.summary.whiteToken,
        blackToken = gameAndSummary.summary.blackToken,
        turnColor = PieceDto.Color.from(gameAndSummary.summary.turnColor),
        state = GameDto.State.from(gameAndSummary.summary.state),
        pieces = gameAndSummary.game.board.pieces().map { PieceDto.from(it) }.toSet(),
        moves = gameAndSummary.game.moves.map { MoveDto.from(it) },
        possibleMoves = gameAndSummary.game.nextPossibleMoves.map { MoveDto.from(it.move) }.toSet()
    )
}
