package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.core.GameEntity
import dev.gleroy.ivanachess.game.Game
import dev.gleroy.ivanachess.io.*
import org.springframework.stereotype.Component

/**
 * Default implementation of game entity converter.
 *
 * @param colorConverter Color converter.
 * @param moveConverter Move converter.
 * @param pieceConverter Piece converter.
 * @param userConverter User converter.
 */
@Component
class DefaultGameConverter(
    private val colorConverter: ColorConverter = DefaultColorConverter(),
    private val moveConverter: MoveConverter = DefaultMoveConverter(),
    private val pieceConverter: PieceConverter = DefaultPieceConverter(),
    private val userConverter: UserConverter = DefaultUserConverter(),
) : GameConverter {
    override fun convertToRepresentation(item: GameEntity) = GameRepresentation.Summary(
        id = item.id,
        whitePlayer = userConverter.convertToRepresentation(item.whitePlayer),
        blackPlayer = userConverter.convertToRepresentation(item.blackPlayer),
        turnColor = colorConverter.convertToRepresentation(item.turnColor),
        state = item.state.toRepresentation(),
        winnerColor = item.winnerColor?.let { colorConverter.convertToRepresentation(it) },
    )

    /**
     * Convert game state to its representation.
     *
     * @return Representation of game state.
     */
    private fun Game.State.toRepresentation() = when (this) {
        Game.State.InGame -> GameRepresentation.State.InGame
        Game.State.Checkmate -> GameRepresentation.State.Checkmate
        Game.State.Stalemate -> GameRepresentation.State.Stalemate
    }
}
