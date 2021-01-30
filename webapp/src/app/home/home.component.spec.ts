import {ComponentFixture, fakeAsync, TestBed, tick, waitForAsync} from '@angular/core/testing'

import {HomeComponent} from './home.component'
import {GameService} from '../game.service'
import {of} from 'rxjs'
import {Router} from '@angular/router'
import {RouterTestingModule} from '@angular/router/testing'
import {routes} from '../app-routing.module'
import {Page} from '../page'
import {Game} from '../game'
import {Location} from '@angular/common'

declare var require: any

describe('HomeComponent', () => {
  const gameService = jasmine.createSpyObj('GameService', ['createNewGame', 'getAll'])

  const game = require('test/game/initial.json')
  const pages = Array.from(Array(3).keys()).map(i => {
    return {
      content: [game],
      number: i + 1,
      totalPages: 3,
      totalItems: 1
    } as Page<Game>
  })

  let component: HomeComponent
  let fixture: ComponentFixture<HomeComponent>
  let router: Router
  let location: Location

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes(routes)],
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

  beforeEach(waitForAsync(() => {
    gameService.getAll.and.returnValue(of(pages[0]))

    fixture = TestBed.createComponent(HomeComponent)
    component = fixture.componentInstance
    router = TestBed.inject(Router)
    location = TestBed.inject(Location)
    router.initialNavigation()
    fixture.detectChanges()
  }))

  it('should create', fakeAsync(() => {
    tick()
    fixture.detectChanges()

    const previousPageButton = fixture.debugElement.nativeElement.querySelector('#previous-page-button')
    const nextPageButton = fixture.debugElement.nativeElement.querySelector('#next-page-button')

    expect(component).toBeTruthy()
    expect(gameService.getAll).toHaveBeenCalledWith(1, component.pageSize)
    expect(component.page).toEqual(pages[0])
    expect(previousPageButton.disabled).toBeTrue()
    expect(nextPageButton.disabled).toBeFalse()
  }))

  it('should create with page params', fakeAsync(() => {
    gameService.getAll.and.returnValue(of(pages[1]))

    router.navigateByUrl('/?page=2')
    tick()
    fixture.detectChanges()

    const previousPageButton = fixture.debugElement.nativeElement.querySelector('#previous-page-button')
    const nextPageButton = fixture.debugElement.nativeElement.querySelector('#next-page-button')

    expect(component).toBeTruthy()
    expect(gameService.getAll).toHaveBeenCalledWith(2, component.pageSize)
    expect(component.page).toEqual(pages[1])
    expect(previousPageButton.disabled).toBeFalse()
    expect(nextPageButton.disabled).toBeFalse()
  }))

  it('should create new game', fakeAsync(() => {
    gameService.getAll.and.returnValue(of(pages[1]))

    router.navigateByUrl('/?page=2')
    tick()
    fixture.detectChanges()

    gameService.createNewGame.and.returnValue(of(game))

    const previousPageButton = fixture.debugElement.nativeElement.querySelector('#previous-page-button')
    const nextPageButton = fixture.debugElement.nativeElement.querySelector('#next-page-button')
    const newGameButton = fixture.debugElement.nativeElement.querySelector('#new-game-button')
    newGameButton.click()

    expect(gameService.createNewGame).toHaveBeenCalledWith()
    expect(gameService.getAll).toHaveBeenCalledWith(component.page?.number, component.pageSize)
    expect(location.isCurrentPathEqualTo('/?page=2')).toBeTrue()
    expect(previousPageButton.disabled).toBeFalse()
    expect(nextPageButton.disabled).toBeFalse()
  }))

  it('should go next page', fakeAsync(() => {
    gameService.getAll.and.returnValue(of(pages[1]))

    router.navigateByUrl('/?page=2')
    tick()
    fixture.detectChanges()

    gameService.getAll.and.returnValue(of(pages[2]))

    const previousPageButton = fixture.debugElement.nativeElement.querySelector('#previous-page-button')
    const nextPageButton = fixture.debugElement.nativeElement.querySelector('#next-page-button')
    nextPageButton.click()
    tick()
    fixture.detectChanges()

    expect(gameService.getAll).toHaveBeenCalledWith(3, component.pageSize)
    expect(location.isCurrentPathEqualTo('/?page=3')).toBeTrue()
    expect(previousPageButton.disabled).toBeFalse()
    expect(nextPageButton.disabled).toBeTrue()
  }))

  it('should go previous page', fakeAsync(() => {
    gameService.getAll.and.returnValue(of(pages[1]))

    router.navigateByUrl('/?page=2')
    tick()
    fixture.detectChanges()

    gameService.getAll.and.returnValue(of(pages[0]))

    const previousPageButton = fixture.debugElement.nativeElement.querySelector('#previous-page-button')
    const nextPageButton = fixture.debugElement.nativeElement.querySelector('#next-page-button')
    previousPageButton.click()
    tick()
    fixture.detectChanges()

    expect(gameService.getAll).toHaveBeenCalledWith(1, component.pageSize)
    expect(location.isCurrentPathEqualTo('/?page=1')).toBeTrue()
    expect(previousPageButton.disabled).toBeTrue()
    expect(nextPageButton.disabled).toBeFalse()
  }))
})
