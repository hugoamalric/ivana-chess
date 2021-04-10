@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.game.Piece
import dev.gleroy.ivanachess.game.Position
import dev.gleroy.ivanachess.game.PositionedPiece
import dev.gleroy.ivanachess.io.ColorRepresentation
import dev.gleroy.ivanachess.io.PieceRepresentation
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultPieceConverterTest {
    private val posConverter = DefaultPositionConverter()
    private val converter = DefaultPieceConverter(
        posConverter = posConverter,
    )

    @Nested
    inner class convertToRepresentation {
        private val pos = Position.fromCoordinates("A1")
        private val posRepresentation = posConverter.convertToRepresentation(pos)

        @Test
        fun `should return pawn representation`() {
            shouldReturnRepresentation(
                piece = Piece.Pawn(Piece.Color.White),
                representation = PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.Pawn,
                    pos = posRepresentation
                )
            )
        }

        @Test
        fun `should return rook representation`() {
            shouldReturnRepresentation(
                piece = Piece.Rook(Piece.Color.White),
                representation = PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.Rook,
                    pos = posRepresentation
                )
            )
        }

        @Test
        fun `should return knight representation`() {
            shouldReturnRepresentation(
                piece = Piece.Knight(Piece.Color.White),
                representation = PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.Knight,
                    pos = posRepresentation
                )
            )
        }

        @Test
        fun `should return bishop representation`() {
            shouldReturnRepresentation(
                piece = Piece.Bishop(Piece.Color.White),
                representation = PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.Bishop,
                    pos = posRepresentation
                )
            )
        }

        @Test
        fun `should return queen representation`() {
            shouldReturnRepresentation(
                piece = Piece.Queen(Piece.Color.White),
                representation = PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.Queen,
                    pos = posRepresentation
                )
            )
        }

        @Test
        fun `should return king representation`() {
            shouldReturnRepresentation(
                piece = Piece.King(Piece.Color.White),
                representation = PieceRepresentation(
                    color = ColorRepresentation.White,
                    type = PieceRepresentation.Type.King,
                    pos = posRepresentation
                )
            )
        }

        private fun shouldReturnRepresentation(piece: Piece, representation: PieceRepresentation) {
            converter.convertToRepresentation(PositionedPiece(piece, pos)) shouldBe representation
        }
    }

    @Nested
    inner class convertToPiece {
        @Test
        fun `should return pawn`() {
            converter.convertToPiece(
                ColorRepresentation.White,
                PieceRepresentation.Type.Pawn
            ) shouldBe Piece.Pawn(Piece.Color.White)
        }

        @Test
        fun `should return rook`() {
            converter.convertToPiece(
                ColorRepresentation.White,
                PieceRepresentation.Type.Rook
            ) shouldBe Piece.Rook(Piece.Color.White)
        }

        @Test
        fun `should return knight`() {
            converter.convertToPiece(
                ColorRepresentation.White,
                PieceRepresentation.Type.Knight
            ) shouldBe Piece.Knight(Piece.Color.White)
        }

        @Test
        fun `should return bishop`() {
            converter.convertToPiece(
                ColorRepresentation.White,
                PieceRepresentation.Type.Bishop
            ) shouldBe Piece.Bishop(Piece.Color.White)
        }

        @Test
        fun `should return queen`() {
            converter.convertToPiece(
                ColorRepresentation.White,
                PieceRepresentation.Type.Queen
            ) shouldBe Piece.Queen(Piece.Color.White)
        }

        @Test
        fun `should return king`() {
            converter.convertToPiece(
                ColorRepresentation.White,
                PieceRepresentation.Type.King
            ) shouldBe Piece.King(Piece.Color.White)
        }
    }
}
