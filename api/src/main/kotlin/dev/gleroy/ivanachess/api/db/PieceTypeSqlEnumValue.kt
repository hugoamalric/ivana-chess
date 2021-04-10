package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.game.Piece

/**
 * Piece type SQL enumeration value.
 *
 * @param label SQL label.
 */
internal enum class PieceTypeSqlEnumValue(
    override val label: String,
) : SqlEnumValue {
    /**
     * Pawn.
     */
    Pawn("pawn") {
        override fun instantiatePiece(color: Piece.Color) = Piece.Pawn(color)
    },

    /**
     * Rook.
     */
    Rook("rook") {
        override fun instantiatePiece(color: Piece.Color) = Piece.Rook(color)
    },

    /**
     * Knight.
     */
    Knight("knight") {
        override fun instantiatePiece(color: Piece.Color) = Piece.Knight(color)
    },

    /**
     * Bishop.
     */
    Bishop("bishop") {
        override fun instantiatePiece(color: Piece.Color) = Piece.Bishop(color)
    },

    /**
     * Queen.
     */
    Queen("queen") {
        override fun instantiatePiece(color: Piece.Color) = Piece.Queen(color)
    },

    /**
     * King.
     */
    King("king") {
        override fun instantiatePiece(color: Piece.Color) = Piece.King(color)
    };

    companion object {
        /**
         * Get piece type SQL enumeration value from piece.
         *
         * @param piece Piece.
         * @return Piece type SQL enumeration value.
         */
        fun from(piece: Piece) = when (piece) {
            is Piece.Pawn -> Pawn
            is Piece.Rook -> Rook
            is Piece.Knight -> Knight
            is Piece.Bishop -> Bishop
            is Piece.Queen -> Queen
            is Piece.King -> King
        }
    }

    abstract fun instantiatePiece(color: Piece.Color): Piece
}
