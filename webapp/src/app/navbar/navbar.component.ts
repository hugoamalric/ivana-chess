import {Component, OnInit} from '@angular/core'
import {faSignInAlt, faSignOutAlt, faUserAlt} from '@fortawesome/free-solid-svg-icons'
import {User} from '../user'
import {AuthenticationService} from '../authentication.service'
import {catchError, finalize} from 'rxjs/operators'
import {ErrorService} from '../error.service'
import {Router} from '@angular/router'

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
   * True if log-out is pending, false otherwise.
   */
  logOutPending: boolean = false

  /**
   * Initialize component.
   *
   * @param authService Authentication service.
   * @param errorService Error service.
   * @param router Router.
   */
  constructor(
    private authService: AuthenticationService,
    private errorService: ErrorService,
    private router: Router,
  ) {
  }

  /**
   * Log-out.
   */
  logOut(): void {
    this.logOutPending = true
    this.authService.logOut()
      .pipe(
        catchError(error => this.errorService.handleApiError(error)),
        finalize(() => this.logOutPending = false)
      )
      .subscribe(() => this.router.navigate(['/login']))
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
