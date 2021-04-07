@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.user

import dev.gleroy.ivanachess.api.Page
import dev.gleroy.ivanachess.api.PageOptions
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class DefaultUserServiceTest {
    private val user = User(
        pseudo = "admin",
        email = "admin@ivanachess.loc",
        bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
    )

    private lateinit var repository: UserRepository
    private lateinit var service: DefaultUserService

    @BeforeEach
    fun beforeEach() {
        repository = mockk()
        service = DefaultUserService(repository)
    }

    @Nested
    inner class create {
        @Test
        fun `should throw exception if pseudo is already used`() {
            every { repository.existsWithPseudo(user.pseudo) } returns true
            val exception = assertThrows<UserPseudoAlreadyUsedException> {
                service.create(user.pseudo, user.email, user.bcryptPassword, user.role)
            }
            exception shouldBe UserPseudoAlreadyUsedException(user.pseudo)
            verify { repository.existsWithPseudo(user.pseudo) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if email is already used`() {
            every { repository.existsWithPseudo(user.pseudo) } returns false
            every { repository.existsWithEmail(user.email) } returns true
            val exception = assertThrows<UserEmailAlreadyUsedException> {
                service.create(user.pseudo, user.email, user.bcryptPassword, user.role)
            }
            exception shouldBe UserEmailAlreadyUsedException(user.email)
            verify { repository.existsWithPseudo(user.pseudo) }
            verify { repository.existsWithEmail(user.email) }
            confirmVerified(repository)
        }

        @Test
        fun `should create new user`() {
            every { repository.existsWithPseudo(user.pseudo) } returns false
            every { repository.existsWithEmail(user.email) } returns false
            every { repository.save(any()) } returns user
            service.create(user.pseudo, user.email, user.bcryptPassword, user.role) shouldBe user
            verify { repository.existsWithPseudo(user.pseudo) }
            verify { repository.existsWithEmail(user.email) }
            verify { repository.save(any()) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class existsByEmail {
        private val email = "user@ivanachess.loc"

        @Test
        fun `should call repository`() {
            every { repository.existsWithEmail(email) } returns true

            service.existsByEmail(email).shouldBeTrue()

            verify { repository.existsWithEmail(email) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class existsByPseudo {
        private val pseudo = "user"

        @Test
        fun `should call repository`() {
            every { repository.existsWithPseudo(pseudo) } returns true

            service.existsByPseudo(pseudo).shouldBeTrue()

            verify { repository.existsWithPseudo(pseudo) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class getAll {
        private val pageNb = 1
        private val pageSize = 10
        private val page = Page<User>(
            number = pageNb,
            totalItems = 10,
            totalPages = 1
        )

        @Test
        fun `should return page`() {
            every { repository.fetchPage(PageOptions(pageNb, pageSize)) } returns page

            service.getAll(pageNb, pageSize) shouldBe page

            verify { repository.fetchPage(PageOptions(pageNb, pageSize)) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class getById {
        @Test
        fun `should throw exception if user does not exist`() {
            val id = UUID.randomUUID()
            every { repository.fetchById(id) } returns null
            val exception = assertThrows<UserIdNotFoundException> { service.getById(id) }
            exception shouldBe UserIdNotFoundException(id)
            verify { repository.fetchById(id) }
            confirmVerified(repository)
        }

        @Test
        fun `should return user`() {
            every { repository.fetchById(user.id) } returns user
            service.getById(user.id) shouldBe user
            verify { repository.fetchById(user.id) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class getByEmail {
        @Test
        fun `should throw exception if user does not exist`() {
            every { repository.fetchByEmail(user.email) } returns null
            val exception = assertThrows<UserEmailNotFoundException> { service.getByEmail(user.email) }
            exception shouldBe UserEmailNotFoundException(user.email)
            verify { repository.fetchByEmail(user.email) }
            confirmVerified(repository)
        }

        @Test
        fun `should return user`() {
            every { repository.fetchByEmail(user.email) } returns user
            service.getByEmail(user.email) shouldBe user
            verify { repository.fetchByEmail(user.email) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class getByPseudo {
        @Test
        fun `should throw exception if user does not exist`() {
            every { repository.fetchByPseudo(user.pseudo) } returns null
            val exception = assertThrows<UserPseudoNotFoundException> { service.getByPseudo(user.pseudo) }
            exception shouldBe UserPseudoNotFoundException(user.pseudo)
            verify { repository.fetchByPseudo(user.pseudo) }
            confirmVerified(repository)
        }

        @Test
        fun `should return user`() {
            every { repository.fetchByPseudo(user.pseudo) } returns user
            service.getByPseudo(user.pseudo) shouldBe user
            verify { repository.fetchByPseudo(user.pseudo) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class searchByPseudo {
        private val page = Page(
            content = listOf(user),
            number = 1,
            totalPages = 1,
            totalItems = 1,
        )
        private val maxSize = 5
        private val q = "pseudo"

        @Test
        fun `should return list of users`() {
            every { repository.search(q, setOf(UserSearchableField.Pseudo), PageOptions(1, maxSize)) } returns page

            service.searchByPseudo(q, maxSize) shouldBe page.content

            verify { repository.search(q, setOf(UserSearchableField.Pseudo), PageOptions(1, maxSize)) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class update {
        private val updatedUser = user.copy(
            email = "superadmin@ivanachess.loc",
            bcryptPassword = "\$2y\$12\$tuJ1.m7TxmJqG0F2IEBX1.YNO.khekCJdNopLFLKofOuy70Rh8JE6",
            role = User.Role.SuperAdmin
        )

        @Test
        fun `should throw exception if email is already used`() {
            every { repository.existsWithEmail(user.email, setOf(user.id)) } returns true
            val exception = assertThrows<UserEmailAlreadyUsedException> {
                service.update(user.id, user.email, user.bcryptPassword, user.role)
            }
            exception shouldBe UserEmailAlreadyUsedException(user.email)
            verify { repository.existsWithEmail(user.email, setOf(user.id)) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if user does not exist`() {
            every { repository.existsWithEmail(user.email, setOf(user.id)) } returns false
            every { repository.fetchById(user.id) } returns null
            val exception = assertThrows<UserIdNotFoundException> {
                service.update(user.id, user.email, user.bcryptPassword, user.role)
            }
            exception shouldBe UserIdNotFoundException(user.id)
            verify { repository.existsWithEmail(user.email, setOf(user.id)) }
            verify { repository.fetchById(user.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should return updated user`() {
            every { repository.existsWithEmail(updatedUser.email, setOf(user.id)) } returns false
            every { repository.fetchById(user.id) } returns user
            every { repository.save(updatedUser) } returns updatedUser
            service.update(
                id = updatedUser.id,
                email = updatedUser.email,
                bcryptPassword = updatedUser.bcryptPassword,
                role = updatedUser.role
            ) shouldBe updatedUser
            verify { repository.existsWithEmail(updatedUser.email, setOf(user.id)) }
            verify { repository.fetchById(user.id) }
            verify { repository.save(updatedUser) }
            confirmVerified(repository)
        }
    }
}
