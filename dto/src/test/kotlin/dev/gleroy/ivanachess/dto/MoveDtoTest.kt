@file:Suppress("ClassName")

package dev.gleroy.ivanachess.dto

import dev.gleroy.ivanachess.core.Move
import dev.gleroy.ivanachess.core.Piece
import dev.gleroy.ivanachess.core.Position
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class MoveDtoTest {
    @Nested
    inner class from {
        @Test
        fun `should return simple DTO`() {
            MoveDto.from(Move.Simple.fromCoordinates("A2", "A4")) shouldBe MoveDto.Simple(
                from = PositionDto(1, 2), 
                to = PositionDto(1, 4)
            )
        }

        @Test
        fun `should return promotion DTO with rook`() {
            val move = Move.Promotion(
                from = Position.fromCoordinates("A7"), 
                to = Position.fromCoordinates("A8"), 
                promotion = Piece.Rook(Piece.Color.White)
            )
            MoveDto.from(move) shouldBe MoveDto.Promotion(
                from = PositionDto(1, 7),
                to = PositionDto(1, 8),
                promotionType = PieceDto.Type.Rook
            )
        }

        @Test
        fun `should return promotion DTO with knight`() {
            val move = Move.Promotion(
                from = Position.fromCoordinates("A7"),
                to = Position.fromCoordinates("A8"),
                promotion = Piece.Knight(Piece.Color.White)
            )
            MoveDto.from(move) shouldBe MoveDto.Promotion(
                from = PositionDto(1, 7),
                to = PositionDto(1, 8),
                promotionType = PieceDto.Type.Knight
            )
        }

        @Test
        fun `should return promotion DTO with bishop`() {
            val move = Move.Promotion(
                from = Position.fromCoordinates("A7"),
                to = Position.fromCoordinates("A8"),
                promotion = Piece.Bishop(Piece.Color.White)
            )
            MoveDto.from(move) shouldBe MoveDto.Promotion(
                from = PositionDto(1, 7),
                to = PositionDto(1, 8),
                promotionType = PieceDto.Type.Bishop
            )
        }

        @Test
        fun `should return promotion DTO with queen`() {
            val move = Move.Promotion(
                from = Position.fromCoordinates("A7"),
                to = Position.fromCoordinates("A8"),
                promotion = Piece.Queen(Piece.Color.White)
            )
            MoveDto.from(move) shouldBe MoveDto.Promotion(
                from = PositionDto(1, 7),
                to = PositionDto(1, 8),
                promotionType = PieceDto.Type.Queen
            )
        }
    }

    @Nested
    inner class Promotion {
        @Nested
        inner class convert {
            @Test
            fun `should convert to promotion move with white rook`() {
                val dto = MoveDto.Promotion(PositionDto(1, 7), PositionDto(1, 8), PieceDto.Type.Rook)
                val color = Piece.Color.White
                dto.convert(color) shouldBe Move.Promotion(
                    from = Position(1, 7),
                    to = Position(1, 8),
                    promotion = Piece.Rook(color)
                )
            }

            @Test
            fun `should convert to promotion move with white knight`() {
                val dto = MoveDto.Promotion(PositionDto(1, 7), PositionDto(1, 8), PieceDto.Type.Knight)
                val color = Piece.Color.White
                dto.convert(color) shouldBe Move.Promotion(
                    from = Position(1, 7),
                    to = Position(1, 8),
                    promotion = Piece.Knight(color)
                )
            }

            @Test
            fun `should convert to promotion move with white bishop`() {
                val dto = MoveDto.Promotion(PositionDto(1, 7), PositionDto(1, 8), PieceDto.Type.Bishop)
                val color = Piece.Color.White
                dto.convert(color) shouldBe Move.Promotion(
                    from = Position(1, 7),
                    to = Position(1, 8),
                    promotion = Piece.Bishop(color)
                )
            }

            @Test
            fun `should convert to promotion move with white queen`() {
                val dto = MoveDto.Promotion(PositionDto(1, 7), PositionDto(1, 8), PieceDto.Type.Queen)
                val color = Piece.Color.White
                dto.convert(color) shouldBe Move.Promotion(
                    from = Position(1, 7),
                    to = Position(1, 8),
                    promotion = Piece.Queen(color)
                )
            }

            @Test
            fun `should convert to promotion move with black rook`() {
                val dto = MoveDto.Promotion(PositionDto(1, 7), PositionDto(1, 8), PieceDto.Type.Rook)
                val color = Piece.Color.Black
                dto.convert(color) shouldBe Move.Promotion(
                    from = Position(1, 7),
                    to = Position(1, 8),
                    promotion = Piece.Rook(color)
                )
            }

            @Test
            fun `should convert to promotion move with black knight`() {
                val dto = MoveDto.Promotion(PositionDto(1, 7), PositionDto(1, 8), PieceDto.Type.Knight)
                val color = Piece.Color.Black
                dto.convert(color) shouldBe Move.Promotion(
                    from = Position(1, 7),
                    to = Position(1, 8),
                    promotion = Piece.Knight(color)
                )
            }

            @Test
            fun `should convert to promotion move with black bishop`() {
                val dto = MoveDto.Promotion(PositionDto(1, 7), PositionDto(1, 8), PieceDto.Type.Bishop)
                val color = Piece.Color.Black
                dto.convert(color) shouldBe Move.Promotion(
                    from = Position(1, 7),
                    to = Position(1, 8),
                    promotion = Piece.Bishop(color)
                )
            }

            @Test
            fun `should convert to promotion move with black queen`() {
                val dto = MoveDto.Promotion(PositionDto(1, 7), PositionDto(1, 8), PieceDto.Type.Queen)
                val color = Piece.Color.Black
                dto.convert(color) shouldBe Move.Promotion(
                    from = Position(1, 7),
                    to = Position(1, 8),
                    promotion = Piece.Queen(color)
                )
            }
        }
    }

    @Nested
    inner class Simple {
        @Nested
        inner class convert {
            @Test
            fun `should convert to simple move`() {
                val dto = MoveDto.Simple(PositionDto(1, 2), PositionDto(1, 4))
                dto.convert(Piece.Color.White) shouldBe Move.Simple.fromCoordinates("A2", "A4")
            }
        }
    }
}
