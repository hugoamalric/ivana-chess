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
import {Filter} from './filter'

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
    return this.doPost(this.path, {whitePlayer, blackPlayer})
  }

  /**
   * Get all page of games.
   *
   * @param page Page number.
   * @param size Page size.
   * @param sorts List of sorts.
   * @param filters List of filters.
   * @return Observable<Page<GameSummary>> Observable which contains page.
   */
  getPage(page: number, size: number, sorts: Sort[] = [], filters: Filter[] = []): Observable<Page<GameSummary>> {
    return this.doPaginatedGet(this.path, page, size, sorts, filters)
  }

  /**
   * Get game.
   *
   * @param id Game ID.
   * @return Observable<Game> Observable which contains game.
   */
  get(id: string): Observable<Game> {
    return this.doGet(`${this.path}/${id}`)
  }

  /**
   * Add current authenticated user to matchmaking queue.
   *
   * @return Observable<void> Empty observable.
   */
  joinMatchmakingQueue(): Observable<void> {
    return this.doPut(`${this.path}/match`)
  }

  /**
   * Remove current authenticated user from matchmaking queue.
   *
   * @return Observable<void> Empty observable.
   */
  leaveMatchmakingQueue(): Observable<void> {
    return this.doDelete(`${this.path}/match`)
  }

  /**
   * Play move.
   *
   * @param id Game ID.
   * @param move Move.
   * @return Observable<Game> Observable which contains game.
   */
  play(id: string, move: Move): Observable<Game> {
    return this.doPut(`${this.path}/${id}/play`, move)
  }

  /**
   * Watch game.
   *
   * @param id Game ID.
   * @return Observable<Game> Observable which contains game.
   */
  watch(id: string): Observable<Game> {
    return this.doWatch(`${this.path}-${id}`)
  }

  /**
   * Watch matchmaking queue.
   *
   * @return Observable<Game> Observable which contains game.
   */
  watchMatchmakingQueue(): Observable<Game> {
    return this.doWatch(this.path)
  }
}
