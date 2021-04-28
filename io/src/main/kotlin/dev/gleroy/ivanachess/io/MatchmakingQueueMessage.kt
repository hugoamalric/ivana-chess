package dev.gleroy.ivanachess.io

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

private const val JoinMessageType = "join"
private const val LeaveMessageType = "leave"

/**
 * Message on matchmaking queue.
 */
@JsonTypeInfo(
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    use = JsonTypeInfo.Id.NAME,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = MatchmakingQueueMessage.Join::class, name = JoinMessageType),
    JsonSubTypes.Type(value = MatchmakingQueueMessage.Leave::class, name = LeaveMessageType),
)
sealed class MatchmakingQueueMessage {
    /**
     * Message sent when an user joins matchmaking queue.
     *
     * @param user Representation of user.
     */
    data class Join(
        override val user: UserQueueMessage,
    ) : MatchmakingQueueMessage() {
        override val type get() = JoinMessageType
    }

    /**
     * Message sent when an user leaves matchmaking queue.
     *
     * @param user Representation of user.
     */
    data class Leave(
        override val user: UserQueueMessage,
    ) : MatchmakingQueueMessage() {
        override val type get() = LeaveMessageType
    }

    /**
     * Message type.
     */
    abstract val type: String

    /**
     * Representation of user.
     */
    abstract val user: UserQueueMessage
}
