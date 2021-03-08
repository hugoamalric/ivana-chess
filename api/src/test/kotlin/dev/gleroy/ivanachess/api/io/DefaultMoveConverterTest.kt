@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.core.Move
import dev.gleroy.ivanachess.core.Piece
import dev.gleroy.ivanachess.core.Position
import dev.gleroy.ivanachess.dto.MoveDto
import dev.gleroy.ivanachess.dto.PieceDto
import dev.gleroy.ivanachess.dto.PositionDto
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultMoveConverterTest {
    private val posConverter = DefaultPositionConverter()

    private val converter = DefaultMoveConverter(
        posConverter = posConverter
    )

    @Nested
    inner class convertToDto {
        @Test
        fun `should return simple DTO`() {
            val move = Move.Simple.fromCoordinates("A1", "A2")
            val moveDto = MoveDto.Simple(
                from = posConverter.convertToDto(move.from),
                to = posConverter.convertToDto(move.to)
            )
            converter.convertToDto(move) shouldBe moveDto
        }

        @Test
        fun `should return promotion DTO`() {
            val move = Move.Promotion(
                from = Position.fromCoordinates("A1"),
                to = Position.fromCoordinates("A2"),
                promotion = Piece.Queen(Piece.Color.White)
            )
            val moveDto = MoveDto.Promotion(
                from = posConverter.convertToDto(move.from),
                to = posConverter.convertToDto(move.to),
                promotionColor = PieceDto.Color.White,
                promotionType = PieceDto.Type.Queen
            )
            converter.convertToDto(move) shouldBe moveDto
        }
    }

    @Nested
    inner class convertToMove {
        @Test
        fun `should return simple move`() {
            val moveDto = MoveDto.Simple(
                from = PositionDto(1, 1),
                to = PositionDto(1, 2)
            )
            val move = Move.Simple(
                from = posConverter.convertToPosition(moveDto.from),
                to = posConverter.convertToPosition(moveDto.to)
            )
            converter.convertToMove(moveDto) shouldBe move
        }

        @Test
        fun `should return promotion move`() {
            val moveDto = MoveDto.Promotion(
                from = PositionDto(1, 1),
                to = PositionDto(1, 2),
                promotionColor = PieceDto.Color.White,
                promotionType = PieceDto.Type.Queen
            )
            val move = Move.Promotion(
                from = posConverter.convertToPosition(moveDto.from),
                to = posConverter.convertToPosition(moveDto.to),
                promotion = Piece.Queen(Piece.Color.White)
            )
            converter.convertToMove(moveDto) shouldBe move
        }
    }
}
