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
import {PieceComponent} from './piece/piece.component'

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    GameComponent,
    BoardComponent,
    PieceComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule
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
}
