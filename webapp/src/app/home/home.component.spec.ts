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

  function expectCreated(
    page: Page<Game>,
    previousPageButtonDisabled: boolean,
    nextPageButtonDisabled: boolean
  ) {
    tick()
    fixture.detectChanges()

    const previousPageButton = fixture.debugElement.nativeElement.querySelector('#previous-page-button')
    const nextPageButton = fixture.debugElement.nativeElement.querySelector('#next-page-button')

    expect(component).toBeTruthy()
    expect(gameService.getAll).toHaveBeenCalledWith(page.number, component.pageSize)
    expect(component.page).toEqual(page)
    expect(previousPageButton.disabled).toBe(previousPageButtonDisabled)
    expect(nextPageButton.disabled).toBe(nextPageButtonDisabled)
  }

  function expectPageChanged(
    targetedPage: Page<Game>,
    click: (previousPageButton: HTMLButtonElement, nextPageButton: HTMLButtonElement) => void,
    previousPageButtonDisabled: boolean,
    nextPageButtonDisabled: boolean
  ) {
    const page = pages[1]
    navigateToPage(page)

    gameService.getAll.and.returnValue(of(targetedPage))

    const previousPageButton = fixture.debugElement.nativeElement.querySelector('#previous-page-button')
    const nextPageButton = fixture.debugElement.nativeElement.querySelector('#next-page-button')
    click(previousPageButton, nextPageButton)
    tick()
    fixture.detectChanges()

    expect(gameService.getAll).toHaveBeenCalledWith(targetedPage.number, component.pageSize)
    expect(location.isCurrentPathEqualTo(`/?page=${targetedPage.number}`)).toBeTrue()
    expect(previousPageButton.disabled).toBe(previousPageButtonDisabled)
    expect(nextPageButton.disabled).toBe(nextPageButtonDisabled)
  }

  function navigateToPage(page: Page<Game>) {
    gameService.getAll.and.returnValue(of(page))
    router.navigate([], {
      queryParams: {
        page: page.number
      }
    })
    tick()
    fixture.detectChanges()
  }

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
    expectCreated(pages[0], true, false)
  }))

  it('should create with page params', fakeAsync(() => {
    const page = pages[1]
    navigateToPage(page)
    expectCreated(page, false, false)
  }))

  it('should create new game', fakeAsync(() => {
    const page = pages[1]
    navigateToPage(page)

    gameService.createNewGame.and.returnValue(of(game))

    const previousPageButton = fixture.debugElement.nativeElement.querySelector('#previous-page-button')
    const nextPageButton = fixture.debugElement.nativeElement.querySelector('#next-page-button')
    const newGameButton = fixture.debugElement.nativeElement.querySelector('#new-game-button')
    newGameButton.click()

    expect(gameService.createNewGame).toHaveBeenCalledWith()
    expect(gameService.getAll).toHaveBeenCalledWith(page.number, component.pageSize)
    expect(location.isCurrentPathEqualTo(`/?page=${page.number}`)).toBeTrue()
    expect(previousPageButton.disabled).toBeFalse()
    expect(nextPageButton.disabled).toBeFalse()
  }))

  it('should go next page', fakeAsync(() => {
    expectPageChanged(
      pages[2],
      (previousPageButton, nextPageButton) => nextPageButton.click(),
      false,
      true
    )
  }))

  it('should go previous page', fakeAsync(() => {
    expectPageChanged(
      pages[0],
      (previousPageButton) => previousPageButton.click(),
      true,
      false
    )
  }))
})
