@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.*
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

internal abstract class AbstractEntityDatabaseRepositoryTest<E : Entity, R : AbstractEntityDatabaseRepository<E>> :
    AbstractDatabaseRepositoryTest<UUID, E, R>() {
    protected lateinit var users: List<User>
    protected lateinit var games: List<GameEntity>

    @Autowired
    protected lateinit var userRepository: UserDatabaseRepository

    @Autowired
    protected lateinit var gameRepository: GameDatabaseRepository

    @BeforeEach
    open fun beforeEach() {
        clean()
        users = (0 until 100).map { index ->
            userRepository.save(
                entity = User(
                    pseudo = "user$index",
                    email = "user$index@ivanachess.loc",
                    creationDate = OffsetDateTime.now(Clock.systemUTC()),
                    bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS",
                )
            )
        }
        games = (0 until 50).map { index ->
            gameRepository.save(
                entity = GameEntity(
                    creationDate = OffsetDateTime.now(Clock.systemUTC()),
                    whitePlayer = users[index],
                    blackPlayer = users[users.size - index - 1],
                )
            )
        }
    }

    @Nested
    inner class delete {
        @Test
        fun `should return false if entity does not exist`() {
            repository.delete(UUID.randomUUID()).shouldBeFalse()
        }

        @Test
        fun `should return true if entity is deleted`() {
            repository.delete(items[0].id).shouldBeTrue()
        }
    }

    @Nested
    inner class existsWithId : AbstractDatabaseRepositoryTest<UUID, E, R>.existsWithId() {
        override val wrongValue: UUID = UUID.randomUUID()
    }

    @Nested
    inner class fetchById : AbstractDatabaseRepositoryTest<UUID, E, R>.fetchById() {
        override val wrongValue: UUID = UUID.randomUUID()
    }

    @Nested
    open inner class fetchPage : AbstractDatabaseRepositoryTest<UUID, E, R>.fetchPage() {
        @Test
        fun `should return page sorted by creation date and ID`() {
            shouldReturnPage(
                sorts = listOf(
                    ItemSort(CommonEntityField.CreationDate),
                    ItemSort(CommonEntityField.Id),
                ),
                sortedItems = items.sortedWith { entity1, entity2 ->
                    val result = entity1.creationDate.compareTo(entity2.creationDate)
                    if (result == 0) {
                        entity1.id.toString().compareTo(entity2.id.toString())
                    } else {
                        result
                    }
                }
            )
        }

        @Test
        fun `should return page sorted by ID (ascending)`() {
            shouldReturnPageSortedById()
        }

        @Test
        fun `should return page sorted by ID (descending)`() {
            shouldReturnPageSortedById(ItemSort.Order.Descending)
        }

        @Test
        fun `should return page sorted by creation date (ascending)`() {
            shouldReturnPageSortedByCreationDate()
        }

        @Test
        fun `should return page sorted by creation date (descending)`() {
            shouldReturnPageSortedByCreationDate(ItemSort.Order.Descending)
        }

        private fun shouldReturnPageSortedByCreationDate(order: ItemSort.Order = ItemSort.Order.Ascending) {
            shouldReturnPage(
                sortedField = CommonEntityField.CreationDate,
                sortedItems = items.sortedBy { it.creationDate },
                order = order,
            )
        }

        private fun shouldReturnPageSortedById(order: ItemSort.Order = ItemSort.Order.Ascending) {
            shouldReturnPage(
                sortedField = CommonEntityField.Id,
                sortedItems = items.sortedBy { it.id.toString() },
                order = order,
            )
        }
    }

    @Suppress("SqlWithoutWhere", "SqlResolve")
    protected fun clean() {
        jdbcTemplate.update(
            "DELETE FROM \"${DatabaseConstants.Game.TableName}\"",
            emptyMap<String, Any>()
        )
        jdbcTemplate.update(
            "DELETE FROM \"${DatabaseConstants.User.TableName}\"",
            emptyMap<String, Any>()
        )
    }

    protected abstract fun updateEntity(entity: E): E
}
