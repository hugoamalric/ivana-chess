@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.user

import dev.gleroy.ivanachess.api.AbstractSearchableEntityServiceTest
import dev.gleroy.ivanachess.api.EntityNotFoundException
import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class DefaultUserServiceTest :
    AbstractSearchableEntityServiceTest<User, UserRepository, DefaultUserService>() {

    @Nested
    inner class create {
        private val user = createEntity()

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
    inner class existsWithEmail : existsWith<String>() {
        override val value get() = "user@ivanachess.loc"

        override fun existsWithFromRepository(value: String, excluding: Set<UUID>) =
            repository.existsWithEmail(value, excluding)

        override fun existsWith(value: String, excluding: Set<UUID>) = service.existsWithEmail(value, excluding)
    }

    @Nested
    inner class existsWithPseudo : existsWith<String>() {
        override val value get() = "user1"

        override fun existsWithFromRepository(value: String, excluding: Set<UUID>) =
            repository.existsWithPseudo(value, excluding)

        override fun existsWith(value: String, excluding: Set<UUID>) = service.existsWithPseudo(value, excluding)
    }

    @Nested
    inner class getByEmail : getBy<String>() {
        override fun buildErrorMessage(value: String) = "User with email '$value' does not exist"

        override fun fetchBy(value: String) = repository.fetchByEmail(value)

        override fun getBy(value: String) = service.getByEmail(value)

        override fun valueFromEntity(entity: User) = entity.email
    }

    @Nested
    inner class getByPseudo : getBy<String>() {
        override fun buildErrorMessage(value: String) = "User with pseudo '$value' does not exist"

        override fun fetchBy(value: String) = repository.fetchByPseudo(value)

        override fun getBy(value: String) = service.getByPseudo(value)

        override fun valueFromEntity(entity: User) = entity.pseudo
    }

    @Nested
    inner class search : AbstractSearchableEntityServiceTest<User, UserRepository, DefaultUserService>.search() {
        override val fields get() = UserSearchableField.values().toSet()
    }

    @Nested
    inner class update {
        private val user = createEntity()
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
        }

        @Test
        fun `should throw exception if user does not exist`() {
            every { repository.existsWithEmail(user.email, setOf(user.id)) } returns false
            every { repository.fetchById(user.id) } returns null
            val exception = assertThrows<EntityNotFoundException> {
                service.update(user.id, user.email, user.bcryptPassword, user.role)
            }
            exception shouldHaveMessage "Entity ${user.id} does not exist"
            verify { repository.existsWithEmail(user.email, setOf(user.id)) }
            verify { repository.fetchById(user.id) }
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
        }
    }

    override fun createEntity() = User(
        pseudo = "admin",
        email = "admin@ivanachess.loc",
        bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
    )

    override fun createService() = DefaultUserService(repository)

    override fun mockRepository() = mockk<UserRepository>()
}
