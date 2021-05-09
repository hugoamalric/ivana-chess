import {Component} from '@angular/core'
import {Version} from './version'
import {faGithub} from '@fortawesome/free-brands-svg-icons'

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

  /**
   * GitHub icon.
   */
  githubIcon = faGithub
}
