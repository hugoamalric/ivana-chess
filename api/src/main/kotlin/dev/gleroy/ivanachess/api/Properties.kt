package dev.gleroy.ivanachess.api

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.net.InetAddress
import java.net.URI

/**
 * Properties.
 *
 * @param server Server properties.
 * @param webapp Webapp properties.
 */
@ConfigurationProperties(prefix = "ivana-chess")
@ConstructorBinding
data class Properties(
    val server: Server = Server(),
    val webapp: Webapp = Webapp()
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

    /**
     * Webapp properties.
     *
     * @param baseUrl Base URL.
     * @param gamePath Path to game page.
     */
    data class Webapp(
        val baseUrl: URI = URI("http://localhost:3000"),
        val gamePath: String = "/game"
    )
}
