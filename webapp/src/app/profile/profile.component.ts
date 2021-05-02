import {Component, OnInit} from '@angular/core'
import {User} from '../user'
import {AuthenticationService} from '../authentication.service'
import {catchError, finalize, mergeMap} from 'rxjs/operators'
import {ActivatedRoute, Router} from '@angular/router'
import {UserService} from '../user.service'
import {ErrorService} from '../error.service'
import {NgbModal} from '@ng-bootstrap/ng-bootstrap'
import {ConfirmModalComponent} from '../confirm-modal/confirm-modal.component'
import {Role} from '../role.enum'
import {of} from 'rxjs'

/**
 * Profile component.
 */
@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  /**
   * Current authenticated user.
   */
  me: User | null = null

  /**
   * User.
   */
  user: User | null = null

  /**
   * True if deletion is pending, false otherwise.
   */
  deletionPending: boolean = false

  /**
   * Role enumeration.
   */
  Role = Role

  /**
   * Initialize component.
   *
   * @param userService User service.
   * @param authService Authentication service.
   * @param errorService Error service.
   * @param router Router.
   * @param route Current route.
   * @param modalService Modal service.
   */
  constructor(
    private userService: UserService,
    private authService: AuthenticationService,
    private errorService: ErrorService,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal,
  ) {
  }

  ngOnInit(): void {
    this.authService.me().subscribe(user => this.me = user)
    this.route.paramMap.subscribe(params => {
      const id = params.get('id')
      this.userService.get(id!!)
        .pipe(catchError(error => this.errorService.handleApiError<User>(error)))
        .subscribe(user => this.user = user)
    })
  }

  openConfirmWindow(): void {
    this.modalService.open(ConfirmModalComponent).result.then(confirmed => {
      if (confirmed) {
        this.deletionPending = true
        this.userService.delete(this.user!!.id)
          .pipe(
            mergeMap(() => {
              if (this.user!!.id === this.me?.id) {
                return this.authService.logOut()
              } else {
                return of()
              }
            }),
            catchError(error => this.errorService.handleApiError<void>(error)),
            finalize(() => this.deletionPending = false),
          )
          .subscribe(() => this.router.navigate(['/']))
      }
    })
  }
}
