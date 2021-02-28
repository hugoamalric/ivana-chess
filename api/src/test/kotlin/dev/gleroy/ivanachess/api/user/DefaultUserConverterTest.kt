@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.user

import dev.gleroy.ivanachess.dto.UserDto
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultUserConverterTest {
    private val converter = DefaultUserConverter()

    @Nested
    inner class convert {
        @Test
        fun `should return simple user DTO`() {
            val user = User(
                pseudo = "admin",
                bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
            )
            converter.convert(user) shouldBe UserDto(
                id = user.id,
                pseudo = user.pseudo,
                creationDate = user.creationDate,
                role = UserDto.Role.Simple
            )
        }

        @Test
        fun `should return admin user DTO`() {
            val user = User(
                pseudo = "admin",
                bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS",
                role = User.Role.Admin
            )
            converter.convert(user) shouldBe UserDto(
                id = user.id,
                pseudo = user.pseudo,
                creationDate = user.creationDate,
                role = UserDto.Role.Admin
            )
        }

        @Test
        fun `should return super admin user DTO`() {
            val user = User(
                pseudo = "admin",
                bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS",
                role = User.Role.SuperAdmin
            )
            converter.convert(user) shouldBe UserDto(
                id = user.id,
                pseudo = user.pseudo,
                creationDate = user.creationDate,
                role = UserDto.Role.SuperAdmin
            )
        }
    }
}
