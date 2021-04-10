@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.game.Game
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class GameStateSqlEnumValueTest {
    @Nested
    inner class `from game state` {
        @Test
        fun `should return in_game`() {
            GameStateSqlEnumValue.from(Game.State.InGame) shouldBe GameStateSqlEnumValue.InGame
        }

        @Test
        fun `should return checkmate`() {
            GameStateSqlEnumValue.from(Game.State.Checkmate) shouldBe GameStateSqlEnumValue.Checkmate
        }

        @Test
        fun `should return stalemate`() {
            GameStateSqlEnumValue.from(Game.State.Stalemate) shouldBe GameStateSqlEnumValue.Stalemate
        }
    }
}
