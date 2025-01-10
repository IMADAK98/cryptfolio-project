import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, tap } from 'rxjs';
import { TokenRequest } from '../interfaces/auth.interfaces';
import { AuthService } from '../auth/auth.service';

export const resetPasswordGuard: CanActivateFn = (route, state) => {
  const token = route.queryParamMap.get('token');
  const router = inject(Router);
  const authService = inject(AuthService);
  if (!token) {
    return router.createUrlTree(['/']);
  }

  const req: TokenRequest = {
    token: token,
  };

  return authService.checkResetPasswordToken(req).pipe(
    map((isValid: boolean) => {
      if (isValid) {
        router.createUrlTree(['/reset-form']);
        return true;
      } else {
        return router.createUrlTree(['/']);
      }
    }),
  );
};
