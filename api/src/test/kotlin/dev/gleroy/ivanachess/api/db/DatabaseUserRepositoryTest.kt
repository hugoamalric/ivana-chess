@file:Suppress("ClassName", "SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.Page
import dev.gleroy.ivanachess.api.user.User
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.shouldBe
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.time.ZoneOffset
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class DatabaseUserRepositoryTest {
    @Autowired
    private lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    @Autowired
    private lateinit var repository: DatabaseUserRepository

    private lateinit var users: List<User>
    private lateinit var user: User

    @BeforeEach
    fun beforeEach() {
        users = (0 until 100)
            .map { i ->
                repository.save(
                    user = User(
                        pseudo = "user_$i",
                        email = "user$i@ivanachess.loc",
                        bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
                    )
                ).atUtc()
            }
            .reversed()
        user = users.first()
    }

    @Suppress("SqlWithoutWhere", "SqlResolve")
    @AfterEach
    fun afterEach() {
        jdbcTemplate.update(
            "DELETE FROM \"${DatabaseConstants.User.TableName}\"",
            ComparableMapSqlParameterSource()
        )
    }

    @Nested
    inner class existsByEmail {
        @Test
        fun `should return false`() {
            repository.existsByEmail("email").shouldBeFalse()
        }

        @Test
        fun `should return true`() {
            repository.existsByEmail(user.email).shouldBeTrue()
        }
    }

    @Nested
    inner class `existsByEmail ignoring one user` {
        @Test
        fun `should return false if user does not exist`() {
            repository.existsByEmail("email", user.id).shouldBeFalse()
        }

        @Test
        fun `should return false if user is ignoring one`() {
            repository.existsByEmail(user.email, user.id).shouldBeFalse()
        }

        @Test
        fun `should return true`() {
            repository.existsByEmail(users[1].email, user.id).shouldBeTrue()
        }
    }

    @Nested
    inner class existsById {
        @Test
        fun `should return false`() {
            repository.existsById(UUID.randomUUID()).shouldBeFalse()
        }

        @Test
        fun `should return true`() {
            repository.existsById(user.id).shouldBeTrue()
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
            repository.existsByPseudo(user.pseudo).shouldBeTrue()
        }
    }

    @Nested
    inner class getAll {
        @Test
        fun `should throw exception if page is 0`() {
            val exception = assertThrows<IllegalArgumentException> { repository.getAll(0, 1) }
            exception shouldHaveMessage "page must be strictly positive"
        }

        @Test
        fun `should throw exception if size is 0`() {
            val exception = assertThrows<IllegalArgumentException> { repository.getAll(1, 0) }
            exception shouldHaveMessage "size must be strictly positive"
        }

        @Test
        fun `should return first page`() {
            val page = 1
            val size = 3
            repository.getAll(page, size) shouldBe Page(
                content = users.subList(users.size - size, users.size).reversed(),
                number = page,
                totalItems = users.size,
                totalPages = 34
            )
        }

        @Test
        fun `should return last page`() {
            val page = 34
            val size = 3
            repository.getAll(page, size) shouldBe Page(
                content = users.subList(0, 1),
                number = page,
                totalItems = users.size,
                totalPages = 34
            )
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
            repository.getByEmail(user.email) shouldBe user
        }
    }

    @Nested
    inner class getById {
        @Test
        fun `should return null`() {
            repository.getById(UUID.randomUUID()).shouldBeNull()
        }

        @Test
        fun `should return user`() {
            repository.getById(user.id) shouldBe user
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
            repository.getByPseudo(user.pseudo) shouldBe user
        }
    }

    @Nested
    inner class save {
        private lateinit var user: User

        @BeforeEach
        fun beforeEach() {
            val index = users.size + 1
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

    private fun User.atUtc() = copy(creationDate = creationDate.withOffsetSameInstant(ZoneOffset.UTC))
}
