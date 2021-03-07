@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.game.GameSummary
import dev.gleroy.ivanachess.api.user.User
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
    private val alias = "g"

    private lateinit var whitePlayerRowMapper: UserRowMapper
    private lateinit var blackPlayerRowMapper: UserRowMapper

    private lateinit var rowMapper: GameSummaryRowMapper

    @BeforeEach
    fun beforeEach() {
        whitePlayerRowMapper = mockk()
        blackPlayerRowMapper = mockk()
        rowMapper = GameSummaryRowMapper(alias, whitePlayerRowMapper, blackPlayerRowMapper)
    }

    @Nested
    inner class mapRow {
        private val rowNum = 1
        private val gameSummary = GameSummary(
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

        private lateinit var resultSet: ResultSet

        @BeforeEach
        fun beforeEach() {
            resultSet = mockk()
        }

        @Test
        fun `should return game entity`() {
            every {
                resultSet.getString(DatabaseConstants.Game.IdColumnName.withAlias(alias))
            } returns gameSummary.id.toString()
            every {
                resultSet.getObject(
                    DatabaseConstants.Game.CreationDateColumnName.withAlias(alias),
                    OffsetDateTime::class.java
                )
            } returns gameSummary.creationDate
            every { whitePlayerRowMapper.mapRow(resultSet, rowNum) } returns gameSummary.whitePlayer
            every { blackPlayerRowMapper.mapRow(resultSet, rowNum) } returns gameSummary.blackPlayer
            every {
                resultSet.getString(DatabaseConstants.Game.TurnColorColumnName.withAlias(alias))
            } returns ColorType.White.sqlValue
            every {
                resultSet.getString(DatabaseConstants.Game.StateColumnName.withAlias(alias))
            } returns GameStateType.InGame.sqlValue

            rowMapper.mapRow(resultSet, rowNum) shouldBe gameSummary

            verify { resultSet.getString(DatabaseConstants.Game.IdColumnName.withAlias(alias)) }
            verify {
                resultSet.getObject(
                    DatabaseConstants.Game.CreationDateColumnName.withAlias(alias),
                    OffsetDateTime::class.java
                )
            }
            verify { whitePlayerRowMapper.mapRow(resultSet, rowNum) }
            verify { blackPlayerRowMapper.mapRow(resultSet, rowNum) }
            verify { resultSet.getString(DatabaseConstants.Game.TurnColorColumnName.withAlias(alias)) }
            verify { resultSet.getString(DatabaseConstants.Game.StateColumnName.withAlias(alias)) }
            confirmVerified(resultSet)
        }
    }
}
