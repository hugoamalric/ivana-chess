import {ComponentFixture, fakeAsync, TestBed, tick, waitForAsync} from '@angular/core/testing'

import {GameComponent} from './game.component'
import {ActivatedRoute, Router} from '@angular/router'
import {Location} from '@angular/common'
import {RouterTestingModule} from '@angular/router/testing'
import {routes} from '../app-routing.module'
import {GameService} from '../game.service'
import {of} from 'rxjs'

declare var require: any

describe('GameComponent', () => {
  const gameService = jasmine.createSpyObj('GameService', ['getGame'])

  const game = require('test/game/initial.json')

  let component: GameComponent
  let fixture: ComponentFixture<GameComponent>
  let router: Router
  let location: Location

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes(routes)],
      declarations: [GameComponent],
      providers: [
        {
          provide: GameService,
          useValue: gameService
        },
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of({
              get(name: string): string | null {
                if (name === 'id') {
                  return game.id
                }
                return null
              }
            })
          }
        }
      ]
    })
      .compileComponents()
  })

  beforeEach(waitForAsync(() => {
    gameService.getGame.and.returnValue(of(game))

    fixture = TestBed.createComponent(GameComponent)
    component = fixture.componentInstance
    router = TestBed.inject(Router)
    location = TestBed.inject(Location)
    router.initialNavigation()
    fixture.detectChanges()
  }))

  it('should create', fakeAsync(() => {
    tick()

    expect(component).toBeTruthy()
    expect(gameService.getGame).toHaveBeenCalledWith(game.id)
    expect(component.game).toEqual(game)
  }))
})
