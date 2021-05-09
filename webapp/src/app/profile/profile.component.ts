import {Component, OnInit} from '@angular/core'
import {User} from '../user'
import {AuthenticationService} from '../authentication.service'
import {catchError, debounceTime, finalize, mergeMap, switchMap, tap} from 'rxjs/operators'
import {ActivatedRoute, Router} from '@angular/router'
import {UserService} from '../user.service'
import {NgbModal} from '@ng-bootstrap/ng-bootstrap'
import {ConfirmModalComponent} from '../confirm-modal/confirm-modal.component'
import {Role} from '../role.enum'
import {Observable, of} from 'rxjs'
import {AbstractControl, FormControl, FormGroup, ValidationErrors, Validators} from '@angular/forms'
import {PasswordMinLength} from '../../../constants'
import {ErrorService} from '../error.service'
import {SuccessMessageService} from '../success-message.service'
import {SuccessMessageCode} from '../success-message-code.enum'

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
   * True if update is pending, false otherwise.
   */
  updatePending: boolean = false

  /**
   * True if deletion is pending, false otherwise.
   */
  deletionPending: boolean = false

  /**
   * Update email form.
   */
  updateEmailForm = new FormGroup({
    email: new FormControl('', Validators.required),
  })

  /**
   * Update password form.
   */
  updatePasswordForm = new FormGroup(
    {
      password: new FormControl('', [Validators.required, Validators.minLength(PasswordMinLength)]),
      passwordConfirmation: new FormControl('', [Validators.required]),
    },
    [ProfileComponent.checkPasswords]
  )

  /**
   * True if email is checking, false otherwise.
   */
  checkingEmail: boolean = false

  /**
   * True if email is already used, false if it is not, null if it is uncheck.
   */
  emailExists: boolean | null = null

  /**
   * Role enumeration.
   */
  Role = Role

  /**
   * Password minimal length.
   */
  PasswordMinLength = PasswordMinLength

  /**
   * Check if password confirmation is same as password.
   *
   * @param control Form.
   * @return ValidationErrors|null Validation errors if password confirmation is not same as password, null otherwise.
   */
  private static checkPasswords(control: AbstractControl): ValidationErrors | null {
    const valid = control.get('password')?.value === control.get('passwordConfirmation')?.value
    return valid ? null : {passwordConfirmation: 'invalid'}
  }

  /**
   * Initialize component.
   *
   * @param userService User service.
   * @param authService Authentication service.
   * @param errorService Error service.
   * @param successMessageService Success message service.
   * @param router Router.
   * @param route Current route.
   * @param modalService Modal service.
   */
  constructor(
    private userService: UserService,
    private authService: AuthenticationService,
    private errorService: ErrorService,
    private successMessageService: SuccessMessageService,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal,
  ) {
  }

  /**
   * Get email form control.
   *
   * @return AbstractControl Form control.
   */
  get email(): AbstractControl {
    return this.updateEmailForm.get('email')!!
  }

  /**
   * Get password form control.
   *
   * @return AbstractControl Form control.
   */
  get password(): AbstractControl {
    return this.updatePasswordForm.get('password')!!
  }

  /**
   * Get password confirmation form control.
   *
   * @return AbstractControl Form control.
   */
  get passwordConfirmation(): AbstractControl {
    return this.updatePasswordForm.get('passwordConfirmation')!!
  }

  ngOnInit(): void {
    this.authService.me().subscribe(user => this.me = user)
    this.route.paramMap.subscribe(params => {
      const id = params.get('id')!!
      this.userService.get(id)
        .pipe(catchError(error => this.errorService.handleApiError<User>(error)))
        .subscribe(user => {
          this.user = user
          if (user.email !== undefined) {
            this.email.setValue(user.email)
          }
        })
      this.email.valueChanges
        .pipe(
          debounceTime(300),
          tap(() => {
            this.checkingEmail = true
            this.emailExists = null
          }),
          switchMap(email =>
            this.userService.existsWithEmail(email, [id])
              .pipe(finalize(() => this.checkingEmail = false))
          )
        )
        .subscribe(exists => this.emailExists = exists)
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

  /**
   * Update email.
   */
  updateEmail(): void {
    this.update(SuccessMessageCode.EmailUpdated, () => this.userService.selfUpdate(this.email.value))
  }

  /**
   * Update password.
   */
  updatePassword(): void {
    this.update(SuccessMessageCode.PasswordUpdated, () => this.userService.selfUpdate(null, this.password.value))
  }

  /**
   * Update user.
   *
   * @param code Success message code.
   * @param updateFn Function to call.
   */
  private update(code: SuccessMessageCode, updateFn: () => Observable<User>): void {
    this.updatePending = true
    updateFn()
      .pipe(
        catchError(error => this.errorService.handleApiError<User>(error)),
        finalize(() => this.updatePending = false),
      )
      .subscribe(user => {
        this.user = user
        this.successMessageService.sendCode(code)
        this.password.reset('')
        this.passwordConfirmation.reset('')
      })
  }
}
