@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.core.User
import dev.gleroy.ivanachess.io.UserRepresentation
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultUserConverterTest {
    private val converter = DefaultUserConverter()

    @Nested
    inner class convertToPublicRepresentation {
        @Test
        fun `should return simple user representation`() {
            val user = User(
                pseudo = "admin",
                email = "admin@ivanachess.loc",
                bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
            )
            converter.convertToPublicRepresentation(user) shouldBe UserRepresentation.Public(
                id = user.id,
                pseudo = user.pseudo,
                creationDate = user.creationDate,
                role = UserRepresentation.Role.Simple,
            )
        }

        @Test
        fun `should return admin user representation`() {
            val user = User(
                pseudo = "admin",
                email = "admin@ivanachess.loc",
                bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS",
                role = User.Role.Admin
            )
            converter.convertToPublicRepresentation(user) shouldBe UserRepresentation.Public(
                id = user.id,
                pseudo = user.pseudo,
                creationDate = user.creationDate,
                role = UserRepresentation.Role.Admin,
            )
        }

        @Test
        fun `should return super admin user representation`() {
            val user = User(
                pseudo = "admin",
                email = "admin@ivanachess.loc",
                bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS",
                role = User.Role.SuperAdmin
            )
            converter.convertToPublicRepresentation(user) shouldBe UserRepresentation.Public(
                id = user.id,
                pseudo = user.pseudo,
                creationDate = user.creationDate,
                role = UserRepresentation.Role.SuperAdmin,
            )
        }
    }
}
