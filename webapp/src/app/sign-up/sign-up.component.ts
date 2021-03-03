import {Component, OnInit} from '@angular/core'
import {AbstractControl, FormControl, FormGroup, ValidationErrors, Validators} from '@angular/forms'
import {UserService} from '../user.service'
import {UserCreation} from '../user-creation'
import {Router} from '@angular/router'
import {handleApiError} from '../utils'
import {HttpErrorResponse} from '@angular/common/http'
import {ApiErrorCode} from '../api-error-code.enum'
import {ApiError} from '../api-error'

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
   * Sign-up form.
   */
  signUpForm = new FormGroup(
    {
      pseudo: new FormControl('', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]),
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required, Validators.minLength(5)]),
      passwordConfirmation: new FormControl('', [Validators.required])
    },
    [SignUpComponent.checkPasswords]
  )

  /**
   * API error code.
   */
  errorCode: ApiErrorCode | null = null

  /**
   * API error code enumeration.
   */
  ApiErrorCode = ApiErrorCode

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
   * @param router Router.
   */
  constructor(
    private userService: UserService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
  }

  /**
   * Submit sign-up form.
   */
  signUp(): void {
    const userCreation = this.signUpForm.value as UserCreation
    this.userService.signUp(userCreation)
      .subscribe(
        () => this.router.navigate(['/login']).then(),
        (errorResponse: HttpErrorResponse) => {
          const error = errorResponse.error as ApiError
          if (error.code === ApiErrorCode.EmailAlreadyUsed || error.code === ApiErrorCode.PseudoAlreadyUsed) {
            this.errorCode = error.code
          } else {
            this.errorCode = ApiErrorCode.Unknown
            handleApiError(errorResponse, this.router)
          }
        }
      )
  }
}
