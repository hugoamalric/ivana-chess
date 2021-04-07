@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.game

import dev.gleroy.ivanachess.api.Page
import dev.gleroy.ivanachess.api.PageOptions
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
    private val gameEntity = GameEntity(
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
                service.create(gameEntity.whitePlayer, gameEntity.whitePlayer)
            }
        }

        @Test
        fun `should create new game`() {
            every { repository.save(any()) } returns gameEntity
            service.create(gameEntity.whitePlayer, gameEntity.blackPlayer) shouldBe Match(gameEntity)
            verify { repository.save(any()) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class getEntityById {
        @Test
        fun `should throw exception if game does not exist`() {
            every { repository.fetchById(gameEntity.id) } returns null
            val exception = assertThrows<GameNotFoundException> { service.getEntityById(gameEntity.id) }
            exception shouldBe GameNotFoundException(gameEntity.id)
            verify { repository.fetchById(gameEntity.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should return game`() {
            every { repository.fetchById(gameEntity.id) } returns gameEntity
            service.getEntityById(gameEntity.id) shouldBe gameEntity
            verify { repository.fetchById(gameEntity.id) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class getGameById {
        private val game = Game(listOf(Move.Simple.fromCoordinates("E2", "E4")))

        @Test
        fun `should throw exception if game does not exist`() {
            every { repository.existsWithId(gameEntity.id) } returns false
            val exception = assertThrows<GameNotFoundException> { service.getGameById(gameEntity.id) }
            exception shouldBe GameNotFoundException(gameEntity.id)
            verify { repository.existsWithId(gameEntity.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should return game`() {
            every { repository.existsWithId(gameEntity.id) } returns true
            every { repository.fetchMoves(gameEntity.id) } returns game.moves
            service.getGameById(gameEntity.id) shouldBe game
            verify { repository.existsWithId(gameEntity.id) }
            verify { repository.fetchMoves(gameEntity.id) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class getPage {
        private val pageNb = 1
        private val pageSize = 10
        private val page = Page<GameEntity>(
            number = pageNb,
            totalItems = 10,
            totalPages = 1
        )

        @Test
        fun `should return page`() {
            every { repository.fetchPage(PageOptions(pageNb, pageSize)) } returns page

            service.getPage(pageNb, pageSize) shouldBe page

            verify { repository.fetchPage(PageOptions(pageNb, pageSize)) }
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
            every { repository.fetchById(gameEntity.id) } returns null
            val exception = assertThrows<GameNotFoundException> {
                service.play(gameEntity.id, gameEntity.whitePlayer, Move.Simple.fromCoordinates("E2", "E4"))
            }
            exception shouldBe GameNotFoundException(gameEntity.id)
            verify { repository.fetchById(gameEntity.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if user is not game player`() {
            val user = gameEntity.whitePlayer.copy(id = UUID.randomUUID())
            every { repository.fetchById(gameEntity.id) } returns gameEntity
            val exception = assertThrows<NotAllowedPlayerException> {
                service.play(gameEntity.id, user, Move.Simple.fromCoordinates("E2", "E4"))
            }
            exception shouldBe NotAllowedPlayerException(gameEntity.id, user)
            verify { repository.fetchById(gameEntity.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if white player tries to steal turn`() {
            val gameEntity = gameEntity.copy(turnColor = Piece.Color.Black)
            every { repository.fetchById(gameEntity.id) } returns gameEntity
            val exception = assertThrows<InvalidPlayerException> {
                service.play(gameEntity.id, gameEntity.whitePlayer, Move.Simple.fromCoordinates("E7", "E5"))
            }
            exception shouldBe InvalidPlayerException(gameEntity.id, gameEntity.whitePlayer)
            verify { repository.fetchById(gameEntity.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if black player tries to steal turn`() {
            every { repository.fetchById(gameEntity.id) } returns gameEntity
            val exception = assertThrows<InvalidPlayerException> {
                service.play(gameEntity.id, gameEntity.blackPlayer, Move.Simple.fromCoordinates("E2", "E4"))
            }
            exception shouldBe InvalidPlayerException(gameEntity.id, gameEntity.blackPlayer)
            verify { repository.fetchById(gameEntity.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if white player tries to move black piece`() {
            val move = Move.Simple.fromCoordinates("E7", "E5")
            every { repository.fetchById(gameEntity.id) } returns gameEntity
            every { repository.fetchMoves(gameEntity.id) } returns emptyList()
            val exception = assertThrows<InvalidMoveException> {
                service.play(gameEntity.id, gameEntity.whitePlayer, move)
            }
            exception shouldBe InvalidMoveException(gameEntity.id, gameEntity.whitePlayer, move)
            verify { repository.fetchById(gameEntity.id) }
            verify { repository.fetchMoves(gameEntity.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if black player tries to move white piece`() {
            val move = Move.Simple.fromCoordinates("A2", "A4")
            val gameEntity = gameEntity.copy(turnColor = Piece.Color.Black)
            every { repository.fetchById(gameEntity.id) } returns gameEntity
            every { repository.fetchMoves(gameEntity.id) } returns listOf(Move.Simple.fromCoordinates("E2", "E4"))
            val exception = assertThrows<InvalidMoveException> {
                service.play(gameEntity.id, gameEntity.blackPlayer, move)
            }
            exception shouldBe InvalidMoveException(gameEntity.id, gameEntity.blackPlayer, move)
            verify { repository.fetchById(gameEntity.id) }
            verify { repository.fetchMoves(gameEntity.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if move is invalid`() {
            val move = Move.Simple.fromCoordinates("E2", "E5")
            every { repository.fetchById(gameEntity.id) } returns gameEntity
            every { repository.fetchMoves(gameEntity.id) } returns emptyList()
            val exception = assertThrows<InvalidMoveException> {
                service.play(gameEntity.id, gameEntity.whitePlayer, move)
            }
            exception shouldBe InvalidMoveException(gameEntity.id, gameEntity.whitePlayer, move)
            verify { repository.fetchById(gameEntity.id) }
            verify { repository.fetchMoves(gameEntity.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should update move`() {
            val move = Move.Simple.fromCoordinates("F3", "F7")
            val updatedGame = game.play(move)
            val updatedGameEntity = gameEntity.copy(
                turnColor = updatedGame.turnColor,
                state = updatedGame.state,
                winnerColor = updatedGame.winnerColor,
            )
            every { repository.fetchById(gameEntity.id) } returns gameEntity
            every { repository.fetchMoves(gameEntity.id) } returns game.moves
            every { repository.save(updatedGameEntity) } returns updatedGameEntity
            every { repository.saveMoves(updatedGameEntity.id, updatedGame.moves) } returns Unit
            service.play(gameEntity.id, gameEntity.whitePlayer, move) shouldBe Match(updatedGameEntity, updatedGame)
            verify { repository.fetchById(gameEntity.id) }
            verify { repository.fetchMoves(gameEntity.id) }
            verify { repository.save(updatedGameEntity) }
            verify { repository.saveMoves(updatedGameEntity.id, updatedGame.moves) }
            confirmVerified(repository)
        }
    }
}
