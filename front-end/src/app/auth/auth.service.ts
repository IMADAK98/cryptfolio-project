import { HttpClient } from '@angular/common/http';
import {
  Inject,
  Injectable,
  PLATFORM_ID,
  afterNextRender,
} from '@angular/core';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import { catchError, map } from 'rxjs';
import { customErrorHandler } from '../errors/handleError';
import { environment } from '../../environments/environment.development';
import {
  ResetEmailRequest,
  ResetPasswordRequest,
  TokenRequest,
  UserLogin,
  UserSignup,
} from '../interfaces/auth.interfaces';
import { isPlatformBrowser, isPlatformServer } from '@angular/common';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private localStorage: any;
  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private http: HttpClient,
    private router: Router,
  ) {
    this.localStorage = isPlatformBrowser(this.platformId)
      ? window.localStorage
      : null;
  }

  apiUrl = environment.apiUrl;

  signUp(user: UserSignup) {
    return this.http
      .post<any>(`${this.apiUrl}user-service/api/v1/auth/register`, user)
      .pipe(
        map((response: any) => {
          if (response && response.token) {
            this.localStorage.setItem('token', response.token);
            return true;
          }
          return false;
        }),
        catchError((err) => customErrorHandler(err)),
      );
  }

  logIn(user: UserLogin) {
    return this.http
      .post<any>(`${this.apiUrl}user-service/api/v1/auth/login`, user)
      .pipe(
        map((response) => {
          if (response && response.token) {
            this.localStorage.setItem('token', response.token);
            return true;
          }
          return false;
        }),
        catchError((err) => customErrorHandler(err)), // Correct placement of catchError
      );
  }

  sendResetPasswordEmail(req: ResetEmailRequest) {
    return this.http
      .post<any>(
        `${this.apiUrl}user-service/api/v1/user/reset-password-email`,
        req,
      )
      .pipe(catchError((err) => customErrorHandler(err)));
  }

  checkResetPasswordToken(req: TokenRequest) {
    return this.http
      .post<boolean>(
        `${this.apiUrl}user-service/api/v1/user/reset-password-token-check`,
        req,
      )
      .pipe(catchError((err) => customErrorHandler(err)));
  }

  resetPassword(request: ResetPasswordRequest) {
    return this.http
      .post<string>(
        `${this.apiUrl}user-service/api/v1/user/reset-password`,
        request,
      )
      .pipe(catchError((err) => customErrorHandler(err)));
  }

  logout() {
    this.localStorage.removeItem('token');
    this.router.navigateByUrl('/');
  }

  public isLoggedIn(): boolean {
    let jwtHelper = new JwtHelperService();
    let token: string | null = null;

    if (this.localStorage) {
      token = this.localStorage.getItem('token');
    }

    if (!token) {
      return false;
    }

    let expirationDate = jwtHelper.getTokenExpirationDate(token);
    let isExpired = jwtHelper.isTokenExpired(token);
    return !isExpired;
  }
}
