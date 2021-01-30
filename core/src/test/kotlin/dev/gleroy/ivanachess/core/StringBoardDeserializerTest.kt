@file:Suppress("ClassName")

package dev.gleroy.ivanachess.core

import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class StringBoardDeserializerTest {
    private val deserializer = StringBoardDeserializer()
    private val boardStr = String(javaClass.getResourceAsStream("/board/initial.txt").readAllBytes())

    @Nested
    inner class deserialize {
        @Test
        fun `should throw exception if byte array is not a valid board`() {
            val str = boardStr.lines().dropLast(1).reduce { acc, rowStr -> "$acc\n$rowStr" }
            val exception = assertThrows<IllegalArgumentException> { deserializer.deserialize(str.toByteArray()) }
            exception shouldHaveMessage "'$str' is not a valid board"
        }

        @Test
        fun `should throw exception if line 2 is invalid`() {
            val str = boardStr.replace("♝", "x")
            val exception = assertThrows<IllegalArgumentException> { deserializer.deserialize(str.toByteArray()) }
            exception shouldHaveMessage "Line 2 is invalid"
        }

        @Test
        fun `should return initial board`() {
            deserializer.deserialize(boardStr.toByteArray()) shouldBe Board.Initial
        }
    }
}
