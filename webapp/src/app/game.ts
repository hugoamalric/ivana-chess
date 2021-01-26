import {Color} from './color.enum'
import {Piece} from './piece'
import {Move} from './move'
import {GameState} from './game-state.enum'

export interface Game {
  id: string
  whiteUrl: string
  blackUrl: string
  colorToPlay: Color
  state: GameState
  pieces: Piece[]
  moves: Move[]
  possibleMoves: Move[]
}
