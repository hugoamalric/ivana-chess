package dev.gleroy.ivanachess.game

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

private const val PromotionType = "promotion"
private const val SimpleType = "simple"

@JsonTypeInfo(
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    use = JsonTypeInfo.Id.NAME,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = MoveDto.Promotion::class, name = PromotionType),
    JsonSubTypes.Type(value = MoveDto.Simple::class, name = SimpleType),
)
internal sealed class MoveDto {
    data class Promotion(
        override val from: PositionDto,
        override val to: PositionDto,
        val promotionColor: PieceDto.Color,
        val promotionType: PieceDto.Type
    ) : MoveDto() {
        override val type = PromotionType

        override fun convert() = Move.Promotion(
            from = from.convert(),
            to = to.convert(),
            promotion = promotionType.instantiatePiece(promotionColor.coreColor)
        )
    }

    data class Simple(
        override val from: PositionDto,
        override val to: PositionDto
    ) : MoveDto() {
        override val type = SimpleType

        override fun convert() = Move.Simple(
            from = from.convert(),
            to = to.convert()
        )
    }

    abstract val type: String
    abstract val from: PositionDto
    abstract val to: PositionDto

    abstract fun convert(): Move
}
