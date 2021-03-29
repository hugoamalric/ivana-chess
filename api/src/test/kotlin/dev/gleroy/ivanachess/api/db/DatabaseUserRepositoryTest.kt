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
import org.springframework.test.context.ActiveProfiles
import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
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
                        creationDate = OffsetDateTime.now(Clock.systemUTC()),
                        bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
                    )
                )
            }
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
        fun `should return false if user is excluded`() {
            repository.existsByEmail(user.email, setOf(user.id)).shouldBeFalse()
        }

        @Test
        fun `should return true`() {
            repository.existsByEmail(user.email).shouldBeTrue()
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
                content = users.subList(0, size),
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
                content = users.subList(users.size - 1, users.size),
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
                creationDate = OffsetDateTime.now(Clock.systemUTC()),
                bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
            )
        }

        @Test
        fun `should create new user`() {
            val user = repository.save(user)
            repository.getById(user.id) shouldBe user
        }
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
            repository.searchByPseudo("UsEr_50", 10) shouldBe listOf(users[50])
        }

        @Test
        fun `should return 2 first match`() {
            repository.searchByPseudo("UsEr_1", 2) shouldBe listOf(users[1], users[10])
        }

        @Test
        fun `should return 10 first match`() {
            repository.searchByPseudo("UsEr", 10) shouldBe users
                .sortedBy { it.pseudo }
                .subList(0, 10)
        }

        @Test
        fun `should return 10 first match excluding user_0 and user_1`() {
            repository.searchByPseudo("UsEr", 10, setOf(users[0].id, users[1].id)) shouldBe users
                .sortedBy { it.pseudo }
                .subList(2, 12)
        }
    }
}
