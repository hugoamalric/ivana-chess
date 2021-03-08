package dev.gleroy.ivanachess.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
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
    /**
     * Promotion move DTO.
     *
     * @param from Start position.
     * @param to Target position.
     * @param promotionColor Promotion piece color.
     * @param promotionType Promotion piece type.
     */
    data class Promotion(
        @field:Valid
        override val from: PositionDto,

        @field:Valid
        override val to: PositionDto,

        val promotionColor: PieceDto.Color,

        val promotionType: PieceDto.Type
    ) : MoveDto() {
        override val type = PromotionType
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
        override val type = SimpleType
    }

    /**
     * Type.
     */
    abstract val type: String

    /**
     * Start position.
     */
    abstract val from: PositionDto

    /**
     * Target position.
     */
    abstract val to: PositionDto
}
