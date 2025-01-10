import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Asset, AssetResponse } from '../interfaces/crypto.interfaces';
import {
  BehaviorSubject,
  Observable,
  catchError,
  combineLatest,
  merge,
  retry,
  switchMap,
  tap,
} from 'rxjs';
import { customErrorHandler } from '../errors/handleError';
import { PortfolioService } from './portfolio.service';
import { environment } from '../../environments/environment.development';
import { CentralizedStateService } from './centralized-state.service';
import { Portfolio } from '../interfaces/portfolio.interface';
import { count } from 'console';

export interface IAssetService {
  fetchAssetsByPortfolioId(portfolioId: number): Observable<Asset[]>;
  deleteAsset(assetId: number): Observable<void>;
  triggerFetch(): void;
}

@Injectable({
  providedIn: 'root',
})
export class AssetService implements IAssetService {
  private dataSubject = new BehaviorSubject<void>(undefined);
  data$: Observable<Portfolio | null>;
  constructor(
    private http: HttpClient,
    private ps: PortfolioService,
    private cs: CentralizedStateService,
  ) {
    this.data$ = merge(this.dataSubject, this.cs.refresh$).pipe(
      switchMap(() => this.ps.getPortfolio()),
    );
  }
  apiurl = environment.apiUrl;
  fetchAssetsByPortfolioId(portfolioId: number) {
    return this.http
      .get<
        Asset[]
      >(`${this.apiurl}portfolio-service/api/v1/assets/portfolio/${portfolioId}`)
      .pipe(
        retry({ count: 3, delay: 2000 }),
        catchError((err) => customErrorHandler(err)),
      );
  }

  deleteAsset(assetId: number) {
    return this.http
      .delete<void>(`${this.apiurl}portfolio-service/api/v1/assets/${assetId}`)
      .pipe(
        tap(() => this.cs.triggerRefresh()),
        catchError((err) => customErrorHandler(err)),
      );
  }

  triggerFetch() {
    this.dataSubject.next();
  }
}
