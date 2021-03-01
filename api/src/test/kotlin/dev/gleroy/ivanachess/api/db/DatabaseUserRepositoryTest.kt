@file:Suppress("ClassName", "SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.user.User
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.time.ZoneOffset

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class DatabaseUserRepositoryTest : AbstractDatabaseRepositoryTest<User, DatabaseUserRepository>() {
    @Nested
    inner class existsByEmail {
        @Test
        fun `should return false`() {
            repository.existsByEmail("email").shouldBeFalse()
        }

        @Test
        fun `should return true`() {
            repository.existsByEmail(entity.email).shouldBeTrue()
        }
    }

    @Nested
    inner class `existsByEmail ignoring one user` {
        @Test
        fun `should return false if user does not exist`() {
            repository.existsByEmail("email", entity.id).shouldBeFalse()
        }

        @Test
        fun `should return false if user is ignoring one`() {
            repository.existsByEmail(entity.email, entity.id).shouldBeFalse()
        }

        @Test
        fun `should return true`() {
            repository.existsByEmail(entities[1].email, entity.id).shouldBeTrue()
        }
    }

    @Nested
    inner class existsByPseudo {
        @Test
        fun `should return false`() {
            repository.existsByPseudo("pseudo").shouldBeFalse()
        }

        @Test
        fun `should return true`() {
            repository.existsByPseudo(entity.pseudo).shouldBeTrue()
        }
    }

    @Nested
    inner class getByEmail {
        @Test
        fun `should return null`() {
            repository.getByEmail("email").shouldBeNull()
        }

        @Test
        fun `should return user`() {
            repository.getByEmail(entity.email) shouldBe entity
        }
    }

    @Nested
    inner class getByPseudo {
        @Test
        fun `should return null`() {
            repository.getByPseudo("pseudo").shouldBeNull()
        }

        @Test
        fun `should return user`() {
            repository.getByPseudo(entity.pseudo) shouldBe entity
        }
    }

    @Nested
    inner class save {
        private lateinit var user: User

        @BeforeEach
        fun beforeEach() {
            val index = entities.size + 1
            user = User(
                pseudo = "user_$index",
                email = "user$index@ivanachess.loc",
                bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
            )
        }

        @Test
        fun `should create new user`() {
            val user = repository.save(user).atUtc()
            repository.getById(user.id) shouldBe user
        }
    }

    override fun create(index: Int) = (index + 1).let { number ->
        repository.save(
            user = User(
                pseudo = "user_$number",
                email = "user$number@ivanachess.loc",
                bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
            )
        )
    }

    override fun User.atUtc() = copy(creationDate = creationDate.withOffsetSameInstant(ZoneOffset.UTC))
}
