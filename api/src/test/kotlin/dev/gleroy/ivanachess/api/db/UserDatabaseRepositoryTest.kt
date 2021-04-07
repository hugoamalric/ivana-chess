@file:Suppress("ClassName", "SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.CommonSortableEntityField
import dev.gleroy.ivanachess.api.EntitySort
import dev.gleroy.ivanachess.api.user.User
import dev.gleroy.ivanachess.api.user.UserSearchableField
import dev.gleroy.ivanachess.api.user.UserSortableField
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
internal class UserDatabaseRepositoryTest :
    AbstractDatabaseSearchableEntityRepositoryTest<User, UserDatabaseRepository>() {

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
    inner class fetchPage : AbstractDatabaseEntityRepositoryTest<User, UserDatabaseRepository>.fetchPage() {
        @Test
        fun `should return page sorted by email (ascending)`() {
            shouldReturnPageSortedByEmail()
        }

        @Test
        fun `should return page sorted by email (descending)`() {
            shouldReturnPageSortedByEmail(EntitySort.Order.Descending)
        }

        @Test
        fun `should return page sorted by pseudo (ascending)`() {
            shouldReturnPageSortedByPseudo()
        }

        @Test
        fun `should return page sorted by pseudo (descending)`() {
            shouldReturnPageSortedByPseudo(EntitySort.Order.Descending)
        }

        private fun shouldReturnPageSortedByEmail(order: EntitySort.Order = EntitySort.Order.Ascending) {
            shouldReturnPage(
                field = UserSortableField.Email,
                sortedEntities = entities.sortedBy { it.email },
                order = order,
            )
        }

        private fun shouldReturnPageSortedByPseudo(order: EntitySort.Order = EntitySort.Order.Ascending) {
            shouldReturnPage(
                field = UserSortableField.Pseudo,
                sortedEntities = entities.sortedBy { it.pseudo },
                order = order,
            )
        }
    }

    @Nested
    inner class search : AbstractDatabaseSearchableEntityRepositoryTest<User, UserDatabaseRepository>.search() {
        @Test
        fun `should return page of entities which pseudo matches UsEr1 sorted by pseudo (ascending)`() {
            shouldReturnPageOfEntitiesWhichPseudoMatchesSearchSortedByPseudo("UsEr1")
        }

        @Test
        fun `should return page of entities which pseudo matches UsEr1 sorted by pseudo (descending)`() {
            shouldReturnPageOfEntitiesWhichPseudoMatchesSearchSortedByPseudo("UsEr1", EntitySort.Order.Descending)
        }

        @Test
        fun `should return page of entities which email matches UsEr1 sorted by email (ascending)`() {
            shouldReturnPageOfEntitiesWhichEmailMatchesSearchSortedByEmail("UsEr1")
        }

        @Test
        fun `should return page of entities which email matches UsEr1 sorted by email (descending)`() {
            shouldReturnPageOfEntitiesWhichEmailMatchesSearchSortedByEmail("UsEr1", EntitySort.Order.Descending)
        }

        @Test
        fun `should return page of entities which pseudo or email match IvAnAcHeSs sorted by pseudo and email (ascending)`() {
            val term = "IvAnAcHeSs"
            shouldReturnPageOfEntitiesWhichMatchSearch(
                term = "IvAnAcHeSs",
                fields = setOf(UserSearchableField.Pseudo, UserSearchableField.Email),
                sorts = listOf(EntitySort(UserSortableField.Pseudo), EntitySort(UserSortableField.Email)),
                sortedEntities = entities.sortedWith { user1, user2 ->
                    val result = user1.pseudo.compareTo(user2.pseudo)
                    if (result == 0) {
                        user1.email.compareTo(user2.email)
                    } else {
                        result
                    }
                },
            ) { it.pseudo.contains(term, true) || it.email.contains(term, true) }
        }

        @Test
        fun `should return page of entities which pseudo or email match IvAnAcHeSs excluding user0 and user1 sorted by creation date and ID (descending)`() {
            val term = "IvAnAcHeSs"
            val excludedEntities = setOf(entities[0], entities[1])
            shouldReturnPageOfEntitiesWhichMatchSearch(
                term = "IvAnAcHeSs",
                fields = setOf(UserSearchableField.Pseudo, UserSearchableField.Email),
                sorts = listOf(
                    EntitySort(CommonSortableEntityField.CreationDate, EntitySort.Order.Descending),
                    EntitySort(CommonSortableEntityField.Id, EntitySort.Order.Descending),
                ),
                sortedEntities = entities.sortedWith { user1, user2 ->
                    val result = user2.creationDate.compareTo(user1.creationDate)
                    if (result == 0) {
                        user2.id.toString().compareTo(user1.id.toString())
                    } else {
                        result
                    }
                },
                excluding = excludedEntities.map { it.id }.toSet(),
            ) { (it.pseudo.contains(term, true) || it.email.contains(term, true)) && !excludedEntities.contains(it) }
        }

        private fun shouldReturnPageOfEntitiesWhichPseudoMatchesSearchSortedByPseudo(
            term: String,
            order: EntitySort.Order = EntitySort.Order.Ascending
        ) {
            shouldReturnPageOfEntitiesWhichMatchSearch(
                term = term,
                searchableField = UserSearchableField.Pseudo,
                sortableField = UserSortableField.Pseudo,
                sortedEntities = entities.sortedBy { it.pseudo },
                order = order,
            ) { it.pseudo.contains(term, true) }
        }

        private fun shouldReturnPageOfEntitiesWhichEmailMatchesSearchSortedByEmail(
            term: String,
            order: EntitySort.Order = EntitySort.Order.Ascending
        ) {
            shouldReturnPageOfEntitiesWhichMatchSearch(
                term = term,
                searchableField = UserSearchableField.Email,
                sortableField = UserSortableField.Email,
                sortedEntities = entities.sortedBy { it.email },
                order = order,
            ) { it.email.contains(term, true) }
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
