@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.game

import dev.gleroy.ivanachess.api.AbstractEntityServiceTest
import dev.gleroy.ivanachess.api.EntityNotFoundException
import dev.gleroy.ivanachess.api.user.User
import dev.gleroy.ivanachess.core.Game
import dev.gleroy.ivanachess.core.Move
import dev.gleroy.ivanachess.core.Piece
import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class DefaultGameServiceTest : AbstractEntityServiceTest<GameEntity, GameRepository, DefaultGameService>() {
    @Nested
    inner class create {
        private val gameEntity = createEntity()

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
    inner class getGameById {
        private val id = UUID.randomUUID()
        private val game = Game(listOf(Move.Simple.fromCoordinates("E2", "E4")))

        @Test
        fun `should throw exception if game does not exist`() {
            every { repository.existsWithId(id) } returns false
            val exception = assertThrows<EntityNotFoundException> { service.getGameById(id) }
            exception shouldHaveMessage "Entity $id does not exist"
            verify { repository.existsWithId(id) }
            confirmVerified(repository)
        }

        @Test
        fun `should return game`() {
            every { repository.existsWithId(id) } returns true
            every { repository.fetchMoves(id) } returns game.moves
            service.getGameById(id) shouldBe game
            verify { repository.existsWithId(id) }
            verify { repository.fetchMoves(id) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class play {
        private val gameEntity = createEntity()

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
            val exception = assertThrows<EntityNotFoundException> {
                service.play(gameEntity.id, gameEntity.whitePlayer, Move.Simple.fromCoordinates("E2", "E4"))
            }
            exception shouldHaveMessage "Entity ${gameEntity.id} does not exist"
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

    override fun createEntity() = GameEntity(
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

    override fun createService() = DefaultGameService(repository)

    override fun mockRepository() = mockk<GameRepository>()
}
