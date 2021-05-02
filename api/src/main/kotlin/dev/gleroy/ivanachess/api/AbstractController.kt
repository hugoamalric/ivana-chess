package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.api.security.UserDetailsAdapter
import org.springframework.security.core.Authentication
import javax.servlet.http.Cookie

/**
 * Abstract implementation of controller.
 */
abstract class AbstractController {
    /**
     * Properties.
     */
    protected abstract val props: Properties

    /**
     * Get authenticated user from authentication.
     *
     * @param auth Authentication.
     * @return Authenticated user.
     */
    protected fun authenticatedUser(auth: Authentication) = (auth.principal as UserDetailsAdapter).user

    /**
     * Create authentication cookie.
     *
     * @param token Token.
     * @param maxAge Number of seconds for which the cookie is valid.
     * @return Cookie.
     */
    protected fun createAuthenticationCookie(token: String, maxAge: Int) =
        Cookie(props.auth.cookie.name, token).apply {
            domain = props.auth.cookie.domain
            secure = props.auth.cookie.secure
            isHttpOnly = props.auth.cookie.httpOnly
            this.maxAge = maxAge
        }

    /**
     * Expire authentication cookie.
     *
     * @return Cookie.
     */
    protected fun expireAuthenticationCookie() = createAuthenticationCookie("", 0)
}
