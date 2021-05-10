@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.PasswordResetToken
import io.kotlintest.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.sql.ResultSet
import java.time.OffsetDateTime
import java.util.*

internal class PasswordResetTokenRowMapperTest {
    private val alias = "prt"

    private val rowMapper = PasswordResetTokenRowMapper(alias)

    @Nested
    inner class mapRow {
        private val rowNum = 1
        private val token = PasswordResetToken(
            userId = UUID.randomUUID(),
            expirationDate = OffsetDateTime.now(),
        )

        private lateinit var resultSet: ResultSet

        @BeforeEach
        fun beforeEach() {
            resultSet = mockk()
        }

        @AfterEach
        fun afterEach() {
            confirmVerified(resultSet)
        }

        @Test
        fun `should return password reset token`() {
            every {
                resultSet.getString(DatabaseConstants.Common.IdColumnName.withAlias(alias))
            } returns token.id.toString()
            every {
                resultSet.getObject(
                    DatabaseConstants.Common.CreationDateColumnName.withAlias(alias),
                    OffsetDateTime::class.java
                )
            } returns token.creationDate
            every {
                resultSet.getString(DatabaseConstants.PasswordResetToken.UserColumnName.withAlias(alias))
            } returns token.userId.toString()
            every {
                resultSet.getObject(
                    DatabaseConstants.PasswordResetToken.ExpirationDateColumnName.withAlias(alias),
                    OffsetDateTime::class.java
                )
            } returns token.expirationDate

            rowMapper.mapRow(resultSet, rowNum) shouldBe token

            verify { resultSet.getString(DatabaseConstants.Common.IdColumnName.withAlias(alias)) }
            verify {
                resultSet.getObject(
                    DatabaseConstants.Common.CreationDateColumnName.withAlias(alias),
                    OffsetDateTime::class.java
                )
            }
            verify { resultSet.getString(DatabaseConstants.PasswordResetToken.UserColumnName.withAlias(alias)) }
            verify {
                resultSet.getObject(
                    DatabaseConstants.PasswordResetToken.ExpirationDateColumnName.withAlias(alias),
                    OffsetDateTime::class.java
                )
            }
        }
    }
}
