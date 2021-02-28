@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.game.GameSummary
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

internal class GameSummaryRowMapperTest {
    private val mapper = GameSummaryRowMapper()

    @Nested
    inner class mapRow {
        private val gameSummary = GameSummary()

        private lateinit var resultSet: ResultSet

        @BeforeEach
        fun beforeEach() {
            resultSet = mockk()
        }

        @Test
        fun `should return game entity`() {
            every { resultSet.getString(DatabaseConstants.Game.IdColumnName) } returns gameSummary.id.toString()
            every {
                resultSet.getObject(DatabaseConstants.Game.CreationDateColumnName, OffsetDateTime::class.java)
            } returns gameSummary.creationDate
            every {
                resultSet.getString(DatabaseConstants.Game.WhiteTokenColumnName)
            } returns gameSummary.whiteToken.toString()
            every {
                resultSet.getString(DatabaseConstants.Game.BlackTokenColumnName)
            } returns gameSummary.blackToken.toString()
            every { resultSet.getString(DatabaseConstants.Game.TurnColorColumnName) } returns ColorType.White.sqlValue
            every { resultSet.getString(DatabaseConstants.Game.StateColumnName) } returns GameStateType.InGame.sqlValue

            mapper.mapRow(resultSet, 1) shouldBe gameSummary

            verify { resultSet.getString(DatabaseConstants.Game.IdColumnName) }
            verify { resultSet.getObject(DatabaseConstants.Game.CreationDateColumnName, OffsetDateTime::class.java) }
            verify { resultSet.getString(DatabaseConstants.Game.WhiteTokenColumnName) }
            verify { resultSet.getString(DatabaseConstants.Game.BlackTokenColumnName) }
            verify { resultSet.getString(DatabaseConstants.Game.TurnColorColumnName) }
            verify { resultSet.getString(DatabaseConstants.Game.StateColumnName) }
            confirmVerified(resultSet)
        }
    }
}
