@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.User
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class RoleSqlEnumValueTest {
    @Nested
    inner class `from role` {
        @Test
        fun `should return simple`() {
            RoleSqlEnumValue.from(User.Role.Simple) shouldBe RoleSqlEnumValue.Simple
        }

        @Test
        fun `should return admin`() {
            RoleSqlEnumValue.from(User.Role.Admin) shouldBe RoleSqlEnumValue.Admin
        }

        @Test
        fun `should return super_admin`() {
            RoleSqlEnumValue.from(User.Role.SuperAdmin) shouldBe RoleSqlEnumValue.SuperAdmin
        }
    }
}
