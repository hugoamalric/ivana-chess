@file:Suppress("ClassName")

package dev.gleroy.ivanachess.core

import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class MoveTest {
    @Nested
    inner class fromCoordinates {
        @Test
        fun `should return move`() {
            val expected = Move(Position(1, 1), Position(2, 2))
            Move.fromCoordinates("A1", "B2") shouldBe expected
        }
    }
}
