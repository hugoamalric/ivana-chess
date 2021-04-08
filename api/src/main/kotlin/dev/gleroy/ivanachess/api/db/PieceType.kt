package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.game.Piece

/**
 * Piece type.
 */
internal enum class PieceType {
    /**
     * Pawn.
     */
    Pawn {
        override fun instantiatePiece(color: Piece.Color) = Piece.Pawn(color)
    },

    /**
     * Rook.
     */
    Rook {
        override fun instantiatePiece(color: Piece.Color) = Piece.Rook(color)
    },

    /**
     * Knight.
     */
    Knight {
        override fun instantiatePiece(color: Piece.Color) = Piece.Knight(color)
    },

    /**
     * Bishop.
     */
    Bishop {
        override fun instantiatePiece(color: Piece.Color) = Piece.Bishop(color)
    },

    /**
     * Queen.
     */
    Queen {
        override fun instantiatePiece(color: Piece.Color) = Piece.Queen(color)
    },

    /**
     * King.
     */
    King {
        override fun instantiatePiece(color: Piece.Color) = Piece.King(color)
    };

    companion object {
        /**
         * Get piece type from piece.
         *
         * @param piece Piece.
         * @return Piece type.
         */
        fun from(piece: Piece) = when (piece) {
            is Piece.Pawn -> Pawn
            is Piece.Rook -> Rook
            is Piece.Knight -> Knight
            is Piece.Bishop -> Bishop
            is Piece.Queen -> Queen
            is Piece.King -> King
        }

        /**
         * Get piece type from SQL type value.
         *
         * @param sqlValue SQL type value.
         * @return Piece type.
         * @throws IllegalArgumentException If SQL type value is not a valid piece type.
         */
        @Throws(IllegalArgumentException::class)
        fun from(sqlValue: String) = values().find { it.sqlValue == sqlValue }
            ?: throw IllegalArgumentException("Unknown piece type '$sqlValue'")
    }

    /**
     * SQL value.
     */
    val sqlValue = name.toLowerCase()

    abstract fun instantiatePiece(color: Piece.Color): Piece
}
