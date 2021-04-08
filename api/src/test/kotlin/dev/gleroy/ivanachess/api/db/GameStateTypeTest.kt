@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.game.Game
import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class GameStateTypeTest {
    @Nested
    inner class `from game state` {
        @Test
        fun `should return in_game`() {
            GameStateType.from(Game.State.InGame) shouldBe GameStateType.InGame
        }

        @Test
        fun `should return checkmate`() {
            GameStateType.from(Game.State.Checkmate) shouldBe GameStateType.Checkmate
        }

        @Test
        fun `should return stalemate`() {
            GameStateType.from(Game.State.Stalemate) shouldBe GameStateType.Stalemate
        }
    }

    @Nested
    inner class `from SQL type value` {
        @Test
        fun `should throw exception if SQL type value is not a valid game state`() {
            val sqlValue = "pawn"
            val exception = assertThrows<IllegalArgumentException> { GameStateType.from(sqlValue) }
            exception shouldHaveMessage "Unknown game state '$sqlValue'"
        }

        @Test
        fun `should return in_game`() {
            GameStateType.from(GameStateType.InGame.sqlValue) shouldBe GameStateType.InGame
        }

        @Test
        fun `should return checkmate`() {
            GameStateType.from(GameStateType.Checkmate.sqlValue) shouldBe GameStateType.Checkmate
        }

        @Test
        fun `should return stalemate`() {
            GameStateType.from(GameStateType.Stalemate.sqlValue) shouldBe GameStateType.Stalemate
        }
    }
}
