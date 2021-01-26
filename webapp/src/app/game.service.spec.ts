import {fakeAsync, TestBed} from '@angular/core/testing'

import {GameService} from './game.service'
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing'
import {Game} from './game'
import {environment} from '../environments/environment'

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
    const expectedGame = require('test/game.json') as Game

    service.createNewGame().subscribe(game => expect(game).toEqual(expectedGame))

    const req = controller.expectOne(`${environment.apiBaseUrl}/game`)
    req.flush(expectedGame)
  }))
})
