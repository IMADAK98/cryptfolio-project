import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs';
import { PortfolioService } from '../services/portfolio.service';

export const noPortfolioGuard: CanActivateFn = (route, state) => {
  const pService = inject(PortfolioService);
  const router = inject(Router);

  // if (isPlatformServer(inject(PLATFORM_ID))) {
  //   return false;
  // }

  return pService.hasPortfolio().pipe(
    map((portfolioExists) => {
      if (portfolioExists) {
        router.navigate(['/dashboard']);
        return false;
      }
      return true;
    }),
  );
};
