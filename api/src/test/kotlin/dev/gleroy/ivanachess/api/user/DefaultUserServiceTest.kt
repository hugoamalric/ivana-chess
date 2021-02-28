@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.user

import dev.gleroy.ivanachess.api.Page
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
            every { repository.existsByPseudo(user.pseudo) } returns true
            val exception = assertThrows<UserPseudoAlreadyUsedException> {
                service.create(user.pseudo, user.bcryptPassword, user.role)
            }
            exception shouldBe UserPseudoAlreadyUsedException(user.pseudo)
            verify { repository.existsByPseudo(user.pseudo) }
            confirmVerified(repository)
        }

        @Test
        fun `should create new user`() {
            every { repository.existsByPseudo(user.pseudo) } returns false
            every { repository.save(any()) } returns user
            service.create(user.pseudo, user.bcryptPassword, user.role) shouldBe user
            verify { repository.existsByPseudo(user.pseudo) }
            verify { repository.save(any()) }
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
            every { repository.getAll(pageNb, pageSize) } returns page

            service.getAll(pageNb, pageSize) shouldBe page

            verify { repository.getAll(pageNb, pageSize) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class getById {
        @Test
        fun `should throw exception if user does not exist`() {
            val id = UUID.randomUUID()
            every { repository.getById(id) } returns null
            val exception = assertThrows<UserIdNotFoundException> { service.getById(id) }
            exception shouldBe UserIdNotFoundException(id)
            verify { repository.getById(id) }
            confirmVerified(repository)
        }

        @Test
        fun `should return user`() {
            every { repository.getById(user.id) } returns user
            service.getById(user.id) shouldBe user
            verify { repository.getById(user.id) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class getByPseudo {
        @Test
        fun `should throw exception if user does not exist`() {
            every { repository.getByPseudo(user.pseudo) } returns null
            val exception = assertThrows<UserPseudoNotFoundException> { service.getByPseudo(user.pseudo) }
            exception shouldBe UserPseudoNotFoundException(user.pseudo)
            verify { repository.getByPseudo(user.pseudo) }
            confirmVerified(repository)
        }

        @Test
        fun `should return user`() {
            every { repository.getByPseudo(user.pseudo) } returns user
            service.getByPseudo(user.pseudo) shouldBe user
            verify { repository.getByPseudo(user.pseudo) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class updatePassword {
        private val updatedUser = user.copy(
            bcryptPassword = "\$2y\$12\$tuJ1.m7TxmJqG0F2IEBX1.YNO.khekCJdNopLFLKofOuy70Rh8JE6",
            role = User.Role.SuperAdmin
        )

        @Test
        fun `should throw exception if user does not exist`() {
            every { repository.getById(user.id) } returns null
            val exception = assertThrows<UserIdNotFoundException> {
                service.update(user.id, user.bcryptPassword, user.role)
            }
            exception shouldBe UserIdNotFoundException(user.id)
            verify { repository.getById(user.id) }
            confirmVerified(repository)
        }

        @Test
        fun `should return updated user`() {
            every { repository.getById(user.id) } returns user
            every { repository.save(updatedUser) } returns updatedUser
            service.update(updatedUser.id, updatedUser.bcryptPassword, updatedUser.role) shouldBe updatedUser
            verify { repository.getById(user.id) }
            verify { repository.save(updatedUser) }
            confirmVerified(repository)
        }
    }
}
