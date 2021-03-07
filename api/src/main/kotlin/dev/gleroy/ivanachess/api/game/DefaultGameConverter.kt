package dev.gleroy.ivanachess.api.game

import dev.gleroy.ivanachess.api.user.UserConverter
import dev.gleroy.ivanachess.dto.GameDto
import dev.gleroy.ivanachess.dto.MoveDto
import dev.gleroy.ivanachess.dto.PieceDto
import org.springframework.stereotype.Component

/**
 * Default implementation of game summary converter.
 *
 * @param userConverter User converter.
 */
@Component
class DefaultGameConverter(
    private val userConverter: UserConverter
) : GameConverter {
    override fun convert(gameSummary: GameSummary) = GameDto.Summary(
        id = gameSummary.id,
        whitePlayer = userConverter.convert(gameSummary.whitePlayer),
        blackPlayer = userConverter.convert(gameSummary.blackPlayer),
        turnColor = PieceDto.Color.from(gameSummary.turnColor),
        state = GameDto.State.from(gameSummary.state)
    )

    override fun convert(gameAndSummary: GameAndSummary) = GameDto.Complete(
        id = gameAndSummary.summary.id,
        whitePlayer = userConverter.convert(gameAndSummary.summary.whitePlayer),
        blackPlayer = userConverter.convert(gameAndSummary.summary.blackPlayer),
        turnColor = PieceDto.Color.from(gameAndSummary.summary.turnColor),
        state = GameDto.State.from(gameAndSummary.summary.state),
        pieces = gameAndSummary.game.board.pieces().map { PieceDto.from(it) }.toSet(),
        moves = gameAndSummary.game.moves.map { MoveDto.from(it) },
        possibleMoves = gameAndSummary.game.nextPossibleMoves.map { MoveDto.from(it.move) }.toSet()
    )
}
