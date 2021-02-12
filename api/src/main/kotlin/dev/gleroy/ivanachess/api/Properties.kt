package dev.gleroy.ivanachess.api

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.net.InetAddress

/**
 * Properties.
 *
 * @param server Server properties.
 */
@ConfigurationProperties(prefix = "ivana-chess")
@ConstructorBinding
data class Properties(
    val db: Database = Database(),
    val server: Server = Server()
) {
    /**
     * Database properties.
     *
     * @param host Host.
     * @param port Port.
     * @param name Name.
     * @param username Username used to connect to database.
     * @param password Password used to connect to database.
     */
    data class Database(
        val host: InetAddress = InetAddress.getLoopbackAddress(),
        val port: Int = 5432,
        val name: String = "ivanachessapi",
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
}
