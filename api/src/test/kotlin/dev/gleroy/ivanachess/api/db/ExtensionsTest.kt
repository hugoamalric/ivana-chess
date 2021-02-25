@file:Suppress("ClassName", "SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.Position
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import java.util.*

internal class ExtensionsTest {
    @Nested
    inner class NamedParameterJdbcTemplate {
        private lateinit var jdbcTemplate: org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

        @BeforeEach
        fun beforeEach() {
            jdbcTemplate = mockk()
        }

        @Nested
        inner class queryForNullableObject {
            private val sql = "SELECT * FROM game"
            private val params = mapOf("parameter" to "value")
            private val mapper = RowMapper { _, _ -> "result" }

            @Test
            fun `should return null`() {
                every {
                    jdbcTemplate.queryForObject(sql, ComparableMapSqlParameterSource(params), mapper)
                } throws EmptyResultDataAccessException("", 1)
                jdbcTemplate.queryForNullableObject(sql, params, mapper).shouldBeNull()
                verify { jdbcTemplate.queryForObject(sql, ComparableMapSqlParameterSource(params), mapper) }
                confirmVerified(jdbcTemplate)
            }

            @Test
            fun `should return object`() {
                val result = "result"
                every {
                    jdbcTemplate.queryForObject(sql, ComparableMapSqlParameterSource(params), mapper)
                } returns result
                jdbcTemplate.queryForNullableObject(sql, params, mapper) shouldBe result
                verify { jdbcTemplate.queryForObject(sql, ComparableMapSqlParameterSource(params), mapper) }
                confirmVerified(jdbcTemplate)
            }
        }
    }

    @Nested
    inner class ResultSet {
        private val alias = "column"

        private lateinit var resultSet: java.sql.ResultSet

        @BeforeEach
        fun beforeEach() {
            resultSet = mockk()
        }

        @Nested
        inner class getColor {
            @Test
            fun `should return color`() {
                every { resultSet.getString(alias) } returns ColorType.White.sqlValue
                resultSet.getColorType(alias) shouldBe ColorType.White
                verify { resultSet.getString(alias) }
                confirmVerified(resultSet)
            }
        }

        @Nested
        inner class getGameState {
            @Test
            fun `should return game state`() {
                every { resultSet.getString(alias) } returns GameStateType.InGame.sqlValue
                resultSet.getGameStateType(alias) shouldBe GameStateType.InGame
                verify { resultSet.getString(alias) }
                confirmVerified(resultSet)
            }
        }

        @Nested
        inner class getNullablePieceType {
            private val pieceType = PieceType.Bishop

            @Test
            fun `should return null`() {
                every { resultSet.getString(alias) } returns null
                resultSet.getNullablePieceType(alias).shouldBeNull()
                verify { resultSet.getString(alias) }
                confirmVerified(resultSet)
            }

            @Test
            fun `should return piece type`() {
                every { resultSet.getString(alias) } returns pieceType.sqlValue
                resultSet.getNullablePieceType(alias) shouldBe pieceType
                verify { resultSet.getString(alias) }
                confirmVerified(resultSet)
            }
        }

        @Nested
        inner class getPosition {
            private val position = Position.fromCoordinates("E1")

            @Test
            fun `should return position`() {
                every { resultSet.getString(alias) } returns position.toString()
                resultSet.getPosition(alias) shouldBe position
                verify { resultSet.getString(alias) }
                confirmVerified(resultSet)
            }
        }

        @Nested
        inner class getTypedObject {
            @Test
            fun `should return null`() {
                every { resultSet.getObject(alias, String::class.java) } returns null
                resultSet.getTypedObject<String>(alias).shouldBeNull()
                verify { resultSet.getObject(alias, String::class.java) }
                confirmVerified(resultSet)
            }

            @Test
            fun `should return string`() {
                val str = "str"
                every { resultSet.getObject(alias, String::class.java) } returns str
                resultSet.getTypedObject<String>(alias) shouldBe str
                verify { resultSet.getObject(alias, String::class.java) }
                confirmVerified(resultSet)
            }
        }

        @Nested
        inner class getUuid {
            private val uuid = UUID.randomUUID()

            @Test
            fun `should return uuid`() {
                every { resultSet.getString(alias) } returns uuid.toString()
                resultSet.getUuid(alias) shouldBe uuid
                verify { resultSet.getString(alias) }
                confirmVerified(resultSet)
            }
        }
    }
}
