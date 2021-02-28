@file:Suppress("ClassName", "SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.User
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
            user = User(
                pseudo = "user_${entities.size + 1}",
                bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
            )
        }

        @Test
        fun `should create new user`() {
            val user = repository.save(user).atUtc()
            repository.getById(user.id) shouldBe user
        }
    }

    override fun create(index: Int) = repository.save(
        user = User(
            pseudo = "user_${index + 1}",
            bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
        )
    )

    override fun User.atUtc() = copy(creationDate = creationDate.withOffsetSameInstant(ZoneOffset.UTC))
}
