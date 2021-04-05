import {Component, OnInit} from '@angular/core'
import {ActivatedRoute, Router} from '@angular/router'
import {GameService} from '../game.service'
import {Game} from '../game'
import {Color} from '../color.enum'
import {Move} from '../move'
import {Position} from '../position'
import {Piece} from '../piece'
import {PieceType} from '../piece-type.enum'
import {AuthenticationService} from '../authentication.service'
import {ErrorService} from '../error.service'
import {catchError} from 'rxjs/operators'
import {User} from '../user'
import {GameState} from '../game-state.enum'

/**
 * Game component.
 */
@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.scss']
})
export class GameComponent implements OnInit {
  /**
   * Game.
   */
  game: Game | null = null

  /**
   * Color of authenticated user (if it is a player).
   */
  playerColor: Color | null = null

  /**
   * List of possible promotion moves.
   */
  possiblePromotionMoves: Move[] = []

  /**
   * Current authenticated user.
   */
  me: User | null = null

  /**
   * Color enumeration.
   */
  Color = Color

  /**
   * Game state enumeration.
   */
  GameState = GameState

  /**
   * Possible positions.
   */
  private possiblePositions: Position[] = []

  /**
   * Selected position.
   */
  private selectedPosition: Position | null = null

  /**
   * Check if position equals another one.
   *
   * @param pos1 Position.
   * @param pos2 Position.
   * @return True if positions are equal, false otherwise.
   */
  private static positionEquals(pos1: Position, pos2: Position): boolean {
    return GameComponent.positionHasCoordinates(pos1, pos2.col, pos2.row)
  }

  /**
   * Check if position has given coordinates.
   *
   * @param pos Position.
   * @param col Column index.
   * @param row Row index.
   * @return True if position has given coordinates, false otherwise.
   */
  private static positionHasCoordinates(pos: Position, col: number, row: number): boolean {
    return pos.col === col && pos.row === row
  }

  /**
   * Initialize component.
   *
   * @param gameService Game service.
   * @param authService Authentication service.
   * @param historyService History service.
   * @param errorService Error service.
   * @param route Current route.
   * @param router Router.
   */
  constructor(
    private gameService: GameService,
    private authService: AuthenticationService,
    private errorService: ErrorService,
    private route: ActivatedRoute,
    private router: Router
  ) {
  }

  /**
   * Choose promotion.
   *
   * @param move Promotion move.
   */
  choosePromotion(move: Move): void {
    this.possiblePromotionMoves = []
    this.play(move)
  }

  /**
   * Get column indexes.
   *
   * @return Column indexes
   */
  columnIndexes(): number[] {
    return Array.from(Array(8).keys())
      .map(i => i + 1)
  }

  /**
   * Go back.
   */
  goBack(): void {
    this.router.navigate(['/']).then()
  }

  /**
   * Check if position is part of last move.
   *
   * @param col Column index.
   * @param row Row index.
   * @return True if position is part of last move, false otherwise.
   */
  isPartOfLastMove(col: number, row: number): boolean {
    const moves = this.game!!.moves
    return moves.length > 0 && (GameComponent.positionHasCoordinates(moves[moves.length - 1].from, col, row) ||
      GameComponent.positionHasCoordinates(moves[moves.length - 1].to, col, row))
  }

  /**
   * Check if position is possible.
   *
   * @param col Column index.
   * @param row Row index.
   * @return True if position is selected, false otherwise.
   */
  isPossiblePosition(col: number, row: number): boolean {
    return this.possiblePositions.filter(pos => GameComponent.positionHasCoordinates(pos, col, row)).length > 0
  }

