// import { inject } from '@angular/core';
// import {
//   ActivatedRouteSnapshot,
//   ResolveFn,
//   Router,
//   RouterStateSnapshot,
// } from '@angular/router';
// import { PortfolioService } from '../service/portfolio.service';
// import { PortfolioResponse } from '../interfaces/portfolio.interface';
// import { EMPTY, catchError, map, of, tap } from 'rxjs';

// export const portfolioResovler: ResolveFn<PortfolioResponse | null> = (
//   route: ActivatedRouteSnapshot,
//   state: RouterStateSnapshot,
// ) => {
//   console.log('excuting resolver !!');
//   const router = inject(Router);
//   const pService = inject(PortfolioService);

//   // return portfolio.checkPortfolioExistance().pipe(
//   //   tap((portfolio) => {
//   //     if (portfolio === null) {
//   //       router.navigate(['/no-portfolio']);
//   //     }
//   //   }),
//   //   catchError((error) => {
//   //     console.error('Portfolio Resolver Error:', error);
//   //     router.navigate(['/no-portfolio']);
//   //     return of(null); // Wrap the error in an object and return it
//   //   }),
//   // );

//   return pService.checkPortfolioExistance().pipe(
//     tap((portfolio: PortfolioResponse) => {
//       if (portfolio.data !== null || portfolio.data !== undefined) {
//         router.navigate(['/dashboard']);
//         return of(portfolio.data);
//       } else {
//         router.navigate(['/no-portfolio']);
//         return of(null);
//       }
//     }),
//     catchError((error) => {
//       console.error('Portfolio Resolver Error:', error);
//       router.navigate(['/no-portfolio']);
//       return EMPTY;
//     }),
//   );
// };
