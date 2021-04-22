package dev.gleroy.ivanachess.api

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.http.HttpHeaders
import java.net.InetAddress
import java.nio.file.Path

/**
 * Properties.
 *
 * @param server Server properties.
 * @param db Database properties.
 * @param broker Broker properties.
 * @param stomp STOMP properties.
 * @param auth Authentication properties.
 * @param logging Logging properties.
 */
@ConfigurationProperties(prefix = "ivana-chess")
@ConstructorBinding
data class Properties(
    val server: Server = Server(),
    val db: Database = Database(),
    val broker: Broker = Broker(),
    val stomp: Stomp = Stomp(),
    val auth: Authentication = Authentication(),
    val logging: Logging = Logging(),
) {
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

    /**
     * Broker properties.
     *
     * @param host Host.
     * @param port Port.
     * @param vhost Virtual host.
     * @param username Username.
     * @param password Password.
     * @param matchQueue Name of match queue.
     * @param matchmakingQueue Name of matchmaking queue.
     * @param matchmakingLeaveExchange name of matchmaking leave exchange.
     * @param matchmakingInstancesIds Coma-separated list of ivana-chess-matchmaking instances IDs.
     * @param ssl SSL properties.
     */
    data class Broker(
        val host: InetAddress = InetAddress.getLoopbackAddress(),
        val port: Int = 5672,
        val vhost: String = "/",
        val username: String = "guest",
        val password: String = "guest",
        val matchQueue: String = "match",
        val matchmakingQueue: String = "matchmaking",
        val matchmakingLeaveExchange: String = "matchmaking-leave",
        val matchmakingInstancesIds: String = "ivana-chess-matchmaking-01",
        val ssl: Ssl = Ssl(),
    ) {
        /**
         * SSL properties.
         *
         * @param enabled True if SSL is enabled, false otherwise.
         * @param verifyHostname True if SSL certificate must be verified, false otherwise.
         */
        data class Ssl(
            val enabled: Boolean = false,
            val verifyHostname: Boolean = false,
        )
    }

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
     * Logging properties.
     *
     * @param configFile Configuration filepath.
     */
    data class Logging(
        val configFile: Path = Path.of(Logging::class.java.getResource("/logback.xml")!!.file)
    )

    /**
     * Server properties.
     *
     * @param bindAddress Bind address.
     * @param port Port.
     * @param contextPath Context path.
     * @param allowedOrigins Coma-separated list of allowed origins.
     * @param ssl SSL properties.
     */
    data class Server(
        val bindAddress: InetAddress = InetAddress.getByName("0.0.0.0"),
        val port: Int = 8080,
        val contextPath: String = "",
        val allowedOrigins: String = "localhost:4200",
        val ssl: Ssl = Ssl()
    ) {
        /**
         * Server SSL properties.
         *
         * @param enabled True if SSL is enabled, false otherwise.
         * @param keystore Path to keystore file.
         * @param keystoreType Type of keystore.
         * @param keystorePassword Password of keystore.
         * @param keyAlias Alias of key in keystore.
         * @param truststore Path to truststore file.
         * @param truststoreType Type of truststore.
         * @param truststorePassword Password of truststore.
         */
        data class Ssl(
            val enabled: Boolean = false,
            val keystore: Path = Path.of(""),
            val keystoreType: String = "PKCS12",
            val keystorePassword: String = "changeit",
            val keyAlias: String = "localhost",
            val truststore: Path = Path.of(""),
            val truststoreType: String = "PKCS12",
            val truststorePassword: String = "changeit"
        )
    }

    /**
     * STOMP properties.
     *
     * @param host Host.
     * @param port Port.
     * @param vhost Virtual host.
     * @param username Username.
     * @param password Password.
     * @param sslEnabled True if SSL is enabled, false otherwise.
     */
    data class Stomp(
        val host: InetAddress = InetAddress.getLoopbackAddress(),
        val port: Int = 61613,
        val vhost: String = "/",
        val username: String = "guest",
        val password: String = "guest",
        val sslEnabled: Boolean = false
    )
}
