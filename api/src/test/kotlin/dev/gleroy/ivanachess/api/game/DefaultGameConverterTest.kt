@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.game

import dev.gleroy.ivanachess.api.user.User
import dev.gleroy.ivanachess.api.user.UserConverter
import dev.gleroy.ivanachess.dto.*
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultGameConverterTest {
    private val gameSummary = GameSummary(
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
    private val whitePlayerDto = gameSummary.whitePlayer.toDto()
    private val blackPlayerDto = gameSummary.blackPlayer.toDto()

    private lateinit var userConverter: UserConverter

    private lateinit var converter: DefaultGameConverter

    @BeforeEach
    fun beforeEach() {
        userConverter = mockk()
        converter = DefaultGameConverter(userConverter)
    }

    @Nested
    inner class `convert to summary DTO` {
        private val gameDto = GameDto.Summary(
            id = gameSummary.id,
            whitePlayer = whitePlayerDto,
            blackPlayer = blackPlayerDto,
            turnColor = PieceDto.Color.White,
            state = GameDto.State.InGame
        )

        @Test
        fun `should return DTO`() {
            every { userConverter.convert(gameSummary.whitePlayer) } returns whitePlayerDto
            every { userConverter.convert(gameSummary.blackPlayer) } returns blackPlayerDto
            converter.convert(gameSummary) shouldBe gameDto
            verify { userConverter.convert(gameSummary.whitePlayer) }
            verify { userConverter.convert(gameSummary.blackPlayer) }
        }
    }

    @Nested
    inner class `convert to complete DTO` {
        private val gameAndSummary = GameAndSummary(
            summary = gameSummary
        )
        private val gameDto = GameDto.Complete(
            id = gameSummary.id,
            whitePlayer = whitePlayerDto,
            blackPlayer = blackPlayerDto,
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
            possibleMoves = gameAndSummary.game.nextPossibleMoves
                .map { MoveDto.from(it.move) }
                .toSet()
        )

        @Test
        fun `should return DTO`() {
            every { userConverter.convert(gameSummary.whitePlayer) } returns whitePlayerDto
            every { userConverter.convert(gameSummary.blackPlayer) } returns blackPlayerDto
            converter.convert(gameAndSummary) shouldBe gameDto
            verify { userConverter.convert(gameSummary.whitePlayer) }
            verify { userConverter.convert(gameSummary.blackPlayer) }
        }
    }

    private fun User.toDto() = UserDto(
        id = id,
        pseudo = pseudo,
        creationDate = creationDate
    )
}
