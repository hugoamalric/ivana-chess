@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api

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
    private val gameSummary = GameSummary()

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
        fun `should create new game`() {
            every { repository.save(any(), emptyList()) } returns gameSummary
            service.create() shouldBe gameSummary
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
            val id = UUID.randomUUID()
            every { repository.getById(id) } returns null
            val exception = assertThrows<GameIdNotFoundException> { service.getSummaryById(id) }
            exception shouldBe GameIdNotFoundException(id)
            verify { repository.getById(id) }
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
    inner class getSummaryByToken {
        @Test
        fun `should throw exception if game does not exist`() {
            val id = UUID.randomUUID()
            every { repository.getByToken(id) } returns null
            val exception = assertThrows<GameTokenNotFoundException> { service.getSummaryByToken(id) }
            exception shouldBe GameTokenNotFoundException(id)
            verify { repository.getByToken(id) }
            confirmVerified(repository)
        }

        @Test
        fun `should return game`() {
            every { repository.getByToken(gameSummary.id) } returns gameSummary
            service.getSummaryByToken(gameSummary.id) shouldBe gameSummary
            verify { repository.getByToken(gameSummary.id) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class getGameById {
        private val game = Game(listOf(Move.Simple.fromCoordinates("E2", "E4")))

        @Test
        fun `should throw exception if game does not exist`() {
            every { repository.exists(gameSummary.id) } returns false
            val exception = assertThrows<GameIdNotFoundException> { service.getGameById(gameSummary.id) }
            exception shouldBe GameIdNotFoundException(gameSummary.id)
            verify { repository.exists(gameSummary.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should return game`() {
            every { repository.exists(gameSummary.id) } returns true
            every { repository.getMoves(gameSummary.id) } returns game.moves
            service.getGameById(gameSummary.id) shouldBe game
            verify { repository.exists(gameSummary.id) }
            verify { repository.getMoves(gameSummary.id) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class play {
        @Test
        fun `should throw exception if token is invalid`() {
            val token = UUID.randomUUID()
            every { repository.getByToken(token) } returns null
            val exception = assertThrows<GameTokenNotFoundException> {
                service.play(token, Move.Simple.fromCoordinates("E2", "E4"))
            }
            exception shouldBe GameTokenNotFoundException(token)
            verify { repository.getByToken(token) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if white player tries to steal turn`() {
            val gameSummary = gameSummary.copy(turnColor = Piece.Color.Black)
            every { repository.getByToken(gameSummary.whiteToken) } returns gameSummary
            val exception = assertThrows<PlayException.InvalidPlayer> {
                service.play(gameSummary.whiteToken, Move.Simple.fromCoordinates("E7", "E5"))
            }
            exception shouldBe PlayException.InvalidPlayer(gameSummary.id, gameSummary.whiteToken, Piece.Color.White)
            verify { repository.getByToken(gameSummary.whiteToken) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if black player tries to steal turn`() {
            every { repository.getByToken(gameSummary.blackToken) } returns gameSummary
            val exception = assertThrows<PlayException.InvalidPlayer> {
                service.play(gameSummary.blackToken, Move.Simple.fromCoordinates("E2", "E4"))
            }
            exception shouldBe PlayException.InvalidPlayer(gameSummary.id, gameSummary.blackToken, Piece.Color.Black)
            verify { repository.getByToken(gameSummary.blackToken) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if white player tries to move black piece`() {
            val move = Move.Simple.fromCoordinates("E7", "E5")
            every { repository.getByToken(gameSummary.whiteToken) } returns gameSummary
            every { repository.getMoves(gameSummary.id) } returns emptyList()
            val exception = assertThrows<PlayException.InvalidMove> { service.play(gameSummary.whiteToken, move) }
            exception.id shouldBe gameSummary.id
            exception.token shouldBe gameSummary.whiteToken
            exception.color shouldBe Piece.Color.White
            exception.move shouldBe move
            verify { repository.getByToken(gameSummary.whiteToken) }
            verify { repository.getMoves(gameSummary.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if black player tries to move white piece`() {
            val move = Move.Simple.fromCoordinates("A2", "A4")
            val gameSummary = gameSummary.copy(turnColor = Piece.Color.Black)
            every { repository.getByToken(gameSummary.blackToken) } returns gameSummary
            every { repository.getMoves(gameSummary.id) } returns listOf(Move.Simple.fromCoordinates("E2", "E4"))
            val exception = assertThrows<PlayException.InvalidMove> { service.play(gameSummary.blackToken, move) }
            exception.id shouldBe gameSummary.id
            exception.token shouldBe gameSummary.blackToken
            exception.color shouldBe Piece.Color.Black
            exception.move shouldBe move
            verify { repository.getByToken(gameSummary.blackToken) }
            verify { repository.getMoves(gameSummary.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if move is invalid`() {
            val move = Move.Simple.fromCoordinates("E2", "E5")
            every { repository.getByToken(gameSummary.whiteToken) } returns gameSummary
            every { repository.getMoves(gameSummary.id) } returns emptyList()
            val exception = assertThrows<PlayException.InvalidMove> { service.play(gameSummary.whiteToken, move) }
            exception.id shouldBe gameSummary.id
            exception.token shouldBe gameSummary.whiteToken
            exception.color shouldBe Piece.Color.White
            exception.move shouldBe move
            verify { repository.getByToken(gameSummary.whiteToken) }
            verify { repository.getMoves(gameSummary.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should update move`() {
            val game = Game()
            val move = Move.Simple.fromCoordinates("E2", "E4")
            every { repository.getByToken(gameSummary.whiteToken) } returns gameSummary
            every { repository.getMoves(gameSummary.id) } returns emptyList()
            val newGame = game.play(move)
            val newGameSummary = gameSummary.copy(
                turnColor = newGame.turnColor,
                state = newGame.state
            )
            every { repository.save(newGameSummary) } returns newGameSummary
            service.play(gameSummary.whiteToken, move) shouldBe newGameSummary
            verify { repository.getByToken(gameSummary.whiteToken) }
            verify { repository.save(newGameSummary) }
            verify { repository.getMoves(gameSummary.id) }
            confirmVerified(repository)
        }
    }
}
