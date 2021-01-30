import {NgModule} from '@angular/core'
import {RouterModule, Routes} from '@angular/router'
import {HomeComponent} from './home/home.component'
import {GameComponent} from './game/game.component'

export const routes: Routes = [
  {
    path: 'game/:id',
    component: GameComponent
  },
  {
    path: '',
    component: HomeComponent
  }
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
