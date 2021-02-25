@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.User
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.sql.ResultSet
import java.time.OffsetDateTime

internal class UserRowMapperTest {
    private val mapper = UserRowMapper()

    @Nested
    inner class mapRow {
        private val user = User(
            pseudo = "admin",
            bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
        )

        private lateinit var resultSet: ResultSet

        @BeforeEach
        fun beforeEach() {
            resultSet = mockk()
        }

        @Test
        fun `should return user`() {
            every { resultSet.getString(DatabaseConstants.User.IdColumnName) } returns user.id.toString()
            every { resultSet.getString(DatabaseConstants.User.PseudoColumnName) } returns user.pseudo
            every {
                resultSet.getObject(DatabaseConstants.User.CreationDateColumnName, OffsetDateTime::class.java)
            } returns user.creationDate
            every { resultSet.getString(DatabaseConstants.User.BCryptPasswordColumnName) } returns user.bcryptPassword
            mapper.mapRow(resultSet, 1) shouldBe user
            verify { resultSet.getString(DatabaseConstants.User.IdColumnName) }
            verify { resultSet.getString(DatabaseConstants.User.PseudoColumnName) }
            verify { resultSet.getObject(DatabaseConstants.User.CreationDateColumnName, OffsetDateTime::class.java) }
            verify { resultSet.getString(DatabaseConstants.User.BCryptPasswordColumnName) }
        }
    }
}
