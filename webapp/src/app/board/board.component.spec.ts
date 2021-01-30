import {ComponentFixture, TestBed} from '@angular/core/testing'

import {BoardComponent} from './board.component'

declare var require: any

describe('BoardComponent', () => {
  const game = require('test/game/initial.json')

  let component: BoardComponent
  let fixture: ComponentFixture<BoardComponent>

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BoardComponent]
    })
      .compileComponents()
  })

  beforeEach(() => {
    fixture = TestBed.createComponent(BoardComponent)
    component = fixture.componentInstance
    component.pieces = game.pieces
    fixture.detectChanges()
  })

  it('should create', () => {
    expect(component).toBeTruthy()
  })

  it('should return symbol', () => {
    expect(component.symbolAt(1, 1)).toEqual('♖')
    expect(component.symbolAt(2, 1)).toEqual('♘')
    expect(component.symbolAt(3, 1)).toEqual('♗')
    expect(component.symbolAt(4, 1)).toEqual('♕')
    expect(component.symbolAt(5, 1)).toEqual('♔')
    expect(component.symbolAt(6, 1)).toEqual('♗')
    expect(component.symbolAt(7, 1)).toEqual('♘')
    expect(component.symbolAt(8, 1)).toEqual('♖')
    Array.from(Array(8).keys()).map(col => {
      expect(component.symbolAt(col + 1, 2)).toEqual('♙')
    })
    Array.from(Array(8).keys()).map(col =>
      Array.from(Array(4).keys()).map(row => {
        expect(component.symbolAt(col + 1, row + 3)).toEqual(' ')
      })
    )
    expect(component.symbolAt(1, 8)).toEqual('♜')
    expect(component.symbolAt(2, 8)).toEqual('♞')
    expect(component.symbolAt(3, 8)).toEqual('♝')
    expect(component.symbolAt(4, 8)).toEqual('♛')
    expect(component.symbolAt(5, 8)).toEqual('♚')
    expect(component.symbolAt(6, 8)).toEqual('♝')
    expect(component.symbolAt(7, 8)).toEqual('♞')
    expect(component.symbolAt(8, 8)).toEqual('♜')
    Array.from(Array(8).keys()).map(col => {
      expect(component.symbolAt(col + 1, 7)).toEqual('♟')
    })
  })
})
