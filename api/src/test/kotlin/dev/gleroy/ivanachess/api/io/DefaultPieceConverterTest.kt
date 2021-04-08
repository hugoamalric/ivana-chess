@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.core.Piece
import dev.gleroy.ivanachess.core.Position
import dev.gleroy.ivanachess.io.PieceRepresentation
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultPieceConverterTest {
    private val posConverter = DefaultPositionConverter()

    private val converter = DefaultPieceConverter()

    @Nested
    inner class convertColorToRepresentation {
        @Test
        fun `should return white representation`() {
            converter.convertColorToRepresentation(Piece.Color.White) shouldBe PieceRepresentation.Color.White
        }

        @Test
        fun `should return black representation`() {
            converter.convertColorToRepresentation(Piece.Color.Black) shouldBe PieceRepresentation.Color.Black
        }
    }

    @Nested
    inner class convertToRepresentation {
        private val pos = Position.fromCoordinates("A1")
        private val posRepresentation = posConverter.convertToRepresentation(pos)

        @Test
        fun `should return white pawn representation`() {
            shouldReturnRepresentation(
                piece = Piece.Pawn(Piece.Color.White),
                representation = PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.Pawn,
                    pos = posRepresentation
                )
            )
        }

        @Test
        fun `should return white rook representation`() {
            shouldReturnRepresentation(
                piece = Piece.Rook(Piece.Color.White),
                representation = PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.Rook,
                    pos = posRepresentation
                )
            )
        }

        @Test
        fun `should return white knight representation`() {
            shouldReturnRepresentation(
                piece = Piece.Knight(Piece.Color.White),
                representation = PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.Knight,
                    pos = posRepresentation
                )
            )
        }

        @Test
        fun `should return white bishop representation`() {
            shouldReturnRepresentation(
                piece = Piece.Bishop(Piece.Color.White),
                representation = PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.Bishop,
                    pos = posRepresentation
                )
            )
        }

        @Test
        fun `should return white queen representation`() {
            shouldReturnRepresentation(
                piece = Piece.Queen(Piece.Color.White),
                representation = PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.Queen,
                    pos = posRepresentation
                )
            )
        }

        @Test
        fun `should return white king representation`() {
            shouldReturnRepresentation(
                piece = Piece.King(Piece.Color.White),
                representation = PieceRepresentation(
                    color = PieceRepresentation.Color.White,
                    type = PieceRepresentation.Type.King,
                    pos = posRepresentation
                )
            )
        }

        @Test
        fun `should return black pawn representation`() {
            shouldReturnRepresentation(
                piece = Piece.Pawn(Piece.Color.Black),
                representation = PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.Pawn,
                    pos = posRepresentation
                )
            )
        }

        @Test
        fun `should return black rook representation`() {
            shouldReturnRepresentation(
                piece = Piece.Rook(Piece.Color.Black),
                representation = PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.Rook,
                    pos = posRepresentation
                )
            )
        }

        @Test
        fun `should return black knight representation`() {
            shouldReturnRepresentation(
                piece = Piece.Knight(Piece.Color.Black),
                representation = PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.Knight,
                    pos = posRepresentation
                )
            )
        }

        @Test
        fun `should return black bishop representation`() {
            shouldReturnRepresentation(
                piece = Piece.Bishop(Piece.Color.Black),
                representation = PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.Bishop,
                    pos = posRepresentation
                )
            )
        }

        @Test
        fun `should return black queen representation`() {
            shouldReturnRepresentation(
                piece = Piece.Queen(Piece.Color.Black),
                representation = PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.Queen,
                    pos = posRepresentation
                )
            )
        }

        @Test
        fun `should return black king representation`() {
            shouldReturnRepresentation(
                piece = Piece.King(Piece.Color.Black),
                representation = PieceRepresentation(
                    color = PieceRepresentation.Color.Black,
                    type = PieceRepresentation.Type.King,
                    pos = posRepresentation
                )
            )
        }

        private fun shouldReturnRepresentation(piece: Piece, representation: PieceRepresentation) {
            converter.convertToRepresentation(piece, pos) shouldBe representation
        }
    }

    @Nested
    inner class convertToPiece {
        @Test
        fun `should return white pawn`() {
            converter.convertToPiece(
                PieceRepresentation.Color.White,
                PieceRepresentation.Type.Pawn
            ) shouldBe Piece.Pawn(Piece.Color.White)
        }

        @Test
        fun `should return white rook`() {
            converter.convertToPiece(
                PieceRepresentation.Color.White,
                PieceRepresentation.Type.Rook
            ) shouldBe Piece.Rook(Piece.Color.White)
        }

        @Test
        fun `should return white knight`() {
            converter.convertToPiece(
                PieceRepresentation.Color.White,
                PieceRepresentation.Type.Knight
            ) shouldBe Piece.Knight(Piece.Color.White)
        }

        @Test
        fun `should return white bishop`() {
            converter.convertToPiece(
                PieceRepresentation.Color.White,
                PieceRepresentation.Type.Bishop
            ) shouldBe Piece.Bishop(Piece.Color.White)
        }

        @Test
        fun `should return white queen`() {
            converter.convertToPiece(
                PieceRepresentation.Color.White,
                PieceRepresentation.Type.Queen
            ) shouldBe Piece.Queen(Piece.Color.White)
        }

        @Test
        fun `should return white king`() {
            converter.convertToPiece(
                PieceRepresentation.Color.White,
                PieceRepresentation.Type.King
            ) shouldBe Piece.King(Piece.Color.White)
        }

        @Test
        fun `should return black pawn`() {
            converter.convertToPiece(
                PieceRepresentation.Color.Black,
                PieceRepresentation.Type.Pawn
            ) shouldBe Piece.Pawn(Piece.Color.Black)
        }

        @Test
        fun `should return black rook`() {
            converter.convertToPiece(
                PieceRepresentation.Color.Black,
                PieceRepresentation.Type.Rook
            ) shouldBe Piece.Rook(Piece.Color.Black)
        }

        @Test
        fun `should return black knight`() {
            converter.convertToPiece(
                PieceRepresentation.Color.Black,
                PieceRepresentation.Type.Knight
            ) shouldBe Piece.Knight(Piece.Color.Black)
        }

        @Test
        fun `should return black bishop`() {
            converter.convertToPiece(
                PieceRepresentation.Color.Black,
                PieceRepresentation.Type.Bishop
            ) shouldBe Piece.Bishop(Piece.Color.Black)
        }

        @Test
        fun `should return black queen`() {
            converter.convertToPiece(
                PieceRepresentation.Color.Black,
                PieceRepresentation.Type.Queen
            ) shouldBe Piece.Queen(Piece.Color.Black)
        }

        @Test
        fun `should return black king`() {
            converter.convertToPiece(
                PieceRepresentation.Color.Black,
                PieceRepresentation.Type.King
            ) shouldBe Piece.King(Piece.Color.Black)
        }
    }
}
