@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import dev.gleroy.ivanachess.api.Properties
import dev.gleroy.ivanachess.api.User
import dev.gleroy.ivanachess.api.UserRepository
import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.*

internal class DefaultAuthenticationServiceTest {
    private val props = Properties()
    private val user = User(
        pseudo = "admin",
        bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
    )
    private val now = Instant.now()
    private val zone = Clock.systemDefaultZone().zone

    private lateinit var repository: UserRepository
    private lateinit var clock: Clock

    private lateinit var service: DefaultAuthenticationService

    @BeforeEach
    fun beforeEach() {
        repository = mockk()
        clock = mockk()
        service = DefaultAuthenticationService(
            repository = repository,
            clock = clock,
            props = props
        )
    }

    @Nested
    inner class generateJwt {
        private val password = "admin"

        @Test
        fun `should throw exception if user does not exist`() {
            every { repository.getByPseudo(user.pseudo) } returns null
            val exception = assertThrows<BadCredentialsException> { service.generateJwt(user.pseudo, password) }
            exception shouldHaveMessage "User '${user.pseudo}' does not exist"
            verify { repository.getByPseudo(user.pseudo) }
            confirmVerified(repository)
        }

        @Test
        fun `should throw exception if password does not match`() {
            every { repository.getByPseudo(user.pseudo) } returns user
            val exception = assertThrows<BadCredentialsException> { service.generateJwt(user.pseudo, "changeit") }
            exception shouldHaveMessage "Wrong password for user '${user.pseudo}'"
            verify { repository.getByPseudo(user.pseudo) }
            confirmVerified(repository)
        }

        @Test
        fun `should return JWT`() {
            every { repository.getByPseudo(user.pseudo) } returns user
            every { clock.instant() } returns now
            every { clock.zone } returns zone
            val jwt = service.generateJwt(user.pseudo, password)
            jwt.expirationDate shouldBe OffsetDateTime.ofInstant(now, zone).plusSeconds(props.auth.validity.toLong())
            verify { repository.getByPseudo(user.pseudo) }
            verify { clock.instant() }
            verify { clock.zone }
            confirmVerified(repository, clock)
        }
    }

    @Nested
    inner class loadUserByUsername {
        @Test
        fun `should throw exception if user does not exist`() {
            every { repository.getByPseudo(user.pseudo) } returns null
            val exception = assertThrows<UsernameNotFoundException> { service.loadUserByUsername(user.pseudo) }
            exception shouldHaveMessage "User '${user.pseudo}' does not exist"
            verify { repository.getByPseudo(user.pseudo) }
            confirmVerified(repository)
        }

        @Test
        fun `should return user principal`() {
            every { repository.getByPseudo(user.pseudo) } returns user
            service.loadUserByUsername(user.pseudo) shouldBe UserDetailsAdapter(user)
            verify { repository.getByPseudo(user.pseudo) }
            confirmVerified(repository)
        }
    }

    @Nested
    inner class parseJwt {
        @Test
        fun `should throw exception if token is not JWT`() {
            val token = "token"
            val exception = assertThrows<BadJwtException> { service.parseJwt(token) }
            exception shouldHaveMessage "Unable to decode JWT ('$token')"
        }

        @Test
        fun `should throw exception if token is corrupted`() {
            val token = JWT.create()
                .withSubject(user.pseudo)
                .withExpiresAt(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .sign(Algorithm.HMAC512("secret"))
            val exception = assertThrows<BadJwtException> { service.parseJwt(token) }
            exception shouldHaveMessage "Unable to verify JWT ('$token')"
        }

        @Test
        fun `should throw exception if token is expired`() {
            val token = JWT.create()
                .withSubject(user.pseudo)
                .withExpiresAt(Date.from(now.minus(1, ChronoUnit.HOURS)))
                .sign(Algorithm.HMAC512(props.auth.secret))
            val exception = assertThrows<BadJwtException> { service.parseJwt(token) }
            exception shouldHaveMessage "Unable to verify JWT ('$token')"
        }

        @Test
        fun `should return JWT`() {
            val expirationDate = OffsetDateTime.ofInstant(Instant.now().plus(1, ChronoUnit.HOURS), zone)
                .truncatedTo(ChronoUnit.SECONDS)
            val token = JWT.create()
                .withSubject(user.pseudo)
                .withExpiresAt(Date.from(expirationDate.toInstant()))
                .sign(Algorithm.HMAC512(props.auth.secret))
            every { clock.zone } returns zone
            service.parseJwt(token) shouldBe Jwt(
                pseudo = user.pseudo,
                expirationDate = expirationDate,
                token = token
            )
            verify { clock.zone }
            confirmVerified(clock)
        }
    }
}
