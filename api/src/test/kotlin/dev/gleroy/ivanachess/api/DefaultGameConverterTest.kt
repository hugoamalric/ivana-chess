@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.dto.GameDto
import dev.gleroy.ivanachess.dto.MoveDto
import dev.gleroy.ivanachess.dto.PieceDto
import dev.gleroy.ivanachess.dto.PositionDto
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultGameConverterTest {
    private val gameSummary = GameSummary()

    private val converter = DefaultGameConverter()

    @Nested
    inner class `convert to summary DTO` {
        private val gameDto = GameDto.Summary(
            id = gameSummary.id,
            whiteToken = gameSummary.whiteToken,
            blackToken = gameSummary.blackToken,
            turnColor = PieceDto.Color.White,
            state = GameDto.State.InGame
        )

        @Test
        fun `should return DTO`() {
            converter.convert(gameSummary) shouldBe gameDto
        }
    }

    @Nested
    inner class `convert to complete DTO` {
        private val gameEntity = GameEntity(
            summary = gameSummary
        )
        private val gameDto = GameDto.Complete(
            id = gameSummary.id,
            whiteToken = gameSummary.whiteToken,
            blackToken = gameSummary.blackToken,
            turnColor = PieceDto.Color.White,
            state = GameDto.State.InGame,
            pieces = setOf(
                PieceDto(PieceDto.Color.White, PieceDto.Type.Rook, PositionDto(1, 1)),
                PieceDto(PieceDto.Color.White, PieceDto.Type.Knight, PositionDto(2, 1)),
                PieceDto(PieceDto.Color.White, PieceDto.Type.Bishop, PositionDto(3, 1)),
                PieceDto(PieceDto.Color.White, PieceDto.Type.Queen, PositionDto(4, 1)),
                PieceDto(PieceDto.Color.White, PieceDto.Type.King, PositionDto(5, 1)),
                PieceDto(PieceDto.Color.White, PieceDto.Type.Bishop, PositionDto(6, 1)),
                PieceDto(PieceDto.Color.White, PieceDto.Type.Knight, PositionDto(7, 1)),
                PieceDto(PieceDto.Color.White, PieceDto.Type.Rook, PositionDto(8, 1)),
                PieceDto(PieceDto.Color.White, PieceDto.Type.Pawn, PositionDto(1, 2)),
                PieceDto(PieceDto.Color.White, PieceDto.Type.Pawn, PositionDto(2, 2)),
                PieceDto(PieceDto.Color.White, PieceDto.Type.Pawn, PositionDto(3, 2)),
                PieceDto(PieceDto.Color.White, PieceDto.Type.Pawn, PositionDto(4, 2)),
                PieceDto(PieceDto.Color.White, PieceDto.Type.Pawn, PositionDto(5, 2)),
                PieceDto(PieceDto.Color.White, PieceDto.Type.Pawn, PositionDto(6, 2)),
                PieceDto(PieceDto.Color.White, PieceDto.Type.Pawn, PositionDto(7, 2)),
                PieceDto(PieceDto.Color.White, PieceDto.Type.Pawn, PositionDto(8, 2)),
                PieceDto(PieceDto.Color.Black, PieceDto.Type.Rook, PositionDto(1, 8)),
                PieceDto(PieceDto.Color.Black, PieceDto.Type.Knight, PositionDto(2, 8)),
                PieceDto(PieceDto.Color.Black, PieceDto.Type.Bishop, PositionDto(3, 8)),
                PieceDto(PieceDto.Color.Black, PieceDto.Type.Queen, PositionDto(4, 8)),
                PieceDto(PieceDto.Color.Black, PieceDto.Type.King, PositionDto(5, 8)),
                PieceDto(PieceDto.Color.Black, PieceDto.Type.Bishop, PositionDto(6, 8)),
                PieceDto(PieceDto.Color.Black, PieceDto.Type.Knight, PositionDto(7, 8)),
                PieceDto(PieceDto.Color.Black, PieceDto.Type.Rook, PositionDto(8, 8)),
                PieceDto(PieceDto.Color.Black, PieceDto.Type.Pawn, PositionDto(1, 7)),
                PieceDto(PieceDto.Color.Black, PieceDto.Type.Pawn, PositionDto(2, 7)),
                PieceDto(PieceDto.Color.Black, PieceDto.Type.Pawn, PositionDto(3, 7)),
                PieceDto(PieceDto.Color.Black, PieceDto.Type.Pawn, PositionDto(4, 7)),
                PieceDto(PieceDto.Color.Black, PieceDto.Type.Pawn, PositionDto(5, 7)),
                PieceDto(PieceDto.Color.Black, PieceDto.Type.Pawn, PositionDto(6, 7)),
                PieceDto(PieceDto.Color.Black, PieceDto.Type.Pawn, PositionDto(7, 7)),
                PieceDto(PieceDto.Color.Black, PieceDto.Type.Pawn, PositionDto(8, 7)),
            ),
            moves = emptyList(),
            possibleMoves = gameEntity.game.nextPossibleMoves
                .map { MoveDto.from(it.move) }
                .toSet()
        )

        @Test
        fun `should return DTO`() {
            converter.convert(gameEntity) shouldBe gameDto
        }
    }
}
