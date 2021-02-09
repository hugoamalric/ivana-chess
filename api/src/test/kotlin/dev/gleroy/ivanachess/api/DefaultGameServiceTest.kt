@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.Move
import dev.gleroy.ivanachess.core.Piece
import io.kotlintest.matchers.throwable.shouldHaveMessage
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
    private val gameInfo = GameInfo()

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
            every { repository.create() } returns gameInfo
            service.create() shouldBe gameInfo
            verify { repository.create() }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class getById {
        @Test
        fun `should throw exception if game does not exist`() {
            val id = UUID.randomUUID()
            every { repository.getById(id) } returns null
            val exception = assertThrows<PlayException.GameIdNotFound> { service.getById(id) }
            exception shouldBe PlayException.GameIdNotFound(id)
            verify { repository.getById(id) }
            confirmVerified(repository)
        }

        @Test
        fun `should return game`() {
            every { repository.getById(gameInfo.id) } returns gameInfo
            service.getById(gameInfo.id) shouldBe gameInfo
            verify { repository.getById(gameInfo.id) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class getByToken {
        @Test
        fun `should throw exception if game does not exist`() {
            val id = UUID.randomUUID()
            every { repository.getByToken(id) } returns null
            val exception = assertThrows<PlayException.GameTokenNotFound> { service.getByToken(id) }
            exception shouldBe PlayException.GameTokenNotFound(id)
            verify { repository.getByToken(id) }
            confirmVerified(repository)
        }

        @Test
        fun `should return game`() {
            every { repository.getByToken(gameInfo.id) } returns gameInfo
            service.getByToken(gameInfo.id) shouldBe gameInfo
            verify { repository.getByToken(gameInfo.id) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class getAll {
        private val pageNb = 1
        private val pageSize = 10
        private val page = Page<GameInfo>(
            number = pageNb,
            totalItems = 10,
            totalPages = 1
        )

        @Test
        fun `should return page`() {
            every { repository.getAll(pageNb, pageSize) } returns page

            service.getAll(pageNb, pageSize) shouldBe page

            verify { repository.getAll(pageNb, pageSize) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class play {
        @Test
        fun `should throw exception if token is invalid`() {
            val token = UUID.randomUUID()
            val exception = assertThrows<IllegalArgumentException> {
                service.play(gameInfo, token, Move.Simple.fromCoordinates("E2", "E4"))
            }
            exception shouldHaveMessage "Token $token does not match white token nor black token"
        }

        @Test
        fun `should throw exception if white player tries to steal turn`() {
            val gameInfo = gameInfo.copy(game = gameInfo.game.play(Move.Simple.fromCoordinates("E2", "E4")))
            val exception = assertThrows<PlayException.InvalidPlayer> {
                service.play(gameInfo, gameInfo.whiteToken, Move.Simple.fromCoordinates("E2", "E4"))
            }
            exception shouldBe PlayException.InvalidPlayer(gameInfo.id, gameInfo.whiteToken, Piece.Color.White)
        }

        @Test
        fun `should throw exception if black player tries to steal turn`() {
            val exception = assertThrows<PlayException.InvalidPlayer> {
                service.play(gameInfo, gameInfo.blackToken, Move.Simple.fromCoordinates("E2", "E4"))
            }
            exception shouldBe PlayException.InvalidPlayer(gameInfo.id, gameInfo.blackToken, Piece.Color.Black)
        }

        @Test
        fun `should throw exception if move is invalid`() {
            val move = Move.Simple.fromCoordinates("E2", "E5")
            val exception = assertThrows<PlayException.InvalidMove> {
                service.play(gameInfo, gameInfo.whiteToken, move)
            }
            exception.id shouldBe gameInfo.id
            exception.token shouldBe gameInfo.whiteToken
            exception.color shouldBe Piece.Color.White
            exception.move shouldBe move
        }

        @Test
        fun `should update move`() {
            val move = Move.Simple.fromCoordinates("E2", "E4")
            val newGameInfo = gameInfo.copy(game = gameInfo.game.play(move))
            every { repository.update(newGameInfo) } returns newGameInfo
            service.play(gameInfo, gameInfo.whiteToken, move) shouldBe newGameInfo
            verify { repository.update(newGameInfo) }
        }
    }
}
