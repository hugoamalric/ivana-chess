package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.api.game.GameAndSummary
import dev.gleroy.ivanachess.api.game.GameSummary
import dev.gleroy.ivanachess.core.Game
import dev.gleroy.ivanachess.dto.GameDto
import org.springframework.stereotype.Component

/**
 * Default implementation of game summary converter.
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
    override fun convertToSummaryDto(gameSummary: GameSummary) = GameDto.Summary(
        id = gameSummary.id,
        whitePlayer = userConverter.convertToDto(gameSummary.whitePlayer),
        blackPlayer = userConverter.convertToDto(gameSummary.blackPlayer),
        turnColor = pieceConverter.convertColorToDto(gameSummary.turnColor),
        state = gameSummary.state.toDto()
    )

    override fun convertToCompleteDto(gameAndSummary: GameAndSummary) = GameDto.Complete(
        id = gameAndSummary.summary.id,
        whitePlayer = userConverter.convertToDto(gameAndSummary.summary.whitePlayer),
        blackPlayer = userConverter.convertToDto(gameAndSummary.summary.blackPlayer),
        turnColor = pieceConverter.convertColorToDto(gameAndSummary.summary.turnColor),
        state = gameAndSummary.summary.state.toDto(),
        pieces = gameAndSummary.game.board.pieces().map { pieceConverter.convertToDto(it.piece, it.pos) }.toSet(),
        moves = gameAndSummary.game.moves.map { moveConverter.convertToDto(it) },
        possibleMoves = gameAndSummary.game.nextPossibleMoves.map { moveConverter.convertToDto(it.move) }.toSet()
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
