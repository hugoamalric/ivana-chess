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
    inner class convertToPrivateRepresentation : convertToRepresentation() {
        override fun convertToRepresentation(user: User) = converter.convertToPrivateRepresentation(user)

        override fun createRepresentation(user: User) = UserRepresentation.Private(
            id = user.id,
            pseudo = user.pseudo,
            email = user.email,
            creationDate = user.creationDate,
            role = user.role.toRepresentation(),
        )
    }

    @Nested
    inner class convertToPublicRepresentation : convertToRepresentation() {
        override fun convertToRepresentation(user: User) = converter.convertToPublicRepresentation(user)

        override fun createRepresentation(user: User) = UserRepresentation.Public(
            id = user.id,
            pseudo = user.pseudo,
            creationDate = user.creationDate,
            role = user.role.toRepresentation(),
        )
    }

    abstract inner class convertToRepresentation {
        @Test
        fun `should return simple user representation`() {
            val user = User(
                pseudo = "admin",
                email = "admin@ivanachess.loc",
                bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
            )
            convertToRepresentation(user) shouldBe createRepresentation(user)
        }

        @Test
        fun `should return admin user representation`() {
            val user = User(
                pseudo = "admin",
                email = "admin@ivanachess.loc",
                bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS",
                role = User.Role.Admin
            )
            convertToRepresentation(user) shouldBe createRepresentation(user)
        }

        @Test
        fun `should return super admin user representation`() {
            val user = User(
                pseudo = "admin",
                email = "admin@ivanachess.loc",
                bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS",
                role = User.Role.SuperAdmin
            )
            convertToRepresentation(user) shouldBe createRepresentation(user)
        }

        abstract fun convertToRepresentation(user: User): UserRepresentation

        abstract fun createRepresentation(user: User): UserRepresentation
    }

    private fun User.Role.toRepresentation() = when (this) {
        User.Role.Simple -> UserRepresentation.Role.Simple
        User.Role.Admin -> UserRepresentation.Role.Admin
        User.Role.SuperAdmin -> UserRepresentation.Role.SuperAdmin
    }
}
