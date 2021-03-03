import {NgModule} from '@angular/core'
import {BrowserModule} from '@angular/platform-browser'

import {AppRoutingModule} from './app-routing.module'
import {AppComponent} from './app.component'
import {HomeComponent} from './home/home.component'
import {HttpClientModule} from '@angular/common/http'
import {GameComponent} from './game/game.component'
import {BoardComponent} from './board/board.component'
import {InjectableRxStompConfig, RxStompService, rxStompServiceFactory} from '@stomp/ng2-stompjs'
import {StompConfig} from './stomp-configuration'
import {PositionComponent} from './position/position.component'
import {PromotionButtonComponent} from './promotion-button/promotion-button.component'
import {PieceImageComponent} from './piece-image/piece-image.component'
import {LogInComponent} from './login/log-in.component'
import {ReactiveFormsModule} from '@angular/forms'
import {AuthenticationService} from './authentication.service'
import {HistoryService} from './history.service'
import {SignUpComponent} from './sign-up/sign-up.component'

/**
 * Ivana Chess module.
 */
@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    GameComponent,
    BoardComponent,
    PositionComponent,
    PromotionButtonComponent,
    PieceImageComponent,
    LogInComponent,
    SignUpComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule
  ],
  providers: [
    {
      provide: InjectableRxStompConfig,
      useValue: StompConfig
    },
    {
      provide: RxStompService,
      useFactory: rxStompServiceFactory,
      deps: [InjectableRxStompConfig]
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
  /**
   * Initialize module.
   *
   * @param authService Authentication service.
   * @param historyService History service.
   */
  constructor(
    private authService: AuthenticationService,
    private historyService: HistoryService
  ) {
  }
}
