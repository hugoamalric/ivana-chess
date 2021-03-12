package dev.gleroy.ivanachess.api

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.http.HttpHeaders
import java.net.InetAddress

/**
 * Properties.
 *
 * @param db Database properties.
 * @param broker Broker properties.
 * @param server Server properties.
 * @param auth Authentication properties.
 */
@ConfigurationProperties(prefix = "ivana-chess")
@ConstructorBinding
data class Properties(
    val server: Server = Server(),
    val db: Database = Database(),
    val broker: Broker = Broker(),
    val auth: Authentication = Authentication()
) {
    /**
     * Broker properties.
     *
     * @param host Host.
     * @param port Port.
     * @param username Username.
     * @param password Password.
     */
    data class Broker(
        val host: InetAddress = InetAddress.getLoopbackAddress(),
        val port: Int = 61613,
        val username: String = "guest",
        val password: String = "guest"
    )

    /**
     * Database properties.
     *
     * @param url JDBC URL of database.
     * @param username Username used to connect to database.
     * @param password Password used to connect to database.
     */
    data class Database(
        val url: String = "jdbc:postgresql://127.0.0.1:5432/ivana_chess_api?currentSchema=public",
        val username: String = "ivanachessapi",
        val password: String = "ivanachessapi"
    )

    /**
     * Server properties.
     *
     * @param bindAddress Bind address.
     * @param port Port.
     * @param contextPath Context path.
     * @param allowedOrigins Coma-separated list of allowed origins
     */
    data class Server(
        val bindAddress: InetAddress = InetAddress.getByName("0.0.0.0"),
        val port: Int = 8080,
        val contextPath: String = "",
        val allowedOrigins: String = "localhost:4200"
    )

    /**
     * Authentication properties.
     *
     * @param secret Secret used to generate JWT.
     * @param validity Number of seconds for which the JWT is valid.
     * @param header Authentication header properties.
     * @param cookie Authentication cookie properties.
     */
    data class Authentication(
        val secret: String = "changeit",
        val validity: Int = 7 * 24 * 60 * 60,
        val header: Header = Header(),
        val cookie: Cookie = Cookie()
    ) {
        /**
         * Authentication header properties.
         *
         * @param name Header name.
         * @param valuePrefix Prefix of header value which prefixes JWT.
         */
        data class Header(
            val name: String = HttpHeaders.AUTHORIZATION,
            val valuePrefix: String = "Bearer "
        )

        /**
         * Authentication cookie properties.
         *
         * @param name Name.
         * @param domain Domain.
         * @param secure If cookie secure attribute is enabled.
         * @param httpOnly If cookie http only attribute is enabled.
         */
        data class Cookie(
            val name: String = "_ivana_chess_session",
            val domain: String = "localhost",
            val secure: Boolean = false,
            val httpOnly: Boolean = true
        )
    }
}
