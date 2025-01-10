import { isPlatformServer } from '@angular/common';
import { inject, PLATFORM_ID } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
) => {
  if (isPlatformServer(inject(PLATFORM_ID))) {
    return false;
  }
  const authService: AuthService = inject(AuthService);
  const router: Router = inject(Router);

  if (authService.isLoggedIn()) return true;

  const navigation = router.getCurrentNavigation();
  const redirectTo = navigation?.extras.state?.['redirectTo'];

  const targetRoute = redirectTo === 'signup' ? '/signup' : '/login';

  return router.createUrlTree([targetRoute], {
    queryParams: { returnUrl: state.url },
  });
};
