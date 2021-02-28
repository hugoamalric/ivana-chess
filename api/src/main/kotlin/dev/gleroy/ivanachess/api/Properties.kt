package dev.gleroy.ivanachess.api

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.http.HttpHeaders
import java.net.InetAddress

/**
 * Properties.
 *
 * @param db Database properties.
 * @param server Server properties.
 * @param auth Authentication properties.
 */
@ConfigurationProperties(prefix = "ivana-chess")
@ConstructorBinding
data class Properties(
    val server: Server = Server(),
    val db: Database = Database(),
    val auth: Authentication = Authentication()
) {
    /**
     * Database properties.
     *
     * @param host Host.
     * @param port Port.
     * @param name Name.
     * @param schema Schema.
     * @param username Username used to connect to database.
     * @param password Password used to connect to database.
     */
    data class Database(
        val host: InetAddress = InetAddress.getLoopbackAddress(),
        val port: Int = 5432,
        val name: String = "ivanachessapi",
        val schema: String = "public",
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
     * @param header HTTP header which contains authentication.
     * @param headerValuePrefix HTTP header value prefix.
     * @param cookie Name of cookie used to send JWT.
     */
    data class Authentication(
        val secret: String = "changeit",
        val validity: Int = 7 * 24 * 60 * 60,
        val header: String = HttpHeaders.AUTHORIZATION,
        val headerValuePrefix: String = "Bearer ",
        val cookie: String = "_ivana_chess_session"
    )
}
