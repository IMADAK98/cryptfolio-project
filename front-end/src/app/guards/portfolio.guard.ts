import { CanActivateFn, Router } from '@angular/router';
import { inject, PLATFORM_ID } from '@angular/core';
import { tap, map } from 'rxjs';
import { isPlatformServer } from '@angular/common';
import { PortfolioService } from '../services/portfolio.service';

export const portfolioGuard: CanActivateFn = (route, state) => {
  const pService = inject(PortfolioService);
  const router = inject(Router);

  // if (isPlatformServer(inject(PLATFORM_ID))) {
  //   return false;
  // }

  return pService.hasPortfolio().pipe(
    map((portfolioExists) => {
      if (!portfolioExists) {
        router.navigateByUrl('/no-portfolio');
        return false;
      }
      return true;
    }),
  );
};
