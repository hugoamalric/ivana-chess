import {fakeAsync, TestBed} from '@angular/core/testing'

import {GameService} from './game.service'
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing'
import {Game} from './game'
import {environment} from '../environments/environment'
import {Page} from './page'

declare var require: any

describe('GameService', () => {
  let service: GameService
  let controller: HttpTestingController

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    })
    service = TestBed.inject(GameService)
    controller = TestBed.inject(HttpTestingController)
  })

  afterEach(() => {
    controller.verify()
  })

  it('should be created', () => {
    expect(service).toBeTruthy()
  })

  it('should create new game', fakeAsync(() => {
    const expectedGame = require('test/game/initial.json') as Game

    service.createNewGame().subscribe(game => expect(game).toEqual(expectedGame))

    const req = controller.expectOne(`${environment.apiBaseUrl}/game`)
    req.flush(expectedGame)
  }))

  it('should get all games', fakeAsync(() => {
    const expectedPage = require('test/game/page.json') as Page<Game>
    const pageNb = 1
    const size = 10

    service.getAll(pageNb, size).subscribe(page => expect(page).toEqual(expectedPage))

    const req = controller.expectOne(`${environment.apiBaseUrl}/game?page=${pageNb}&size=${size}`)
    req.flush(expectedPage)
  }))

  it('should get game', fakeAsync(() => {
    const expectedGame = require('test/game/initial.json') as Game

    service.getGame(expectedGame.id).subscribe(game => expect(game).toEqual(expectedGame))

    const req = controller.expectOne(`${environment.apiBaseUrl}/game/${expectedGame.id}`)
    req.flush(expectedGame)
  }))
})
