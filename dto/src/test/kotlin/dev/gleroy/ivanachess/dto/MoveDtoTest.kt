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
        fun `should return promotion DTO with white rook`() {
            val move = Move.Promotion(
                from = Position.fromCoordinates("A7"),
                to = Position.fromCoordinates("A8"),
                promotion = Piece.Rook(Piece.Color.White)
            )
            MoveDto.from(move) shouldBe MoveDto.Promotion(
                from = PositionDto(1, 7),
                to = PositionDto(1, 8),
                promotionColor = PieceDto.Color.White,
                promotionType = PieceDto.Type.Rook
            )
        }

        @Test
        fun `should return promotion DTO with white knight`() {
            val move = Move.Promotion(
                from = Position.fromCoordinates("A7"),
                to = Position.fromCoordinates("A8"),
                promotion = Piece.Knight(Piece.Color.White)
            )
            MoveDto.from(move) shouldBe MoveDto.Promotion(
                from = PositionDto(1, 7),
                to = PositionDto(1, 8),
                promotionColor = PieceDto.Color.White,
                promotionType = PieceDto.Type.Knight
            )
        }

        @Test
        fun `should return promotion DTO with white bishop`() {
            val move = Move.Promotion(
                from = Position.fromCoordinates("A7"),
                to = Position.fromCoordinates("A8"),
                promotion = Piece.Bishop(Piece.Color.White)
            )
            MoveDto.from(move) shouldBe MoveDto.Promotion(
                from = PositionDto(1, 7),
                to = PositionDto(1, 8),
                promotionColor = PieceDto.Color.White,
                promotionType = PieceDto.Type.Bishop
            )
        }

        @Test
        fun `should return promotion DTO with white queen`() {
            val move = Move.Promotion(
                from = Position.fromCoordinates("A7"),
                to = Position.fromCoordinates("A8"),
                promotion = Piece.Queen(Piece.Color.White)
            )
            MoveDto.from(move) shouldBe MoveDto.Promotion(
                from = PositionDto(1, 7),
                to = PositionDto(1, 8),
                promotionColor = PieceDto.Color.White,
                promotionType = PieceDto.Type.Queen
            )
        }

        @Test
        fun `should return promotion DTO with black rook`() {
            val move = Move.Promotion(
                from = Position.fromCoordinates("A7"),
                to = Position.fromCoordinates("A8"),
                promotion = Piece.Rook(Piece.Color.Black)
            )
            MoveDto.from(move) shouldBe MoveDto.Promotion(
                from = PositionDto(1, 7),
                to = PositionDto(1, 8),
                promotionColor = PieceDto.Color.Black,
                promotionType = PieceDto.Type.Rook
            )
        }

        @Test
        fun `should return promotion DTO with black knight`() {
            val move = Move.Promotion(
                from = Position.fromCoordinates("A7"),
                to = Position.fromCoordinates("A8"),
                promotion = Piece.Knight(Piece.Color.Black)
            )
            MoveDto.from(move) shouldBe MoveDto.Promotion(
                from = PositionDto(1, 7),
                to = PositionDto(1, 8),
                promotionColor = PieceDto.Color.Black,
                promotionType = PieceDto.Type.Knight
            )
        }

        @Test
        fun `should return promotion DTO with black bishop`() {
            val move = Move.Promotion(
                from = Position.fromCoordinates("A7"),
                to = Position.fromCoordinates("A8"),
                promotion = Piece.Bishop(Piece.Color.Black)
            )
            MoveDto.from(move) shouldBe MoveDto.Promotion(
                from = PositionDto(1, 7),
                to = PositionDto(1, 8),
                promotionColor = PieceDto.Color.Black,
                promotionType = PieceDto.Type.Bishop
            )
        }

        @Test
        fun `should return promotion DTO with black queen`() {
            val move = Move.Promotion(
                from = Position.fromCoordinates("A7"),
                to = Position.fromCoordinates("A8"),
                promotion = Piece.Queen(Piece.Color.Black)
            )
            MoveDto.from(move) shouldBe MoveDto.Promotion(
                from = PositionDto(1, 7),
                to = PositionDto(1, 8),
                promotionColor = PieceDto.Color.Black,
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
                val dto = MoveDto.Promotion(
                    from = PositionDto(1, 7),
                    to = PositionDto(1, 8),
                    promotionColor = PieceDto.Color.White,
                    promotionType = PieceDto.Type.Rook
                )
                dto.convert() shouldBe Move.Promotion(
                    from = Position(1, 7),
                    to = Position(1, 8),
                    promotion = Piece.Rook(Piece.Color.White)
                )
            }

            @Test
            fun `should convert to promotion move with white knight`() {
                val dto = MoveDto.Promotion(
                    from = PositionDto(1, 7),
                    to = PositionDto(1, 8),
                    promotionColor = PieceDto.Color.White,
                    promotionType = PieceDto.Type.Knight
                )
                dto.convert() shouldBe Move.Promotion(
                    from = Position(1, 7),
                    to = Position(1, 8),
                    promotion = Piece.Knight(Piece.Color.White)
                )
            }

            @Test
            fun `should convert to promotion move with white bishop`() {
                val dto = MoveDto.Promotion(
                    from = PositionDto(1, 7),
                    to = PositionDto(1, 8),
                    promotionColor = PieceDto.Color.White,
                    promotionType = PieceDto.Type.Bishop
                )
                dto.convert() shouldBe Move.Promotion(
                    from = Position(1, 7),
                    to = Position(1, 8),
                    promotion = Piece.Bishop(Piece.Color.White)
                )
            }

            @Test
            fun `should convert to promotion move with white queen`() {
                val dto = MoveDto.Promotion(
                    from = PositionDto(1, 7),
                    to = PositionDto(1, 8),
                    promotionColor = PieceDto.Color.White,
                    promotionType = PieceDto.Type.Queen
                )
                dto.convert() shouldBe Move.Promotion(
                    from = Position(1, 7),
                    to = Position(1, 8),
                    promotion = Piece.Queen(Piece.Color.White)
                )
            }

            @Test
            fun `should convert to promotion move with black rook`() {
                val dto = MoveDto.Promotion(
                    from = PositionDto(1, 7),
                    to = PositionDto(1, 8),
                    promotionColor = PieceDto.Color.Black,
                    promotionType = PieceDto.Type.Rook
                )
                dto.convert() shouldBe Move.Promotion(
                    from = Position(1, 7),
                    to = Position(1, 8),
                    promotion = Piece.Rook(Piece.Color.Black)
                )
            }

            @Test
            fun `should convert to promotion move with black knight`() {
                val dto = MoveDto.Promotion(
                    from = PositionDto(1, 7),
                    to = PositionDto(1, 8),
                    promotionColor = PieceDto.Color.Black,
                    promotionType = PieceDto.Type.Knight
                )
                dto.convert() shouldBe Move.Promotion(
                    from = Position(1, 7),
                    to = Position(1, 8),
                    promotion = Piece.Knight(Piece.Color.Black)
                )
            }

            @Test
            fun `should convert to promotion move with black bishop`() {
                val dto = MoveDto.Promotion(
                    from = PositionDto(1, 7),
                    to = PositionDto(1, 8),
                    promotionColor = PieceDto.Color.Black,
                    promotionType = PieceDto.Type.Bishop
                )
                dto.convert() shouldBe Move.Promotion(
                    from = Position(1, 7),
                    to = Position(1, 8),
                    promotion = Piece.Bishop(Piece.Color.Black)
                )
            }

            @Test
            fun `should convert to promotion move with black queen`() {
                val dto = MoveDto.Promotion(
                    from = PositionDto(1, 7),
                    to = PositionDto(1, 8),
                    promotionColor = PieceDto.Color.Black,
                    promotionType = PieceDto.Type.Queen
                )
                dto.convert() shouldBe Move.Promotion(
                    from = Position(1, 7),
                    to = Position(1, 8),
                    promotion = Piece.Queen(Piece.Color.Black)
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
                dto.convert() shouldBe Move.Simple.fromCoordinates("A2", "A4")
            }
        }
    }
}
