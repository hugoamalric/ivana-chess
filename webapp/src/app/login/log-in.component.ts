import {Component, OnInit} from '@angular/core'
import {AbstractControl, FormControl, FormGroup, Validators} from '@angular/forms'
import {Credentials} from '../credentials'
import {AuthenticationService} from '../authentication.service'
import {handleApiError} from '../utils'
import {HttpErrorResponse} from '@angular/common/http'
import {Router} from '@angular/router'
import {ApiErrorCode} from '../api-error-code.enum'
import {ApiError} from '../api-error'
import {finalize} from 'rxjs/operators'

/**
 * Log-in component.
 */
@Component({
  selector: 'app-login',
  templateUrl: './log-in.component.html',
  styleUrls: ['./log-in.component.scss']
})
export class LogInComponent implements OnInit {
  /**
   * Log-in form.
   */
  logInForm = new FormGroup({
    pseudo: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required)
  })

  /**
   * API error code.
   */
  errorCode: ApiErrorCode | null = null

  /**
   * API error code enumeration.
   */
  ApiErrorCode = ApiErrorCode

  /**
   * True if log-in is pending, false otherwise.
   */
  logInPending: boolean = false

  /**
   * Initialize component.
   *
   * @param authService Authentication service.
   * @param router Router.
   */
  constructor(
    private authService: AuthenticationService,
    private router: Router
  ) {
  }

  /**
   * Get password form control.
   *
   * @return Form control.
   */
  get password(): AbstractControl {
    return this.logInForm.get('password')!!
  }

  /**
   * Get pseudo form control.
   *
   * @return Form control.
   */
  get pseudo(): AbstractControl {
    return this.logInForm.get('pseudo')!!
  }

  /**
   * Submit log-in form.
   */
  logIn(): void {
    this.logInPending = true
    const creds = this.logInForm.value as Credentials
    this.authService.logIn(creds)
      .pipe(finalize(() => this.logInPending = false))
      .subscribe(
        () => this.router.navigate(['/']),
        (errorResponse: HttpErrorResponse) => {
          const error = errorResponse.error as ApiError
          if (error.code === ApiErrorCode.Unauthorized) {
            this.errorCode = error.code
          } else {
            this.errorCode = ApiErrorCode.Unknown
            handleApiError(errorResponse, this.router)
          }
        }
      )
  }

  ngOnInit(): void {
  }

}
