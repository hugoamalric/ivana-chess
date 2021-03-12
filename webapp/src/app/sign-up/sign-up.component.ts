import {Component, OnInit} from '@angular/core'
import {AbstractControl, FormControl, FormGroup, ValidationErrors, Validators} from '@angular/forms'
import {UserService} from '../user.service'
import {UserCreation} from '../user-creation'
import {Router} from '@angular/router'
import {ApiErrorCode} from '../api-error-code.enum'
import {catchError, finalize} from 'rxjs/operators'
import {ErrorService} from '../error.service'

/**
 * Sign-up component.
 */
@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.scss']
})
export class SignUpComponent implements OnInit {
  /**
   * Pseudo minimal length.
   */
  readonly pseudoMinLength = 3

  /**
   * Pseudo maximal length.
   */
  readonly pseudoMaxLength = 50

  /**
   * Password minimal length.
   */
  readonly passwordMinLength = 5

  /**
   * Sign-up form.
   */
  signUpForm = new FormGroup(
    {
      pseudo: new FormControl('', [
        Validators.required,
        Validators.minLength(this.pseudoMinLength),
        Validators.maxLength(this.pseudoMaxLength)
      ]),
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required, Validators.minLength(this.passwordMinLength)]),
      passwordConfirmation: new FormControl('', [Validators.required])
    },
    [SignUpComponent.checkPasswords]
  )

  /**
   * API error code.
   */
  errorCode: ApiErrorCode | null = null

  /**
   * True if sign-up is pending, false otherwise.
   */
  signUpPending: boolean = false

  /**
   * Check if password confirmation is same as password.
   *
   * @param control Form.
   * @return Validation errors if password confirmation is not same as password, null otherwise.
   * @private
   */
  private static checkPasswords(control: AbstractControl): ValidationErrors | null {
    const valid = control.get('password')?.value === control.get('passwordConfirmation')?.value
    return valid ? null : {passwordConfirmation: 'invalid'}
  }

  /**
   * Initialize component.
   *
   * @param userService User service.
   * @param errorService Error service.
   * @param router Router.
   */
  constructor(
    private userService: UserService,
    private errorService: ErrorService,
    private router: Router
  ) {
  }

  /**
   * Get email form control.
   *
   * @return Form control.
   */
  get email(): AbstractControl {
    return this.signUpForm.get('email')!!
  }

  /**
   * Get password form control.
   *
   * @return Form control.
   */
  get password(): AbstractControl {
    return this.signUpForm.get('password')!!
  }

  /**
   * Get password confirmation form control.
   *
   * @return Form control.
   */
  get passwordConfirmation(): AbstractControl {
    return this.signUpForm.get('passwordConfirmation')!!
  }

  /**
   * Get pseudo form control.
   *
   * @return Form control.
   */
  get pseudo(): AbstractControl {
    return this.signUpForm.get('pseudo')!!
  }

  ngOnInit(): void {
  }

  /**
   * Submit sign-up form.
   */
  signUp(): void {
    this.signUpPending = true
    const userCreation = this.signUpForm.value as UserCreation
    this.userService.signUp(userCreation)
      .pipe(
        catchError(error => this.errorService.handleApiError(error)),
        finalize(() => this.signUpPending = false)
      )
      .subscribe(() => this.router.navigate(['/login']).then())
  }
}
