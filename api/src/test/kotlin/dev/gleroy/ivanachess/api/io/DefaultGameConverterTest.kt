@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.api.game.GameEntity
import dev.gleroy.ivanachess.api.game.Match
import dev.gleroy.ivanachess.api.user.User
import dev.gleroy.ivanachess.game.Game
import dev.gleroy.ivanachess.game.Piece
import dev.gleroy.ivanachess.io.GameRepresentation
import dev.gleroy.ivanachess.io.PieceRepresentation
import dev.gleroy.ivanachess.io.PositionRepresentation
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultGameConverterTest {
    private val gameEntity = GameEntity(
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

    private val moveConverter = DefaultMoveConverter()
    private val userConverter = DefaultUserConverter()

    private val converter = DefaultGameConverter(
        moveConverter = moveConverter,
        userConverter = userConverter
    )

    @Nested
    inner class convertToSummaryRepresentation {
        private val gameRepresentation = GameRepresentation.Summary(
            id = gameEntity.id,
            whitePlayer = userConverter.convertToRepresentation(gameEntity.whitePlayer),
            blackPlayer = userConverter.convertToRepresentation(gameEntity.blackPlayer),
            turnColor = PieceRepresentation.Color.White,
            state = GameRepresentation.State.InGame,
            winnerColor = null,
        )

        @Test
        fun `should return in_game representation`() {
            converter.convertToSummaryRepresentation(gameEntity) shouldBe gameRepresentation
        }

        @Test
        fun `should return checkmate with white winner representation`() {
            converter.convertToSummaryRepresentation(
                gameEntity.copy(
                    state = Game.State.Checkmate,
                    winnerColor = Piece.Color.White
                )
            ) shouldBe gameRepresentation.copy(
                state = GameRepresentation.State.Checkmate,
                winnerColor = PieceRepresentation.Color.White
            )
        }

        @Test
        fun `should return checkmate with black winner representation`() {
            converter.convertToSummaryRepresentation(
                gameEntity.copy(
                    state = Game.State.Checkmate,
                    winnerColor = Piece.Color.Black
                )
            ) shouldBe gameRepresentation.copy(
                state = GameRepresentation.State.Checkmate,
                winnerColor = PieceRepresentation.Color.Black
            )
        }

        @Test
        fun `should return stalemate representation`() {
            converter.convertToSummaryRepresentation(gameEntity.copy(state = Game.State.Stalemate)) shouldBe gameRepresentation.copy(
                state = GameRepresentation.State.Stalemate
            )
        }
    }

    @Nested
    inner class `convert to complete representation` {
        private val match = Match(
            entity = gameEntity
        )
        private val gameRepresentation = GameRepresentation.Complete(
            id = gameEntity.id,
            whitePlayer = userConverter.convertToRepresentation(gameEntity.whitePlayer),
            blackPlayer = userConverter.convertToRepresentation(gameEntity.blackPlayer),
            turnColor = PieceRepresentation.Color.White,
            state = GameRepresentation.State.InGame,
            winnerColor = null,
            pieces = setOf(
                PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.Rook,
                    pos = PositionRepresentation(1, 1)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.Knight,
                    pos = PositionRepresentation(2, 1)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.Bishop,
                    pos = PositionRepresentation(3, 1)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.Queen,
                    pos = PositionRepresentation(4, 1)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.King,
                    pos = PositionRepresentation(5, 1)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.Bishop,
                    pos = PositionRepresentation(6, 1)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.Knight,
                    pos = PositionRepresentation(7, 1)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.Rook,
                    pos = PositionRepresentation(8, 1)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(1, 2)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(2, 2)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(3, 2)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(4, 2)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(5, 2)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(6, 2)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(7, 2)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(8, 2)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.Rook,
                    pos = PositionRepresentation(1, 8)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.Knight,
                    pos = PositionRepresentation(2, 8)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.Bishop,
                    pos = PositionRepresentation(3, 8)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.Queen,
                    pos = PositionRepresentation(4, 8)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.King,
                    pos = PositionRepresentation(5, 8)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.Bishop,
                    pos = PositionRepresentation(6, 8)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.Knight,
                    pos = PositionRepresentation(7, 8)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.Rook,
                    pos = PositionRepresentation(8, 8)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(1, 7)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(2, 7)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(3, 7)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(4, 7)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(5, 7)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(6, 7)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.Pawn,
                    pos = PositionRepresentation(7, 7)
                ),
                PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
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
            converter.convertToCompleteRepresentation(match) shouldBe gameRepresentation
        }

        @Test
        fun `should return checkmate with white winner representation`() {
            converter.convertToCompleteRepresentation(
                match = match.copy(
                    entity = gameEntity.copy(
                        state = Game.State.Checkmate,
                        winnerColor = Piece.Color.White
                    )
                )
            ) shouldBe gameRepresentation.copy(
                state = GameRepresentation.State.Checkmate,
                winnerColor = PieceRepresentation.Color.White
            )
        }

        @Test
        fun `should return checkmate with black winner representation`() {
            converter.convertToCompleteRepresentation(
                match = match.copy(
                    entity = gameEntity.copy(
                        state = Game.State.Checkmate,
                        winnerColor = Piece.Color.Black
                    )
                )
            ) shouldBe gameRepresentation.copy(
                state = GameRepresentation.State.Checkmate,
                winnerColor = PieceRepresentation.Color.Black
            )
        }

        @Test
        fun `should return stalemate representation`() {
            converter.convertToCompleteRepresentation(
                match = match.copy(
                    entity = gameEntity.copy(state = Game.State.Stalemate)
                )
            ) shouldBe gameRepresentation.copy(state = GameRepresentation.State.Stalemate)
        }
    }
}
