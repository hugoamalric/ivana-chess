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
    val server: Server = Server()
) {
    /**
     * Server properties.
     *
     * @param bindAddress Bind address.
     * @param port Port.
     * @param allowedOrigins Coma-separated list of allowed origins
     */
    data class Server(
        val bindAddress: InetAddress = InetAddress.getByName("0.0.0.0"),
        val port: Int = 8080,
        val allowedOrigins: String = "localhost:4200"
    )
}
