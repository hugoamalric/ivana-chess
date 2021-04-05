package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.api.game.GameEntity
import dev.gleroy.ivanachess.api.game.Match
import dev.gleroy.ivanachess.core.Game
import dev.gleroy.ivanachess.dto.GameDto
import org.springframework.stereotype.Component

/**
 * Default implementation of game entity converter.
 *
 * @param moveConverter Move converter.
 * @param pieceConverter Piece converter.
 * @param userConverter User converter.
 */
@Component
class DefaultGameConverter(
    private val moveConverter: MoveConverter = DefaultMoveConverter(),
    private val pieceConverter: PieceConverter = DefaultPieceConverter(),
    private val userConverter: UserConverter = DefaultUserConverter()
) : GameConverter {
    override fun convertToSummaryDto(gameEntity: GameEntity) = GameDto.Summary(
        id = gameEntity.id,
        whitePlayer = userConverter.convertToDto(gameEntity.whitePlayer),
        blackPlayer = userConverter.convertToDto(gameEntity.blackPlayer),
        turnColor = pieceConverter.convertColorToDto(gameEntity.turnColor),
        state = gameEntity.state.toDto(),
        winnerColor = gameEntity.winnerColor?.let { pieceConverter.convertColorToDto(it) }
    )

    override fun convertToCompleteDto(match: Match) = GameDto.Complete(
        id = match.entity.id,
        whitePlayer = userConverter.convertToDto(match.entity.whitePlayer),
        blackPlayer = userConverter.convertToDto(match.entity.blackPlayer),
        turnColor = pieceConverter.convertColorToDto(match.entity.turnColor),
        state = match.entity.state.toDto(),
        winnerColor = match.entity.winnerColor?.let { pieceConverter.convertColorToDto(it) },
        pieces = match.game.board.pieces().map { pieceConverter.convertToDto(it.piece, it.pos) }.toSet(),
        moves = match.game.moves.map { moveConverter.convertToDto(it) },
        possibleMoves = match.game.nextPossibleMoves.map { moveConverter.convertToDto(it.move) }.toSet()
    )

    /**
     * Convert game state to DTO.
     *
     * @return DTO.
     */
    private fun Game.State.toDto() = when (this) {
        Game.State.InGame -> GameDto.State.InGame
        Game.State.Checkmate -> GameDto.State.Checkmate
        Game.State.Stalemate -> GameDto.State.Stalemate
    }
}
