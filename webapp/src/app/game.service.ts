import {Injectable} from '@angular/core'
import {IvanaChessService} from './ivana-chess.service'
import {HttpClient} from '@angular/common/http'
import {Observable} from 'rxjs'
import {Game} from './game'
import {Page} from './page'
import {RxStompService} from '@stomp/ng2-stompjs'
import {Move} from './move'
import {GameSummary} from './game-summary'

/**
 * Game service.
 */
@Injectable({
  providedIn: 'root'
})
export class GameService extends IvanaChessService {
  /**
   * Path.
   * @private
   */
  private path: string = '/game'

  /**
   * Initialize service.
   *
   * @param http HTTP client.
   * @param stompService Stomp service.
   */
  constructor(
    http: HttpClient,
    stompService: RxStompService
  ) {
    super(http, stompService)
  }

  /**
   * Create new game.
   *
   * @return Game.
   */
  createNewGame(): Observable<Game> {
    return this.post(this.path)
  }

  /**
   * Get all games paginated.
   *
   * @param page Page number.
   * @param size Page size.
   * @return Page.
   */
  getAll(page: number, size: number): Observable<Page<GameSummary>> {
    return this.getPaginated(this.path, page, size)
  }

  /**
   * Get game.
   *
   * @param id Game ID.
   * @return Game.
   */
  getGame(id: string): Observable<Game> {
    return this.get(`${this.path}/${id}`)
  }

  /**
   * Play move.
   *
   * @param token Player token.
   * @param move Move.
   * @return Game.
   */
  play(token: string, move: Move): Observable<Game> {
    return this.put(`${this.path}/${token}/play`, move)
  }

  /**
   * Watch game.
   *
   * @param id Game ID.
   * @return Game.
   */
  watchGame(id: string): Observable<Game> {
    return this.watch(`${this.path}/${id}`)
  }
}
