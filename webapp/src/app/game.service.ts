import { Injectable } from '@angular/core';
import {IvanaChessService} from './ivana-chess.service'
import {HttpClient} from '@angular/common/http'

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
}
