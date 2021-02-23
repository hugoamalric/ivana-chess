@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.GameSummary
import io.kotlintest.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.sql.ResultSet
import java.time.OffsetDateTime
import java.util.*

internal class GameSummaryRowMapperTest {
    private lateinit var mapper: GameSummaryRowMapper

    @BeforeEach
    fun beforeEach() {
        mapper = GameSummaryRowMapper()
    }

    @Nested
    inner class mapRow {
        private val gameInfo = GameSummary(
            id = UUID.randomUUID(),
            creationDate = OffsetDateTime.now(),
            whiteToken = UUID.randomUUID(),
            blackToken = UUID.randomUUID()
        )

        private lateinit var resultSet: ResultSet

        @BeforeEach
        fun beforeEach() {
            resultSet = mockk()
        }

        @Test
        fun `should return game entity`() {
            every { resultSet.getString(DatabaseConstants.Game.IdColumnName) } returns gameInfo.id.toString()
            every {
                resultSet.getObject(DatabaseConstants.Game.CreationDateColumnName, OffsetDateTime::class.java)
            } returns gameInfo.creationDate
            every {
                resultSet.getString(DatabaseConstants.Game.WhiteTokenColumnName)
            } returns gameInfo.whiteToken.toString()
            every {
                resultSet.getString(DatabaseConstants.Game.BlackTokenColumnName)
            } returns gameInfo.blackToken.toString()

            mapper.mapRow(resultSet, 1) shouldBe gameInfo

            verify { resultSet.getString(DatabaseConstants.Game.IdColumnName) }
            verify { resultSet.getObject(DatabaseConstants.Game.CreationDateColumnName, OffsetDateTime::class.java) }
            verify { resultSet.getString(DatabaseConstants.Game.WhiteTokenColumnName) }
            verify { resultSet.getString(DatabaseConstants.Game.BlackTokenColumnName) }
            confirmVerified(resultSet)
        }
    }
}
