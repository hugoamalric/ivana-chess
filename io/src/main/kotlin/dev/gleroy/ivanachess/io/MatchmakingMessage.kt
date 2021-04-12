package dev.gleroy.ivanachess.io

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.util.*

private const val JoinMessageType = "join"
private const val LeaveMessageType = "leave"

/**
 * Matchmaking message.
 */
@JsonTypeInfo(
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    use = JsonTypeInfo.Id.NAME,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = MatchmakingMessage.Join::class, name = JoinMessageType),
    JsonSubTypes.Type(value = MatchmakingMessage.Leave::class, name = LeaveMessageType),
)
sealed class MatchmakingMessage {
    /**
     * Join message.
     *
     * @param id User ID.
     * @param pseudo User pseudo.
     */
    data class Join(
        override val id: UUID,
        override val pseudo: String,
    ) : MatchmakingMessage() {
        override val type get() = JoinMessageType
    }

    /**
     * Leave message.
     *
     * @param id User ID.
     * @param pseudo User pseudo.
     */
    data class Leave(
        override val id: UUID,
        override val pseudo: String,
    ) : MatchmakingMessage() {
        override val type get() = LeaveMessageType
    }

    /**
     * Message type.
     */
    abstract val type: String

    /**
     * User ID.
     */
    abstract val id: UUID

    /**
     * User pseudo.
     */
    abstract val pseudo: String
}
