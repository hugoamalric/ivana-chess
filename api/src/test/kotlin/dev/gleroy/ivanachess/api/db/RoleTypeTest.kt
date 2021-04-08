@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.User
import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class RoleTypeTest {
    @Nested
    inner class `from role` {
        @Test
        fun `should return simple`() {
            RoleType.from(User.Role.Simple) shouldBe RoleType.Simple
        }

        @Test
        fun `should return admin`() {
            RoleType.from(User.Role.Admin) shouldBe RoleType.Admin
        }

        @Test
        fun `should return super_admin`() {
            RoleType.from(User.Role.SuperAdmin) shouldBe RoleType.SuperAdmin
        }
    }

    @Nested
    inner class `from SQL type value` {
        @Test
        fun `should throw exception if SQL type value is not a valid role`() {
            val sqlValue = "pawn"
            val exception = assertThrows<IllegalArgumentException> { RoleType.from(sqlValue) }
            exception shouldHaveMessage "Unknown role '$sqlValue'"
        }

        @Test
        fun `should return simple`() {
            RoleType.from(RoleType.Simple.sqlValue) shouldBe RoleType.Simple
        }

        @Test
        fun `should return admin`() {
            RoleType.from(RoleType.Admin.sqlValue) shouldBe RoleType.Admin
        }

        @Test
        fun `should return super_admin`() {
            RoleType.from(RoleType.SuperAdmin.sqlValue) shouldBe RoleType.SuperAdmin
        }
    }
}
