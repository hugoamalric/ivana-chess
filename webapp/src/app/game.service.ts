import {Injectable} from '@angular/core'
import {IvanaChessService} from './ivana-chess.service'
import {HttpClient} from '@angular/common/http'
import {Observable} from 'rxjs'
import {Game} from './game'
import {Page} from './page'
import {RxStompService} from '@stomp/ng2-stompjs'
import {Move} from './move'
import {GameSummary} from './game-summary'
import {Sort} from './sort'

/**
 * Game service.
 */
@Injectable({
  providedIn: 'root'
})
export class GameService extends IvanaChessService {
  /**
   * Path.
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
   * @param whitePlayer White player user ID.
   * @param blackPlayer Black player user ID.
   * @return Observable<Game> Observable which contains game.
   */
  createNewGame(whitePlayer: string, blackPlayer: string): Observable<Game> {
    return this.post(this.path, {whitePlayer, blackPlayer})
  }

  /**
   * Get all games paginated.
   *
   * @param page Page number.
   * @param size Page size.
   * @param sorts List of sorts.
   * @return Observable<Page<GameSummary>> Observable which contains page.
   */
  getAll(page: number, size: number, sorts: Sort[] = []): Observable<Page<GameSummary>> {
    return this.getPage(this.path, page, size, sorts)
  }

  /**
   * Get game.
   *
   * @param id Game ID.
   * @return Observable<Game> Observable which contains game.
   */
  getGame(id: string): Observable<Game> {
    return this.get(`${this.path}/${id}`)
  }

  /**
   * Add current authenticated user to matchmaking queue.
   *
   * @return Observable<void> Empty observable.
   */
  joinMatchmakingQueue(): Observable<void> {
    return this.put(`${this.path}/match`)
  }

  /**
   * Remove current authenticated user from matchmaking queue.
   *
   * @return Observable<void> Empty observable.
   */
  leaveMatchmakingQueue(): Observable<void> {
    return this.delete(`${this.path}/match`)
  }

  /**
   * Play move.
   *
   * @param id Game ID.
   * @param move Move.
   * @return Observable<Game> Observable which contains game.
   */
  play(id: string, move: Move): Observable<Game> {
    return this.put(`${this.path}/${id}/play`, move)
  }

  /**
   * Watch game.
   *
   * @param id Game ID.
   * @return Observable<Game> Observable which contains game.
   */
  watchGame(id: string): Observable<Game> {
    return this.watch(`${this.path}-${id}`)
  }

  /**
   * Watch matchmaking queue.
   *
   * @return Observable<Game> Observable which contains game.
   */
  watchMatchmakingQueue(): Observable<Game> {
    return this.watch(this.path)
  }
}
