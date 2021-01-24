@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api

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
    inner class get {
        @Test
        fun `should throw exception if game does not exist`() {
            val id = UUID.randomUUID()
            every { repository.getById(id) } returns null
            val exception = assertThrows<PlayException.GameIdNotFound> { service.get(id) }
            exception shouldBe PlayException.GameIdNotFound(id)
            verify { repository.getById(id) }
            confirmVerified(repository)
        }

        @Test
        fun `should return game`() {
            every { repository.getById(gameInfo.id) } returns gameInfo
            service.get(gameInfo.id) shouldBe gameInfo
            verify { repository.getById(gameInfo.id) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class play {
        @Test
        fun `should throw exception if game does not exist`() {
            val token = UUID.randomUUID()
            every { repository.getByToken(token) } returns null
            val exception = assertThrows<PlayException.GameTokenNotFound> {
                service.play(token, Move.fromCoordinates("E2", "E4"))
            }
            exception shouldBe PlayException.GameTokenNotFound(token)
            verify { repository.getByToken(token) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if white player tries to steal turn`() {
            val gameInfo = gameInfo.copy(game = gameInfo.game.play(Move.fromCoordinates("E2", "E4")))
            every { repository.getByToken(gameInfo.whiteToken) } returns gameInfo
            val exception = assertThrows<PlayException.InvalidPlayer> {
                service.play(gameInfo.whiteToken, Move.fromCoordinates("E2", "E4"))
            }
            exception shouldBe PlayException.InvalidPlayer(gameInfo.id, gameInfo.whiteToken, Piece.Color.White)
            verify { repository.getByToken(gameInfo.whiteToken) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if black player tries to steal turn`() {
            every { repository.getByToken(gameInfo.blackToken) } returns gameInfo
            val exception = assertThrows<PlayException.InvalidPlayer> {
                service.play(gameInfo.blackToken, Move.fromCoordinates("E2", "E4"))
            }
            exception shouldBe PlayException.InvalidPlayer(gameInfo.id, gameInfo.blackToken, Piece.Color.Black)
            verify { repository.getByToken(gameInfo.blackToken) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if move is invalid`() {
            val move = Move.fromCoordinates("E2", "E5")
            every { repository.getByToken(gameInfo.whiteToken) } returns gameInfo
            val exception = assertThrows<PlayException.InvalidMove> { service.play(gameInfo.whiteToken, move) }
            exception.id shouldBe gameInfo.id
            exception.token shouldBe gameInfo.whiteToken
            exception.color shouldBe Piece.Color.White
            exception.move shouldBe move
            verify { repository.getByToken(gameInfo.whiteToken) }
            confirmVerified(repository)
        }

        @Test
        fun `should update move`() {
            val move = Move.fromCoordinates("E2", "E4")
            val newGameInfo = gameInfo.copy(game = gameInfo.game.play(move))
            every { repository.getByToken(gameInfo.whiteToken) } returns gameInfo
            every { repository.update(newGameInfo) } returns newGameInfo
            service.play(gameInfo.whiteToken, move) shouldBe newGameInfo
            verify { repository.getByToken(gameInfo.whiteToken) }
            verify { repository.update(newGameInfo) }
            confirmVerified(repository)
        }
    }
}