  /**
   * Check if position is selected one.
   *
   * @param col Column index.
   * @param row Row index.
   * @return True if position is selected one, false otherwise.
   */
  isSelectedPosition(col: number, row: number): boolean {
    return this.selectedPosition !== null && GameComponent.positionHasCoordinates(this.selectedPosition, col, row)
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id')
      this.gameService.getGame(id!!)
        .pipe(catchError(error => this.errorService.handleApiError<Game>(error)))
        .subscribe(game => {
          this.game = game
          this.gameService.watchGame(this.game.id).subscribe(game => this.game = game)
          this.authService.me().subscribe(user => {
            this.me = user
            if (user?.id === this.game!!.whitePlayer.id) {
              this.playerColor = Color.White
            } else if (user?.id === this.game!!.blackPlayer.id) {
              this.playerColor = Color.Black
            }
          })
        })
    })
  }

  /**
   * Get piece at position.
   *
   * @param col Column index.
   * @param row Row index.
   * @return Piece or null if no piece at position.
   */
  pieceAt(col: number, row: number): Piece | null {
    const piece = this.game?.pieces.find(piece => GameComponent.positionHasCoordinates(piece.pos, col, row))
    return piece === undefined ? null : piece
  }

  /**
   * Get image source of piece.
   *
   * @param color Piece color.
   * @param type Piece type.
   * @return Image source.
   */
  pieceImageSource(color: Color, type: PieceType): string {
    return `assets/board/pieces/${type}_${color}.svg`
  }

  /**
   * Get row indexes.
   *
   * @return Row indexes
   */
  rowIndexes(): number[] {
    const indexes = Array.from(Array(8).keys())
      .map(i => i + 1)
    if (this.playerColor === Color.White) {
      return indexes.reverse()
    }
    return indexes
  }

  /**
   * Select position.
   *
   * If given coordinates are the same as the current selected position, it will be reset.
   * If given coordinates match another player piece, selected position will be updated.
   * If current selected position is not null and given coordinates match simple move, it will be played.
   * If current selected position is not null and given coordinates match promotion moves, they will be updated.
   *
   * @param col Column index.
   * @param row Row index.
   */
  selectPosition(col: number, row: number): void {
    if (this.game?.state === GameState.InGame && this.playerColor !== null && this.playerColor === this.game.turnColor) {
      const piece = this.pieceAt(col, row)
      if (this.selectedPosition === null) {
        if (piece !== null && piece.color === this.playerColor) {
          this.updateSelectedPosition(col, row)
        }
      } else {
        if (GameComponent.positionHasCoordinates(this.selectedPosition, col, row)) {
          this.resetSelectedPosition()
        } else if (piece?.color === this.playerColor) {
          this.updateSelectedPosition(col, row)
        } else {
          const moves = this.game.possibleMoves.filter(move =>
            GameComponent.positionEquals(move.from, this.selectedPosition!!) && GameComponent.positionHasCoordinates(move.to, col, row)
          )
          if (moves.length === 1) {
            this.play(moves[0])
          } else if (moves.length > 1) {
            this.possiblePromotionMoves = moves
          }
        }
      }
    }
  }

  /**
   *
   */
  turnPlayer(): User {
    if (this.game!!.turnColor === Color.White) {
      return this.game!!.whitePlayer
    } else {
      return this.game!!.blackPlayer
    }
  }

  /**
   * Reset selected position.
   * @private
   */
  private resetSelectedPosition(): void {
    this.selectedPosition = null
    this.possiblePositions = []
  }

  /**
   * Play move.
   *
   * @param move Move.
   * @private
   */
  private play(move: Move): void {
    this.resetSelectedPosition()
    this.gameService.play(this.game!!.id, move)
      .pipe(catchError(error => this.errorService.handleApiError(error)))
      .subscribe()
  }

  /**
   * Update selected position.
   *
   * @param col Column index.
   * @param row Row index.
   * @private
   */
  private updateSelectedPosition(col: number, row: number): void {
    this.selectedPosition = {col, row}
    this.possiblePositions = this.game!!.possibleMoves
      .filter(move => GameComponent.positionHasCoordinates(move.from, col, row))
      .map(move => move.to)
  }
}
