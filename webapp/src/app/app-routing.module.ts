import {NgModule} from '@angular/core'
import {RouterModule, Routes} from '@angular/router'
import {HomeComponent} from './home/home.component'
import {GameComponent} from './game/game.component'
import {LogInComponent} from './login/log-in.component'
import {AnonymousGuard} from './anonymous.guard'
import {SignUpComponent} from './sign-up/sign-up.component'
import {NewGameComponent} from './new-game/new-game.component'
import {ErrorPageComponent} from './error-page/error-page.component'

export const routes: Routes = [
  {
    path: 'login',
    component: LogInComponent,
    canActivate: [AnonymousGuard]
  },
  {
    path: 'signup',
    component: SignUpComponent,
    canActivate: [AnonymousGuard]
  },
  {
    path: 'game/:id',
    component: GameComponent
  },
  {
    path: 'new-game',
    component: NewGameComponent
  },
  {
    path: 'error',
    component: ErrorPageComponent
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
