package dev.gleroy.ivanachess.game

import com.fasterxml.jackson.annotation.JsonProperty

internal data class PieceDto(
    val color: Color,
    val type: Type,
    val pos: PositionDto
) {
    enum class Color(
        val coreColor: Piece.Color
    ) {
        @JsonProperty("white")
        White(Piece.Color.White),

        @JsonProperty("black")
        Black(Piece.Color.Black);
    }

    enum class Type {
        @JsonProperty("pawn")
        Pawn {
            override fun instantiatePiece(color: Piece.Color) = Piece.Pawn(color)
        },

        @JsonProperty("rook")
        Rook {
            override fun instantiatePiece(color: Piece.Color) = Piece.Rook(color)
        },

        @JsonProperty("knight")
        Knight {
            override fun instantiatePiece(color: Piece.Color) = Piece.Knight(color)
        },

        @JsonProperty("bishop")
        Bishop {
            override fun instantiatePiece(color: Piece.Color) = Piece.Bishop(color)
        },

        @JsonProperty("queen")
        Queen {
            override fun instantiatePiece(color: Piece.Color) = Piece.Queen(color)
        },

        @JsonProperty("king")
        King {
            override fun instantiatePiece(color: Piece.Color) = Piece.King(color)
        };

        internal abstract fun instantiatePiece(color: Piece.Color): Piece
    }

    fun convert() = PositionedPiece(type.instantiatePiece(color.coreColor), pos.convert())
}
