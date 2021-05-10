@file:Suppress("ClassName", "SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.PasswordResetToken
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Clock
import java.time.OffsetDateTime

@SpringBootTest
@ActiveProfiles("dev")
internal class PasswordResetTokenDatabaseRepositoryTest :
    AbstractEntityDatabaseRepositoryTest<PasswordResetToken, PasswordResetTokenDatabaseRepository>() {

    @BeforeEach
    override fun beforeEach() {
        super.beforeEach()
        items = passwordResetTokens
        repository = passwordResetTokenRepository
    }

    @Nested
    inner class save :
        AbstractEntityDatabaseRepositoryTest<PasswordResetToken, PasswordResetTokenDatabaseRepository>.save() {

        override fun `should update entity`() {

        }

        @Test
        fun `should delete previous token before saving new one`() {
            repository.save(
                entity = PasswordResetToken(
                    userId = items[0].userId,
                    expirationDate = OffsetDateTime.now(Clock.systemUTC()).plusMinutes(15),
                )
            )
        }
    }

    override fun updateEntity(entity: PasswordResetToken) = entity.copy()
}
