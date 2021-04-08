@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.GameEntity
import dev.gleroy.ivanachess.core.User
import dev.gleroy.ivanachess.game.Piece
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

internal class GameEntityRowMapperTest {
    private val alias = "g"

    private lateinit var whitePlayerRowMapper: UserRowMapper
    private lateinit var blackPlayerRowMapper: UserRowMapper

    private lateinit var rowMapper: GameEntityRowMapper

    @BeforeEach
    fun beforeEach() {
        whitePlayerRowMapper = mockk()
        blackPlayerRowMapper = mockk()
        rowMapper = GameEntityRowMapper(alias, whitePlayerRowMapper, blackPlayerRowMapper)
    }

    @Nested
    inner class mapRow {
        private val rowNum = 1
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

        private lateinit var resultSet: ResultSet

        @BeforeEach
        fun beforeEach() {
            resultSet = mockk()
        }

        @Test
        fun `should return game entity`() {
            every {
                resultSet.getString(DatabaseConstants.Common.IdColumnName.withAlias(alias))
            } returns gameEntity.id.toString()
            every {
                resultSet.getObject(
                    DatabaseConstants.Common.CreationDateColumnName.withAlias(alias),
                    OffsetDateTime::class.java
                )
            } returns gameEntity.creationDate
            every { whitePlayerRowMapper.mapRow(resultSet, rowNum) } returns gameEntity.whitePlayer
            every { blackPlayerRowMapper.mapRow(resultSet, rowNum) } returns gameEntity.blackPlayer
            every {
                resultSet.getString(DatabaseConstants.Game.TurnColorColumnName.withAlias(alias))
            } returns ColorType.White.sqlValue
            every {
                resultSet.getString(DatabaseConstants.Game.StateColumnName.withAlias(alias))
            } returns GameStateType.InGame.sqlValue
            every {
                resultSet.getString(DatabaseConstants.Game.WinnerColorColumnName.withAlias(alias))
            } returns null

            rowMapper.mapRow(resultSet, rowNum) shouldBe gameEntity

            verify { resultSet.getString(DatabaseConstants.Common.IdColumnName.withAlias(alias)) }
            verify {
                resultSet.getObject(
                    DatabaseConstants.Common.CreationDateColumnName.withAlias(alias),
                    OffsetDateTime::class.java
                )
            }
            verify { whitePlayerRowMapper.mapRow(resultSet, rowNum) }
            verify { blackPlayerRowMapper.mapRow(resultSet, rowNum) }
            verify { resultSet.getString(DatabaseConstants.Game.TurnColorColumnName.withAlias(alias)) }
            verify { resultSet.getString(DatabaseConstants.Game.StateColumnName.withAlias(alias)) }
            verify { resultSet.getString(DatabaseConstants.Game.WinnerColorColumnName.withAlias(alias)) }
            confirmVerified(resultSet)
        }

        @Test
        fun `should return game entity with white winner`() {
            every {
                resultSet.getString(DatabaseConstants.Common.IdColumnName.withAlias(alias))
            } returns gameEntity.id.toString()
            every {
                resultSet.getObject(
                    DatabaseConstants.Common.CreationDateColumnName.withAlias(alias),
                    OffsetDateTime::class.java
                )
            } returns gameEntity.creationDate
            every { whitePlayerRowMapper.mapRow(resultSet, rowNum) } returns gameEntity.whitePlayer
            every { blackPlayerRowMapper.mapRow(resultSet, rowNum) } returns gameEntity.blackPlayer
            every {
                resultSet.getString(DatabaseConstants.Game.TurnColorColumnName.withAlias(alias))
            } returns ColorType.White.sqlValue
            every {
                resultSet.getString(DatabaseConstants.Game.StateColumnName.withAlias(alias))
            } returns GameStateType.InGame.sqlValue
            every {
                resultSet.getString(DatabaseConstants.Game.WinnerColorColumnName.withAlias(alias))
            } returns ColorType.White.sqlValue

            rowMapper.mapRow(resultSet, rowNum) shouldBe gameEntity.copy(winnerColor = Piece.Color.White)

            verify { resultSet.getString(DatabaseConstants.Common.IdColumnName.withAlias(alias)) }
            verify {
                resultSet.getObject(
                    DatabaseConstants.Common.CreationDateColumnName.withAlias(alias),
                    OffsetDateTime::class.java
                )
            }
            verify { whitePlayerRowMapper.mapRow(resultSet, rowNum) }
            verify { blackPlayerRowMapper.mapRow(resultSet, rowNum) }
            verify { resultSet.getString(DatabaseConstants.Game.TurnColorColumnName.withAlias(alias)) }
            verify { resultSet.getString(DatabaseConstants.Game.StateColumnName.withAlias(alias)) }
            verify { resultSet.getString(DatabaseConstants.Game.WinnerColorColumnName.withAlias(alias)) }
            confirmVerified(resultSet)
        }
    }
}
