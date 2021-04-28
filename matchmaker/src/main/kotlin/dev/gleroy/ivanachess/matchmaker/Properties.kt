package dev.gleroy.ivanachess.matchmaker

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.net.InetAddress
import java.nio.file.Path

/**
 * Properties.
 *
 * @param broker Broker properties.
 * @param logging Logging properties.
 */
@ConfigurationProperties(prefix = "ivana-chess")
@ConstructorBinding
data class Properties(
    val broker: Broker = Broker(),
    val logging: Logging = Logging(),
) {
    /**
     * Broker properties.
     *
     * @param host Host.
     * @param port Port.
     * @param vhost Virtual host.
     * @param username Username.
     * @param password Password.
     * @param instanceId ID used to create instance-specific queue.
     * @param matchmakingQueue Name of matchmaking queue.
     * @param matchQueue Name of match queue.
     * @param ssl SSL properties.
     */
    data class Broker(
        val host: InetAddress = InetAddress.getLoopbackAddress(),
        val port: Int = 5672,
        val vhost: String = "/",
        val username: String = "guest",
        val password: String = "guest",
        val instanceId: String = "ivana-chess-matchmaking-01",
        val matchmakingQueue: String = "matchmaking",
        val matchQueue: String = "match",
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
     * Logging properties.
     *
     * @param configFile Configuration filepath.
     */
    data class Logging(
        val configFile: Path = Path.of(Logging::class.java.getResource("/logback.xml")!!.file)
    )
}
