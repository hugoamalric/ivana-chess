import {ComponentFixture, TestBed} from '@angular/core/testing'

import {HomeComponent} from './home.component'
import {GameService} from '../game.service'
import {of} from 'rxjs'

declare var require: any

describe('HomeComponent', () => {
  const gameService = jasmine.createSpyObj('GameService', ['createNewGame'])

  let component: HomeComponent
  let fixture: ComponentFixture<HomeComponent>

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HomeComponent],
      providers: [
        {
          provide: GameService,
          useValue: gameService
        }
      ]
    })
      .compileComponents()
  })

  beforeEach(() => {
    fixture = TestBed.createComponent(HomeComponent)
    component = fixture.componentInstance
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })

  it('should create new game', () => {
    const game = require('test/initial.json')

    const button = fixture.debugElement.nativeElement.querySelector('#new-game-button')
    button.click()

    gameService.createNewGame.and.returnValue(of(game))
    expect(gameService.createNewGame).toHaveBeenCalledWith()
  })
})
