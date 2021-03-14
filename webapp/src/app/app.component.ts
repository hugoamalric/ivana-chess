import {Component} from '@angular/core'
import {Version} from './version'

/**
 * Root component.
 */
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  /**
   * Application version.
   */
  version = Version
}
