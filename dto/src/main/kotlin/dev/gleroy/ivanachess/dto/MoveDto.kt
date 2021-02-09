package dev.gleroy.ivanachess.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import dev.gleroy.ivanachess.core.Move
import dev.gleroy.ivanachess.core.Piece
import javax.validation.Valid

/**
 * Promotion type.
 */
private const val PromotionType = "promotion"

/**
 * Simple type.
 */
private const val SimpleType = "simple"

/**
 * Move DTO.
 */
@JsonTypeInfo(
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    use = JsonTypeInfo.Id.NAME,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = MoveDto.Promotion::class, name = PromotionType),
    JsonSubTypes.Type(value = MoveDto.Simple::class, name = SimpleType),
)
sealed class MoveDto {
    companion object {
        /**
         * Instantiate DTO from move.
         *
         * @param move Move.
         * @return DTO.
         */
        fun from(move: Move) = when(move) {
            is Move.Promotion -> Promotion(
                from = PositionDto.from(move.from),
                to = PositionDto.from(move.to),
                promotionType = PieceDto.Type.from(move.promotion)
            )
            is Move.Simple -> Simple(
                from = PositionDto.from(move.from),
                to = PositionDto.from(move.to)
            )
        }
    }

    /**
     * Type.
     */
    enum class Type {
        @JsonProperty(SimpleType)
        Simple,

        @JsonProperty(PromotionType)
        Promotion
    }

    /**
     * Promotion move DTO.
     *
     * @param from Start position.
     * @param to Target position.
     * @param promotionType Promotion piece type.
     */
    data class Promotion(
        @field:Valid
        override val from: PositionDto,

        @field:Valid
        override val to: PositionDto,

        val promotionType: PieceDto.Type
    ) : MoveDto() {
        override val type = Type.Promotion

        override fun convert(color: Piece.Color) = Move.Promotion(
            from = from.convert(),
            to = to.convert(),
            promotion = promotionType.instantiatePiece(color)
        )
    }

    /**
     * Simple move DTO.
     *
     * @param from Start position.
     * @param to Target position.
     */
    data class Simple(
        @field:Valid
        override val from: PositionDto,

        @field:Valid
        override val to: PositionDto
    ) : MoveDto() {
        override val type = Type.Simple

        override fun convert(color: Piece.Color) = Move.Simple(
            from = from.convert(),
            to = to.convert()
        )
    }

    /**
     * Type.
     */
    abstract val type: Type

    /**
     * Start position.
     */
    abstract val from: PositionDto

    /**
     * Target position.
     */
    abstract val to: PositionDto

    /**
     * Convert this DTO to move.
     *
     * @param color Player which do this move.
     * @return Move.
     */
    abstract fun convert(color: Piece.Color): Move
}
