@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.core.Piece
import dev.gleroy.ivanachess.core.Position
import dev.gleroy.ivanachess.dto.PieceDto
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultPieceConverterTest {
    private val posConverter = DefaultPositionConverter()

    private val converter = DefaultPieceConverter()

    @Nested
    inner class convertColorToDto {
        @Test
        fun `should return white DTO`() {
            converter.convertColorToDto(Piece.Color.White) shouldBe PieceDto.Color.White
        }

        @Test
        fun `should return black DTO`() {
            converter.convertColorToDto(Piece.Color.Black) shouldBe PieceDto.Color.Black
        }
    }

    @Nested
    inner class convertToDto {
        private val pos = Position.fromCoordinates("A1")
        private val posDto = posConverter.convertToDto(pos)

        @Test
        fun `should return white pawn DTO`() {
            shouldReturnDto(
                piece = Piece.Pawn(Piece.Color.White),
                dto = PieceDto(
                    color = PieceDto.Color.White,
                    type = PieceDto.Type.Pawn,
                    pos = posDto
                )
            )
        }

        @Test
        fun `should return white rook DTO`() {
            shouldReturnDto(
                piece = Piece.Rook(Piece.Color.White),
                dto = PieceDto(
                    color = PieceDto.Color.White,
                    type = PieceDto.Type.Rook,
                    pos = posDto
                )
            )
        }

        @Test
        fun `should return white knight DTO`() {
            shouldReturnDto(
                piece = Piece.Knight(Piece.Color.White),
                dto = PieceDto(
                    color = PieceDto.Color.White,
                    type = PieceDto.Type.Knight,
                    pos = posDto
                )
            )
        }

        @Test
        fun `should return white bishop DTO`() {
            shouldReturnDto(
                piece = Piece.Bishop(Piece.Color.White),
                dto = PieceDto(
                    color = PieceDto.Color.White,
                    type = PieceDto.Type.Bishop,
                    pos = posDto
                )
            )
        }

        @Test
        fun `should return white queen DTO`() {
            shouldReturnDto(
                piece = Piece.Queen(Piece.Color.White),
                dto = PieceDto(
                    color = PieceDto.Color.White,
                    type = PieceDto.Type.Queen,
                    pos = posDto
                )
            )
        }

        @Test
        fun `should return white king DTO`() {
            shouldReturnDto(
                piece = Piece.King(Piece.Color.White),
                dto = PieceDto(
                    color = PieceDto.Color.White,
                    type = PieceDto.Type.King,
                    pos = posDto
                )
            )
        }

        @Test
        fun `should return black pawn DTO`() {
            shouldReturnDto(
                piece = Piece.Pawn(Piece.Color.Black),
                dto = PieceDto(
                    color = PieceDto.Color.Black,
                    type = PieceDto.Type.Pawn,
                    pos = posDto
                )
            )
        }

        @Test
        fun `should return black rook DTO`() {
            shouldReturnDto(
                piece = Piece.Rook(Piece.Color.Black),
                dto = PieceDto(
                    color = PieceDto.Color.Black,
                    type = PieceDto.Type.Rook,
                    pos = posDto
                )
            )
        }

        @Test
        fun `should return black knight DTO`() {
            shouldReturnDto(
                piece = Piece.Knight(Piece.Color.Black),
                dto = PieceDto(
                    color = PieceDto.Color.Black,
                    type = PieceDto.Type.Knight,
                    pos = posDto
                )
            )
        }

        @Test
        fun `should return black bishop DTO`() {
            shouldReturnDto(
                piece = Piece.Bishop(Piece.Color.Black),
                dto = PieceDto(
                    color = PieceDto.Color.Black,
                    type = PieceDto.Type.Bishop,
                    pos = posDto
                )
            )
        }

        @Test
        fun `should return black queen DTO`() {
            shouldReturnDto(
                piece = Piece.Queen(Piece.Color.Black),
                dto = PieceDto(
                    color = PieceDto.Color.Black,
                    type = PieceDto.Type.Queen,
                    pos = posDto
                )
            )
        }

        @Test
        fun `should return black king DTO`() {
            shouldReturnDto(
                piece = Piece.King(Piece.Color.Black),
                dto = PieceDto(
                    color = PieceDto.Color.Black,
                    type = PieceDto.Type.King,
                    pos = posDto
                )
            )
        }

        private fun shouldReturnDto(piece: Piece, dto: PieceDto) {
            converter.convertToDto(piece, pos) shouldBe dto
        }
    }

    @Nested
    inner class convertToPiece {
        @Test
        fun `should return white pawn`() {
            converter.convertToPiece(PieceDto.Color.White, PieceDto.Type.Pawn) shouldBe Piece.Pawn(Piece.Color.White)
        }

        @Test
        fun `should return white rook`() {
            converter.convertToPiece(PieceDto.Color.White, PieceDto.Type.Rook) shouldBe Piece.Rook(Piece.Color.White)
        }

        @Test
        fun `should return white knight`() {
            converter.convertToPiece(
                PieceDto.Color.White,
                PieceDto.Type.Knight
            ) shouldBe Piece.Knight(Piece.Color.White)
        }

        @Test
        fun `should return white bishop`() {
            converter.convertToPiece(
                PieceDto.Color.White,
                PieceDto.Type.Bishop
            ) shouldBe Piece.Bishop(Piece.Color.White)
        }

        @Test
        fun `should return white queen`() {
            converter.convertToPiece(PieceDto.Color.White, PieceDto.Type.Queen) shouldBe Piece.Queen(Piece.Color.White)
        }

        @Test
        fun `should return white king`() {
            converter.convertToPiece(PieceDto.Color.White, PieceDto.Type.King) shouldBe Piece.King(Piece.Color.White)
        }

        @Test
        fun `should return black pawn`() {
            converter.convertToPiece(PieceDto.Color.Black, PieceDto.Type.Pawn) shouldBe Piece.Pawn(Piece.Color.Black)
        }

        @Test
        fun `should return black rook`() {
            converter.convertToPiece(PieceDto.Color.Black, PieceDto.Type.Rook) shouldBe Piece.Rook(Piece.Color.Black)
        }

        @Test
        fun `should return black knight`() {
            converter.convertToPiece(
                PieceDto.Color.Black,
                PieceDto.Type.Knight
            ) shouldBe Piece.Knight(Piece.Color.Black)
        }

        @Test
        fun `should return black bishop`() {
            converter.convertToPiece(
                PieceDto.Color.Black,
                PieceDto.Type.Bishop
            ) shouldBe Piece.Bishop(Piece.Color.Black)
        }

        @Test
        fun `should return black queen`() {
            converter.convertToPiece(PieceDto.Color.Black, PieceDto.Type.Queen) shouldBe Piece.Queen(Piece.Color.Black)
        }

        @Test
        fun `should return black king`() {
            converter.convertToPiece(PieceDto.Color.Black, PieceDto.Type.King) shouldBe Piece.King(Piece.Color.Black)
        }
    }
}
