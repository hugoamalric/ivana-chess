@file:Suppress("ClassName")

package dev.gleroy.ivanachess.dto

import dev.gleroy.ivanachess.core.Piece
import dev.gleroy.ivanachess.core.Position
import dev.gleroy.ivanachess.core.PositionedPiece
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PieceDtoTest {
    @Nested
    inner class from {
        @Test
        fun `should return white pawn`() {
            val pos = Position.fromCoordinates("A2")
            PieceDto.from(PositionedPiece(Piece.Pawn(Piece.Color.White), pos)) shouldBe PieceDto(
                color = PieceDto.Color.White,
                type = PieceDto.Type.Pawn,
                pos = PositionDto.from(pos)
            )
        }

        @Test
        fun `should return white rook`() {
            val pos = Position.fromCoordinates("A1")
            PieceDto.from(PositionedPiece(Piece.Rook(Piece.Color.White), pos)) shouldBe PieceDto(
                color = PieceDto.Color.White,
                type = PieceDto.Type.Rook,
                pos = PositionDto.from(pos)
            )
        }

        @Test
        fun `should return white knight`() {
            val pos = Position.fromCoordinates("B1")
            PieceDto.from(PositionedPiece(Piece.Knight(Piece.Color.White), pos)) shouldBe PieceDto(
                color = PieceDto.Color.White,
                type = PieceDto.Type.Knight,
                pos = PositionDto.from(pos)
            )
        }

        @Test
        fun `should return white bishop`() {
            val pos = Position.fromCoordinates("C1")
            PieceDto.from(PositionedPiece(Piece.Pawn(Piece.Color.White), pos)) shouldBe PieceDto(
                color = PieceDto.Color.White,
                type = PieceDto.Type.Pawn,
                pos = PositionDto.from(pos)
            )
        }

        @Test
        fun `should return white queen`() {
            val pos = Position.fromCoordinates("D1")
            PieceDto.from(PositionedPiece(Piece.Queen(Piece.Color.White), pos)) shouldBe PieceDto(
                color = PieceDto.Color.White,
                type = PieceDto.Type.Queen,
                pos = PositionDto.from(pos)
            )
        }

        @Test
        fun `should return white king`() {
            val pos = Position.fromCoordinates("E1")
            PieceDto.from(PositionedPiece(Piece.King(Piece.Color.White), pos)) shouldBe PieceDto(
                color = PieceDto.Color.White,
                type = PieceDto.Type.King,
                pos = PositionDto.from(pos)
            )
        }

        @Test
        fun `should return black pawn`() {
            val pos = Position.fromCoordinates("A2")
            PieceDto.from(PositionedPiece(Piece.Pawn(Piece.Color.Black), pos)) shouldBe PieceDto(
                color = PieceDto.Color.Black,
                type = PieceDto.Type.Pawn,
                pos = PositionDto.from(pos)
            )
        }

        @Test
        fun `should return black rook`() {
            val pos = Position.fromCoordinates("A1")
            PieceDto.from(PositionedPiece(Piece.Rook(Piece.Color.Black), pos)) shouldBe PieceDto(
                color = PieceDto.Color.Black,
                type = PieceDto.Type.Rook,
                pos = PositionDto.from(pos)
            )
        }

        @Test
        fun `should return black knight`() {
            val pos = Position.fromCoordinates("B1")
            PieceDto.from(PositionedPiece(Piece.Knight(Piece.Color.Black), pos)) shouldBe PieceDto(
                color = PieceDto.Color.Black,
                type = PieceDto.Type.Knight,
                pos = PositionDto.from(pos)
            )
        }

        @Test
        fun `should return black bishop`() {
            val pos = Position.fromCoordinates("C1")
            PieceDto.from(PositionedPiece(Piece.Pawn(Piece.Color.Black), pos)) shouldBe PieceDto(
                color = PieceDto.Color.Black,
                type = PieceDto.Type.Pawn,
                pos = PositionDto.from(pos)
            )
        }

        @Test
        fun `should return black queen`() {
            val pos = Position.fromCoordinates("D1")
            PieceDto.from(PositionedPiece(Piece.Queen(Piece.Color.Black), pos)) shouldBe PieceDto(
                color = PieceDto.Color.Black,
                type = PieceDto.Type.Queen,
                pos = PositionDto.from(pos)
            )
        }

        @Test
        fun `should return black king`() {
            val pos = Position.fromCoordinates("E1")
            PieceDto.from(PositionedPiece(Piece.King(Piece.Color.Black), pos)) shouldBe PieceDto(
                color = PieceDto.Color.Black,
                type = PieceDto.Type.King,
                pos = PositionDto.from(pos)
            )
        }
    }

    @Nested
    inner class convert {
        @Test
        fun `should return white pawn`() {
            val pos = Position.fromCoordinates("A2")
            val dto = PieceDto(
                color = PieceDto.Color.White,
                type = PieceDto.Type.Pawn,
                pos = PositionDto.from(pos)
            )
            dto.convert() shouldBe PositionedPiece(Piece.Pawn(Piece.Color.White), pos)
        }

        @Test
        fun `should return white rook`() {
            val pos = Position.fromCoordinates("A2")
            val dto = PieceDto(
                color = PieceDto.Color.White,
                type = PieceDto.Type.Rook,
                pos = PositionDto.from(pos)
            )
            dto.convert() shouldBe PositionedPiece(Piece.Rook(Piece.Color.White), pos)
        }

        @Test
        fun `should return white knight`() {
            val pos = Position.fromCoordinates("A2")
            val dto = PieceDto(
                color = PieceDto.Color.White,
                type = PieceDto.Type.Knight,
                pos = PositionDto.from(pos)
            )
            dto.convert() shouldBe PositionedPiece(Piece.Knight(Piece.Color.White), pos)
        }

        @Test
        fun `should return white bishop`() {
            val pos = Position.fromCoordinates("A2")
            val dto = PieceDto(
                color = PieceDto.Color.White,
                type = PieceDto.Type.Bishop,
                pos = PositionDto.from(pos)
            )
            dto.convert() shouldBe PositionedPiece(Piece.Bishop(Piece.Color.White), pos)
        }

        @Test
        fun `should return white queen`() {
            val pos = Position.fromCoordinates("A2")
            val dto = PieceDto(
                color = PieceDto.Color.White,
                type = PieceDto.Type.Queen,
                pos = PositionDto.from(pos)
            )
            dto.convert() shouldBe PositionedPiece(Piece.Queen(Piece.Color.White), pos)
        }

        @Test
        fun `should return white king`() {
            val pos = Position.fromCoordinates("A2")
            val dto = PieceDto(
                color = PieceDto.Color.White,
                type = PieceDto.Type.King,
                pos = PositionDto.from(pos)
            )
            dto.convert() shouldBe PositionedPiece(Piece.King(Piece.Color.White), pos)
        }

        @Test
        fun `should return black pawn`() {
            val pos = Position.fromCoordinates("A2")
            val dto = PieceDto(
                color = PieceDto.Color.Black,
                type = PieceDto.Type.Pawn,
                pos = PositionDto.from(pos)
            )
            dto.convert() shouldBe PositionedPiece(Piece.Pawn(Piece.Color.Black), pos)
        }

        @Test
        fun `should return black rook`() {
            val pos = Position.fromCoordinates("A2")
            val dto = PieceDto(
                color = PieceDto.Color.Black,
                type = PieceDto.Type.Rook,
                pos = PositionDto.from(pos)
            )
            dto.convert() shouldBe PositionedPiece(Piece.Rook(Piece.Color.Black), pos)
        }

        @Test
        fun `should return black knight`() {
            val pos = Position.fromCoordinates("A2")
            val dto = PieceDto(
                color = PieceDto.Color.Black,
                type = PieceDto.Type.Knight,
                pos = PositionDto.from(pos)
            )
            dto.convert() shouldBe PositionedPiece(Piece.Knight(Piece.Color.Black), pos)
        }

        @Test
        fun `should return black bishop`() {
            val pos = Position.fromCoordinates("A2")
            val dto = PieceDto(
                color = PieceDto.Color.Black,
                type = PieceDto.Type.Bishop,
                pos = PositionDto.from(pos)
            )
            dto.convert() shouldBe PositionedPiece(Piece.Bishop(Piece.Color.Black), pos)
        }

        @Test
        fun `should return black queen`() {
            val pos = Position.fromCoordinates("A2")
            val dto = PieceDto(
                color = PieceDto.Color.Black,
                type = PieceDto.Type.Queen,
                pos = PositionDto.from(pos)
            )
            dto.convert() shouldBe PositionedPiece(Piece.Queen(Piece.Color.Black), pos)
        }

        @Test
        fun `should return black king`() {
            val pos = Position.fromCoordinates("A2")
            val dto = PieceDto(
                color = PieceDto.Color.Black,
                type = PieceDto.Type.King,
                pos = PositionDto.from(pos)
            )
            dto.convert() shouldBe PositionedPiece(Piece.King(Piece.Color.Black), pos)
        }
    }
}
