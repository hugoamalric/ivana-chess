@file:Suppress("ClassName", "SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.user.User
import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

@SpringBootTest
@ActiveProfiles("dev")
internal class UserDatabaseRepositoryTest : AbstractDatabaseRepositoryTest<User, UserDatabaseRepository>() {
    @Nested
    inner class existsWithEmail : existsWith<String>() {
        override val wrongValue get() = "noreply@ivanachess.loc"

        override fun existsWith(value: String, excluding: Set<UUID>) = repository.existsWithEmail(value, excluding)

        override fun valueFromEntity(entity: User) = entity.email
    }

    @Nested
    inner class existsByPseudo : existsWith<String>() {
        override val wrongValue get() = "pseudo"

        override fun existsWith(value: String, excluding: Set<UUID>) = repository.existsWithPseudo(value, excluding)

        override fun valueFromEntity(entity: User) = entity.pseudo
    }

    @Nested
    inner class fetchByEmail : fetchBy<String>() {
        override val wrongValue get() = "noreply@ivanachess.loc"

        override fun fetchBy(value: String) = repository.fetchByEmail(value)

        override fun valueFromEntity(entity: User) = entity.email
    }

    @Nested
    inner class fetchByPseudo : fetchBy<String>() {
        override val wrongValue get() = "pseudo"

        override fun fetchBy(value: String) = repository.fetchByPseudo(value)

        override fun valueFromEntity(entity: User) = entity.pseudo
    }

    @Nested
    inner class searchByPseudo {
        @Test
        fun `should throw exception if maxSize is zero`() {
            val exception = assertThrows<IllegalArgumentException> { repository.searchByPseudo("q", 0) }
            exception shouldHaveMessage "maxSize must be strictly positive"
        }

        @Test
        fun `should return list containing only user50`() {
            repository.searchByPseudo("UsEr50", 10) shouldBe listOf(entities[50])
        }

        @Test
        fun `should return 2 first match`() {
            repository.searchByPseudo("UsEr1", 2) shouldBe listOf(entities[1], entities[10])
        }

        @Test
        fun `should return 10 first match`() {
            repository.searchByPseudo("UsEr", 10) shouldBe entities
                .sortedBy { it.pseudo }
                .subList(0, 10)
        }

        @Test
        fun `should return 10 first match excluding user_0 and user_1`() {
            repository.searchByPseudo("UsEr", 10, setOf(entities[0].id, entities[1].id)) shouldBe entities
                .sortedBy { it.pseudo }
                .subList(2, 12)
        }
    }

    override fun createEntity(index: Int) = User(
        pseudo = "user$index",
        email = "user$index@ivanachess.loc",
        creationDate = OffsetDateTime.now(Clock.systemUTC()),
        bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
    )

    override fun updateEntity(entity: User) = entity.copy(
        email = "${entity.pseudo}_2@ivanachess.loc",
        bcryptPassword = "\$2y\$12\$vzd9ZPetuPmU1PoC6hFPsOiC93S8rB.TJTsGdVI7huo4lNzpilZ4W",
        role = User.Role.SuperAdmin,
    )
}
