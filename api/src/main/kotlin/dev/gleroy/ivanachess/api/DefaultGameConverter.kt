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

    override fun convert(gameEntity: GameEntity) = GameDto.Complete(
        id = gameEntity.summary.id,
        whiteToken = gameEntity.summary.whiteToken,
        blackToken = gameEntity.summary.blackToken,
        turnColor = PieceDto.Color.from(gameEntity.summary.turnColor),
        state = GameDto.State.from(gameEntity.summary.state),
        pieces = gameEntity.game.board.pieces().map { PieceDto.from(it) }.toSet(),
        moves = gameEntity.game.moves.map { MoveDto.from(it) },
        possibleMoves = gameEntity.game.nextPossibleMoves.map { MoveDto.from(it.move) }.toSet()
    )
}
