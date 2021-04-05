package dev.gleroy.ivanachess.api.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import dev.gleroy.ivanachess.api.Properties
import dev.gleroy.ivanachess.api.user.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

/**
 * Default implementation of authentication service.
 *
 * @param repository User repository.
 * @param props Properties.
 * @param clock Clock.
 */
@Service
class DefaultAuthenticationService(
    private val repository: UserRepository,
    private val clock: Clock,
    private val props: Properties
) : AuthenticationService {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(DefaultAuthenticationService::class.java)
    }

    override fun generateJwt(pseudo: String, password: String): Jwt {
        val user = repository.fetchByPseudo(pseudo) ?: throw BadCredentialsException("User '$pseudo' does not exist")
        if (!BCrypt.checkpw(password, user.bcryptPassword)) {
            throw BadCredentialsException("Wrong password for user '$pseudo' (${user.id})")
        }
        val expirationDate = OffsetDateTime.now(clock).plusSeconds(props.auth.validity.toLong())
        val token = JWT.create()
            .withSubject(pseudo)
            .withExpiresAt(Date.from(expirationDate.toInstant()))
            .sign(Algorithm.HMAC512(props.auth.secret))
        Logger.info("JWT generated for user '$pseudo' (${user.id})")
        return Jwt(
            pseudo = pseudo,
            expirationDate = expirationDate,
            token = token
        )
    }

    override fun loadUserByUsername(username: String) =
        repository.fetchByPseudo(username)?.let { UserDetailsAdapter(it) }
            ?: throw UsernameNotFoundException("User '$username' does not exist")

    override fun parseJwt(token: String) = try {
        val jwt = JWT.require(Algorithm.HMAC512(props.auth.secret))
            .build()
            .verify(token)
        Jwt(
            pseudo = jwt.subject,
            expirationDate = OffsetDateTime.ofInstant(jwt.expiresAt.toInstant(), clock.zone),
            token = token
        )
    } catch (exception: JWTDecodeException) {
        throw BadJwtException("Unable to decode JWT ('$token')", exception).apply {
            Logger.debug(message, exception)
        }
    } catch (exception: JWTVerificationException) {
        throw BadJwtException("Unable to verify JWT ('$token')", exception).apply {
            Logger.debug(message, exception)
        }
    }
}
