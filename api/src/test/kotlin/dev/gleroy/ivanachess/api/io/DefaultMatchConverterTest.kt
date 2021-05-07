@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.core.GameEntity
import dev.gleroy.ivanachess.core.Match
import dev.gleroy.ivanachess.core.User
import dev.gleroy.ivanachess.game.Game
import dev.gleroy.ivanachess.game.Piece
import dev.gleroy.ivanachess.io.ColorRepresentation
import dev.gleroy.ivanachess.io.GameRepresentation
import dev.gleroy.ivanachess.io.PieceRepresentation
import dev.gleroy.ivanachess.io.PositionRepresentation
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultMatchConverterTest {
    private val match = Match(
        entity = GameEntity(
            whitePlayer = User(
                pseudo = "white",
                email = "white@ivanachess.loc",
                bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
            ),
            blackPlayer = User(
                pseudo = "black",
                email = "black@ivanachess.loc",
                bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
            )
        )
    )

    private val moveConverter = DefaultMoveConverter()
    private val userConverter = DefaultUserConverter()
    private val converter = DefaultMatchConverter(
        gameConverter = DefaultGameConverter(
            userConverter = userConverter,
        ),
        moveConverter = moveConverter,
    )

    @Nested
    inner class convertToRepresentation {
        private val gameRepresentation = GameRepresentation.Complete(
            id = match.entity.id,
            creationDate = match.entity.creationDate,
            whitePlayer = userConverter.convertToPublicRepresentation(match.entity.whitePlayer),
            blackPlayer = userConverter.convertToPublicRepresentation(match.entity.blackPlayer),
            turnColor = ColorRepresentation.White,
            state = GameRepresentation.State.InGame,
            winnerColor = null,
            pieces = setOf(
                PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.Rook,
                    pos = PositionRepresentation(1, 1)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.Knight,
                    pos = PositionRepresentation(2, 1)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.Bishop,
                    pos = PositionRepresentation(3, 1)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.Queen,
                    pos = PositionRepresentation(4, 1)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.King,
                    pos = PositionRepresentation(5, 1)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.Bishop,
                    pos = PositionRepresentation(6, 1)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.Knight,
                    pos = PositionRepresentation(7, 1)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.Rook,
                    pos = PositionRepresentation(8, 1)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(1, 2)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(2, 2)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(3, 2)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(4, 2)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(5, 2)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(6, 2)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(7, 2)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(8, 2)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.Black,
                    type = PieceRepresentation.Type.Rook,
                    pos = PositionRepresentation(1, 8)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.Black,
                    type = PieceRepresentation.Type.Knight,
                    pos = PositionRepresentation(2, 8)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.Black,
                    type = PieceRepresentation.Type.Bishop,
                    pos = PositionRepresentation(3, 8)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.Black,
                    type = PieceRepresentation.Type.Queen,
                    pos = PositionRepresentation(4, 8)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.Black,
                    type = PieceRepresentation.Type.King,
                    pos = PositionRepresentation(5, 8)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.Black,
                    type = PieceRepresentation.Type.Bishop,
                    pos = PositionRepresentation(6, 8)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.Black,
                    type = PieceRepresentation.Type.Knight,
                    pos = PositionRepresentation(7, 8)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.Black,
                    type = PieceRepresentation.Type.Rook,
                    pos = PositionRepresentation(8, 8)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.Black,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(1, 7)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.Black,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(2, 7)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.Black,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(3, 7)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.Black,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(4, 7)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.Black,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(5, 7)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.Black,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(6, 7)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.Black,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(7, 7)
                ),
                PieceRepresentation(
                    color = ColorRepresentation.Black,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(8, 7)
                ),
            ),
            moves = emptyList(),
            possibleMoves = match.game.nextPossibleMoves
                .map { moveConverter.convertToRepresentation(it.move) }
                .toSet()
        )

        @Test
        fun `should return in_game representation`() {
            converter.convertToRepresentation(match) shouldBe gameRepresentation
        }

        @Test
        fun `should return checkmate representation`() {
            converter.convertToRepresentation(
                item = match.copy(
                    entity = match.entity.copy(
                        state = Game.State.Checkmate,
                        winnerColor = Piece.Color.White
                    )
                )
            ) shouldBe gameRepresentation.copy(
                state = GameRepresentation.State.Checkmate,
                winnerColor = ColorRepresentation.White
            )
        }

        @Test
        fun `should return stalemate representation`() {
            converter.convertToRepresentation(
                item = match.copy(
                    entity = match.entity.copy(state = Game.State.Stalemate)
                )
            ) shouldBe gameRepresentation.copy(state = GameRepresentation.State.Stalemate)
        }
    }
}
