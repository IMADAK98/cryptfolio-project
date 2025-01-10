import { HttpInterceptorFn } from '@angular/common/http';
import { Inject, InjectionToken, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser, isPlatformServer } from '@angular/common';
import { inject } from '@angular/core';
import { of } from 'rxjs';
import { environment } from '../../environments/environment.development';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const platformId: any = inject(PLATFORM_ID);
  const token = isPlatformServer(platformId)
    ? null
    : localStorage.getItem('token');

  const url: string = environment.apiUrl;

  //   Check if the request URL matches the route to exempt
  if (
    req.url === `${url}user-service/api/v1/auth/register` ||
    req.url === `${url}user-service/api/v1/auth/login` ||
    req.url === `${url}user-service/api/v1/user/reset-password-email` ||
    req.url === `${url}user-service/api/v1/user/reset-password-token-check` ||
    req.url === `${url}user-service/api/v1/user/reset-password`
  ) {
    // Return the original request without modifying it
    return next(req);
  }
  // Check if it's an OPTIONS request
  if (req.method === 'OPTIONS') {
    // Return the original request without modifying it
    return next(req);
  }

  if (!token) {
    //prevents race condtions where the request is sent to the server without a token on intilization
    return of();
  } else {
    const authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`),
    });
    // Proceed with the modified or original request
    return next(authReq);
  }
};
