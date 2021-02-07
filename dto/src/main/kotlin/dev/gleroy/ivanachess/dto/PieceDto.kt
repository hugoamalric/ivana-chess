package dev.gleroy.ivanachess.dto

import com.fasterxml.jackson.annotation.JsonProperty
import dev.gleroy.ivanachess.core.Piece
import dev.gleroy.ivanachess.core.PositionedPiece

/**
 * Piece DTO.
 *
 * @param color Color.
 * @param type Type.
 * @param pos Position.
 */
data class PieceDto(
    val color: Color,
    val type: Type,
    val pos: PositionDto
) {
    companion object {
        /**
         * Instantiate DTO from positioned piece.
         *
         * @param positionedPiece Positioned piece.
         * @return DTO.
         */
        fun from(positionedPiece: PositionedPiece): PieceDto {
            val type = when (positionedPiece.piece) {
                is Piece.Pawn -> Type.Pawn
                is Piece.Rook -> Type.Rook
                is Piece.Knight -> Type.Knight
                is Piece.Bishop -> Type.Bishop
                is Piece.Queen -> Type.Queen
                is Piece.King -> Type.King
            }
            return PieceDto(
                color = Color.from(positionedPiece.piece.color),
                type = type,
                pos = PositionDto.from(positionedPiece.pos)
            )
        }
    }

    /**
     * Color.
     *
     * @param coreColor Color.
     */
    enum class Color(
        val coreColor: Piece.Color
    ) {
        /**
         * White.
         */
        @JsonProperty("white")
        White(Piece.Color.White),

        /**
         * Black.
         */
        @JsonProperty("black")
        Black(Piece.Color.Black);

        companion object {
            /**
             * Get DTO from color.
             *
             * @param color Color.
             * @return DTO.
             */
            fun from(color: Piece.Color) = when (color) {
                Piece.Color.White -> White
                Piece.Color.Black -> Black
            }
        }
    }

    /**
     * Type.
     */
    enum class Type {
        /**
         * Pawn.
         */
        @JsonProperty("pawn")
        Pawn {
            override fun instantiatePiece(color: Piece.Color) = Piece.Pawn(color)
        },

        /**
         * Rook.
         */
        @JsonProperty("rook")
        Rook {
            override fun instantiatePiece(color: Piece.Color) = Piece.Rook(color)
        },

        /**
         * Knight.
         */
        @JsonProperty("knight")
        Knight {
            override fun instantiatePiece(color: Piece.Color) = Piece.Knight(color)
        },

        /**
         * Bishop.
         */
        @JsonProperty("bishop")
        Bishop {
            override fun instantiatePiece(color: Piece.Color) = Piece.Bishop(color)
        },

        /**
         * Queen.
         */
        @JsonProperty("queen")
        Queen {
            override fun instantiatePiece(color: Piece.Color) = Piece.Queen(color)
        },

        /**
         * King.
         */
        @JsonProperty("king")
        King {
            override fun instantiatePiece(color: Piece.Color) = Piece.King(color)
        };

        internal abstract fun instantiatePiece(color: Piece.Color): Piece
    }

    /**
     * Convert this DTO to positioned piece.
     *
     * @return Positioned piece.
     */
    fun convert() = PositionedPiece(type.instantiatePiece(color.coreColor), pos.convert())
}
