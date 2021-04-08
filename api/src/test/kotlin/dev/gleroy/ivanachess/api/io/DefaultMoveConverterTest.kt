@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.game.Move
import dev.gleroy.ivanachess.game.Piece
import dev.gleroy.ivanachess.game.Position
import dev.gleroy.ivanachess.io.MoveRepresentation
import dev.gleroy.ivanachess.io.PieceRepresentation
import dev.gleroy.ivanachess.io.PositionRepresentation
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultMoveConverterTest {
    private val posConverter = DefaultPositionConverter()

    private val converter = DefaultMoveConverter(
        posConverter = posConverter
    )

    @Nested
    inner class convertToRepresentation {
        @Test
        fun `should return simple representation`() {
            val move = Move.Simple.fromCoordinates("A1", "A2")
            val moveRepresentation = MoveRepresentation.Simple(
                from = posConverter.convertToRepresentation(move.from),
                to = posConverter.convertToRepresentation(move.to)
            )
            converter.convertToRepresentation(move) shouldBe moveRepresentation
        }

        @Test
        fun `should return promotion representation`() {
            val move = Move.Promotion(
                from = Position.fromCoordinates("A1"),
                to = Position.fromCoordinates("A2"),
                promotion = Piece.Queen(Piece.Color.White)
            )
            val moveRepresentation = MoveRepresentation.Promotion(
                from = posConverter.convertToRepresentation(move.from),
                to = posConverter.convertToRepresentation(move.to),
                promotionColor = PieceRepresentation.Color.White,
                promotionType = PieceRepresentation.Type.Queen
            )
            converter.convertToRepresentation(move) shouldBe moveRepresentation
        }
    }

    @Nested
    inner class convertToMove {
        @Test
        fun `should return simple move`() {
            val moveRepresentation = MoveRepresentation.Simple(
                from = PositionRepresentation(1, 1),
                to = PositionRepresentation(1, 2)
            )
            val move = Move.Simple(
                from = posConverter.convertToPosition(moveRepresentation.from),
                to = posConverter.convertToPosition(moveRepresentation.to)
            )
            converter.convertToMove(moveRepresentation) shouldBe move
        }

        @Test
        fun `should return promotion move`() {
            val moveRepresentation = MoveRepresentation.Promotion(
                from = PositionRepresentation(1, 1),
                to = PositionRepresentation(1, 2),
                promotionColor = PieceRepresentation.Color.White,
                promotionType = PieceRepresentation.Type.Queen
            )
            val move = Move.Promotion(
                from = posConverter.convertToPosition(moveRepresentation.from),
                to = posConverter.convertToPosition(moveRepresentation.to),
                promotion = Piece.Queen(Piece.Color.White)
            )
            converter.convertToMove(moveRepresentation) shouldBe move
        }
    }
}
