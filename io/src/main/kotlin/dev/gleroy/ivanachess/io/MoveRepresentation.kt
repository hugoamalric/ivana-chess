package dev.gleroy.ivanachess.io

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import javax.validation.Valid

private const val PromotionType = "promotion"
private const val SimpleType = "simple"

/**
 * Representation of move.
 */
@JsonTypeInfo(
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    use = JsonTypeInfo.Id.NAME,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = MoveRepresentation.Promotion::class, name = PromotionType),
    JsonSubTypes.Type(value = MoveRepresentation.Simple::class, name = SimpleType),
)
sealed class MoveRepresentation {
    /**
     * Representation of promotion move.
     *
     * @param from Start position.
     * @param to Target position.
     * @param promotionColor Promotion piece color.
     * @param promotionType Promotion piece type.
     */
    data class Promotion(
        @field:Valid
        override val from: PositionRepresentation,

        @field:Valid
        override val to: PositionRepresentation,

        val promotionColor: PieceRepresentation.Color,

        val promotionType: PieceRepresentation.Type,
    ) : MoveRepresentation() {
        override val type get() = PromotionType
    }

    /**
     * Representation of simple move.
     *
     * @param from Start position.
     * @param to Target position.
     */
    data class Simple(
        @field:Valid
        override val from: PositionRepresentation,

        @field:Valid
        override val to: PositionRepresentation,
    ) : MoveRepresentation() {
        override val type get() = SimpleType
    }

    /**
     * Type.
     */
    abstract val type: String

    /**
     * Start position.
     */
    abstract val from: PositionRepresentation

    /**
     * Target position.
     */
    abstract val to: PositionRepresentation
}
