import {Injectable} from '@angular/core'
import {IvanaChessService} from './ivana-chess.service'
import {HttpClient} from '@angular/common/http'
import {Observable} from 'rxjs'
import {Game} from './game'
import {Page} from './page'

/**
 * Game service.
 */
@Injectable({
  providedIn: 'root'
})
export class GameService extends IvanaChessService {

  /**
   * Initialize service.
   * @param http HTTP client.
   */
  constructor(
    http: HttpClient
  ) {
    super(http)
  }

  /**
   * Create new game.
   * @return Game.
   */
  createNewGame(): Observable<Game> {
    return this.post('/game')
  }

  /**
   * Get all games paginated.
   * @param page Page number.
   * @param size Page size.
   * @return Page.
   */
  getAll(page: number, size: number): Observable<Page<Game>> {
    return this.getPaginated('/game', page, size)
  }
}
