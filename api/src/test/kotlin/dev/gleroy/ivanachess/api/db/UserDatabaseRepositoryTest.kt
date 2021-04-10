@file:Suppress("ClassName", "SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.CommonEntityField
import dev.gleroy.ivanachess.core.ItemSort
import dev.gleroy.ivanachess.core.User
import dev.gleroy.ivanachess.core.UserField
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

@SpringBootTest
@ActiveProfiles("dev")
internal class UserDatabaseRepositoryTest :
    AbstractSearchableEntityDatabaseRepositoryTest<User, UserDatabaseRepository>() {

    @Nested
    inner class existsWithEmail : existsWith<String>() {
        override val wrongValue get() = "noreply@ivanachess.loc"

        override fun existsWith(value: String, excluding: Set<UUID>) = repository.existsWithEmail(value, excluding)

        override fun valueFromItem(item: User) = item.email
    }

    @Nested
    inner class existsByPseudo : existsWith<String>() {
        override val wrongValue get() = "pseudo"

        override fun existsWith(value: String, excluding: Set<UUID>) = repository.existsWithPseudo(value, excluding)

        override fun valueFromItem(item: User) = item.pseudo
    }

    @Nested
    inner class fetchByEmail : fetchBy<String>() {
        override val wrongValue get() = "noreply@ivanachess.loc"

        override fun fetchBy(value: String) = repository.fetchByEmail(value)

        override fun valueFromItem(item: User) = item.email
    }

    @Nested
    inner class fetchByPseudo : fetchBy<String>() {
        override val wrongValue get() = "pseudo"

        override fun fetchBy(value: String) = repository.fetchByPseudo(value)

        override fun valueFromItem(item: User) = item.pseudo
    }

    @Nested
    inner class fetchPage : AbstractEntityDatabaseRepositoryTest<User, UserDatabaseRepository>.fetchPage() {
        @Test
        fun `should return page sorted by email (ascending)`() {
            shouldReturnPageSortedByEmail()
        }

        @Test
        fun `should return page sorted by email (descending)`() {
            shouldReturnPageSortedByEmail(ItemSort.Order.Descending)
        }

        @Test
        fun `should return page sorted by pseudo (ascending)`() {
            shouldReturnPageSortedByPseudo()
        }

        @Test
        fun `should return page sorted by pseudo (descending)`() {
            shouldReturnPageSortedByPseudo(ItemSort.Order.Descending)
        }

        private fun shouldReturnPageSortedByEmail(order: ItemSort.Order = ItemSort.Order.Ascending) {
            shouldReturnPage(
                field = UserField.Email,
                sortedItems = items.sortedBy { it.email },
                order = order,
            )
        }

        private fun shouldReturnPageSortedByPseudo(order: ItemSort.Order = ItemSort.Order.Ascending) {
            shouldReturnPage(
                field = UserField.Pseudo,
                sortedItems = items.sortedBy { it.pseudo },
                order = order,
            )
        }
    }

    @Nested
    inner class search : AbstractSearchableEntityDatabaseRepositoryTest<User, UserDatabaseRepository>.search() {
        @Test
        fun `should return page of entities which pseudo matches UsEr1 sorted by pseudo (ascending)`() {
            shouldReturnPageOfEntitiesWhichPseudoMatchesSearchSortedByPseudo("UsEr1")
        }

        @Test
        fun `should return page of entities which pseudo matches UsEr1 sorted by pseudo (descending)`() {
            shouldReturnPageOfEntitiesWhichPseudoMatchesSearchSortedByPseudo("UsEr1", ItemSort.Order.Descending)
        }

        @Test
        fun `should return page of entities which email matches UsEr1 sorted by email (ascending)`() {
            shouldReturnPageOfEntitiesWhichEmailMatchesSearchSortedByEmail("UsEr1")
        }

        @Test
        fun `should return page of entities which email matches UsEr1 sorted by email (descending)`() {
            shouldReturnPageOfEntitiesWhichEmailMatchesSearchSortedByEmail("UsEr1", ItemSort.Order.Descending)
        }

        @Test
        fun `should return page of entities which pseudo or email match IvAnAcHeSs sorted by pseudo and email (ascending)`() {
            val term = "IvAnAcHeSs"
            shouldReturnPageOfEntitiesWhichMatchSearch(
                term = "IvAnAcHeSs",
                fields = setOf(UserField.Pseudo, UserField.Email),
                sorts = listOf(ItemSort(UserField.Pseudo), ItemSort(UserField.Email)),
                sortedEntities = items.sortedWith { user1, user2 ->
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
            val excludedEntities = setOf(items[0], items[1])
            shouldReturnPageOfEntitiesWhichMatchSearch(
                term = "IvAnAcHeSs",
                fields = setOf(UserField.Pseudo, UserField.Email),
                sorts = listOf(
                    ItemSort(CommonEntityField.CreationDate, ItemSort.Order.Descending),
                    ItemSort(CommonEntityField.Id, ItemSort.Order.Descending),
                ),
                sortedEntities = items.sortedWith { user1, user2 ->
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
            order: ItemSort.Order = ItemSort.Order.Ascending
        ) {
            shouldReturnPageOfEntitiesWhichMatchSearch(
                term = term,
                searchableField = UserField.Pseudo,
                sortableField = UserField.Pseudo,
                sortedEntities = items.sortedBy { it.pseudo },
                order = order,
            ) { it.pseudo.contains(term, true) }
        }

        private fun shouldReturnPageOfEntitiesWhichEmailMatchesSearchSortedByEmail(
            term: String,
            order: ItemSort.Order = ItemSort.Order.Ascending
        ) {
            shouldReturnPageOfEntitiesWhichMatchSearch(
                term = term,
                searchableField = UserField.Email,
                sortableField = UserField.Email,
                sortedEntities = items.sortedBy { it.email },
                order = order,
            ) { it.email.contains(term, true) }
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
