package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.dto.GameDto
import dev.gleroy.ivanachess.dto.MoveDto
import dev.gleroy.ivanachess.dto.PieceDto
import org.springframework.stereotype.Component

/**
 * Default implementation of game information converter.
 *
 * @param props Properties.
 */
@Component
class DefaultGameInfoConverter(
    private val props: Properties
) : GameInfoConverter {
    override fun convert(gameInfo: GameInfo) = GameDto(
        id = gameInfo.id,
        whiteToken = gameInfo.whiteToken,
        blackToken = gameInfo.blackToken,
        turnColor = PieceDto.Color.from(gameInfo.game.turnColor),
        state = GameDto.State.from(gameInfo.game.state),
        pieces = gameInfo.game.board.pieces().map { PieceDto.from(it) }.toSet(),
        moves = gameInfo.game.moves.map { MoveDto.from(it) },
        possibleMoves = gameInfo.game.nextPossibleMoves.map { MoveDto.from(it.move) }.toSet()
    )
}
