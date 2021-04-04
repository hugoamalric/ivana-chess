@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.game

import dev.gleroy.ivanachess.api.Page
import dev.gleroy.ivanachess.api.user.User
import dev.gleroy.ivanachess.core.Game
import dev.gleroy.ivanachess.core.Move
import dev.gleroy.ivanachess.core.Piece
import io.kotlintest.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class DefaultGameServiceTest {
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

    private lateinit var repository: GameRepository
    private lateinit var service: DefaultGameService

    @BeforeEach
    fun beforeEach() {
        repository = mockk()
        service = DefaultGameService(repository)
    }

    @Nested
    inner class create {
        @Test
        fun `should throw exception if white and black player are same user`() {
            assertThrows<PlayersAreSameUserException> {
                service.create(gameSummary.whitePlayer, gameSummary.whitePlayer)
            }
        }

        @Test
        fun `should create new game`() {
            every { repository.save(any(), emptyList()) } returns gameSummary
            service.create(gameSummary.whitePlayer, gameSummary.blackPlayer) shouldBe GameAndSummary(gameSummary)
            verify { repository.save(any(), emptyList()) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class getAllSummaries {
        private val pageNb = 1
        private val pageSize = 10
        private val page = Page<GameSummary>(
            number = pageNb,
            totalItems = 10,
            totalPages = 1
        )

        @Test
        fun `should return page`() {
            every { repository.getAll(pageNb, pageSize) } returns page

            service.getAllSummaries(pageNb, pageSize) shouldBe page

            verify { repository.getAll(pageNb, pageSize) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class getSummaryById {
        @Test
        fun `should throw exception if game does not exist`() {
            every { repository.getById(gameSummary.id) } returns null
            val exception = assertThrows<GameNotFoundException> { service.getSummaryById(gameSummary.id) }
            exception shouldBe GameNotFoundException(gameSummary.id)
            verify { repository.getById(gameSummary.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should return game`() {
            every { repository.getById(gameSummary.id) } returns gameSummary
            service.getSummaryById(gameSummary.id) shouldBe gameSummary
            verify { repository.getById(gameSummary.id) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class getGameById {
        private val game = Game(listOf(Move.Simple.fromCoordinates("E2", "E4")))

        @Test
        fun `should throw exception if game does not exist`() {
            every { repository.existsById(gameSummary.id) } returns false
            val exception = assertThrows<GameNotFoundException> { service.getGameById(gameSummary.id) }
            exception shouldBe GameNotFoundException(gameSummary.id)
            verify { repository.existsById(gameSummary.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should return game`() {
            every { repository.existsById(gameSummary.id) } returns true
            every { repository.getMoves(gameSummary.id) } returns game.moves
            service.getGameById(gameSummary.id) shouldBe game
            verify { repository.existsById(gameSummary.id) }
            verify { repository.getMoves(gameSummary.id) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class play {
        private val game = Game(
            moves = listOf(
                Move.Simple.fromCoordinates("E2", "E4"),
                Move.Simple.fromCoordinates("E7", "E5"),
                Move.Simple.fromCoordinates("D1", "F3"),
                Move.Simple.fromCoordinates("A7", "A6"),
                Move.Simple.fromCoordinates("F1", "C4"),
                Move.Simple.fromCoordinates("B7", "B6"),
            )
        )

        @Test
        fun `should throw exception if game does not exist`() {
            every { repository.getById(gameSummary.id) } returns null
            val exception = assertThrows<GameNotFoundException> {
                service.play(gameSummary.id, gameSummary.whitePlayer, Move.Simple.fromCoordinates("E2", "E4"))
            }
            exception shouldBe GameNotFoundException(gameSummary.id)
            verify { repository.getById(gameSummary.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if user is not game player`() {
            val user = gameSummary.whitePlayer.copy(id = UUID.randomUUID())
            every { repository.getById(gameSummary.id) } returns gameSummary
            val exception = assertThrows<NotAllowedPlayerException> {
                service.play(gameSummary.id, user, Move.Simple.fromCoordinates("E2", "E4"))
            }
            exception shouldBe NotAllowedPlayerException(gameSummary.id, user)
            verify { repository.getById(gameSummary.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if white player tries to steal turn`() {
            val gameSummary = gameSummary.copy(turnColor = Piece.Color.Black)
            every { repository.getById(gameSummary.id) } returns gameSummary
            val exception = assertThrows<InvalidPlayerException> {
                service.play(gameSummary.id, gameSummary.whitePlayer, Move.Simple.fromCoordinates("E7", "E5"))
            }
            exception shouldBe InvalidPlayerException(gameSummary.id, gameSummary.whitePlayer)
            verify { repository.getById(gameSummary.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if black player tries to steal turn`() {
            every { repository.getById(gameSummary.id) } returns gameSummary
            val exception = assertThrows<InvalidPlayerException> {
                service.play(gameSummary.id, gameSummary.blackPlayer, Move.Simple.fromCoordinates("E2", "E4"))
            }
            exception shouldBe InvalidPlayerException(gameSummary.id, gameSummary.blackPlayer)
            verify { repository.getById(gameSummary.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if white player tries to move black piece`() {
            val move = Move.Simple.fromCoordinates("E7", "E5")
            every { repository.getById(gameSummary.id) } returns gameSummary
            every { repository.getMoves(gameSummary.id) } returns emptyList()
            val exception = assertThrows<InvalidMoveException> {
                service.play(gameSummary.id, gameSummary.whitePlayer, move)
            }
            exception shouldBe InvalidMoveException(gameSummary.id, gameSummary.whitePlayer, move)
            verify { repository.getById(gameSummary.id) }
            verify { repository.getMoves(gameSummary.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if black player tries to move white piece`() {
            val move = Move.Simple.fromCoordinates("A2", "A4")
            val gameSummary = gameSummary.copy(turnColor = Piece.Color.Black)
            every { repository.getById(gameSummary.id) } returns gameSummary
            every { repository.getMoves(gameSummary.id) } returns listOf(Move.Simple.fromCoordinates("E2", "E4"))
            val exception = assertThrows<InvalidMoveException> {
                service.play(gameSummary.id, gameSummary.blackPlayer, move)
            }
            exception shouldBe InvalidMoveException(gameSummary.id, gameSummary.blackPlayer, move)
            verify { repository.getById(gameSummary.id) }
            verify { repository.getMoves(gameSummary.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if move is invalid`() {
            val move = Move.Simple.fromCoordinates("E2", "E5")
            every { repository.getById(gameSummary.id) } returns gameSummary
            every { repository.getMoves(gameSummary.id) } returns emptyList()
            val exception = assertThrows<InvalidMoveException> {
                service.play(gameSummary.id, gameSummary.whitePlayer, move)
            }
            exception shouldBe InvalidMoveException(gameSummary.id, gameSummary.whitePlayer, move)
            verify { repository.getById(gameSummary.id) }
            verify { repository.getMoves(gameSummary.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should update move`() {
            val move = Move.Simple.fromCoordinates("F3", "F7")
            val newGame = game.play(move)
            val newGameSummary = gameSummary.copy(
                turnColor = newGame.turnColor,
                state = newGame.state,
                winnerColor = newGame.winnerColor,
            )
            every { repository.getById(gameSummary.id) } returns gameSummary
            every { repository.getMoves(gameSummary.id) } returns game.moves
            every { repository.save(newGameSummary, newGame.moves) } returns newGameSummary
            service.play(gameSummary.id, gameSummary.whitePlayer, move) shouldBe GameAndSummary(newGameSummary, newGame)
            verify { repository.getById(gameSummary.id) }
            verify { repository.getMoves(gameSummary.id) }
            verify { repository.save(newGameSummary, newGame.moves) }
            confirmVerified(repository)
        }
    }
}
