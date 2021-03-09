import {Component, OnInit} from '@angular/core'
import {faSignInAlt, faSignOutAlt, faUserAlt} from '@fortawesome/free-solid-svg-icons'
import {User} from '../user'
import {AuthenticationService} from '../authentication.service'

/**
 * Navigation bar component.
 */
@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  /**
   * True if menu is collapsed, false otherwise.
   */
  menuIsCollapsed: boolean = true

  /**
   * Log-in icon.
   */
  logInIcon = faSignInAlt

  /**
   * Log-out icon.
   */
  logOutIcon = faSignOutAlt

  /**
   * User icon.
   */
  userIcon = faUserAlt

  /**
   * Current authenticated user.
   */
  me: User | null = null

  /**
   * Initialize component.
   *
   * @param authService Authentication service.
   */
  constructor(
    private authService: AuthenticationService
  ) {
  }

  /**
   * Log-out.
   */
  logOut(): void {
    this.authService.logOut().subscribe()
  }

  ngOnInit(): void {
    this.authService.me().subscribe(user => this.me = user)
  }

  /**
   * Open/close menu.
   */
  toggleMenu(): void {
    this.menuIsCollapsed = !this.menuIsCollapsed
  }

}
