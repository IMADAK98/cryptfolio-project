import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import {
  BehaviorSubject,
  Observable,
  catchError,
  map,
  of,
  shareReplay,
  tap,
} from 'rxjs';
import { customErrorHandler } from '../errors/handleError';
import {
  Portfolio,
  PortfolioRequest,
  PortfolioResponse,
  StatsResponse,
} from '../interfaces/portfolio.interface';
import { Asset } from '../interfaces/crypto.interfaces';
import { CentralizedStateService } from './centralized-state.service';
import { Router } from '@angular/router';
import { SkipLoading } from '../loading-indicator/loading-utils/loadin.interceptor';

export interface IPortfolioService {
  getPortfolio(): Observable<Portfolio | null>;
  fetchPortfolio(): Observable<Portfolio | null>;
  createPortfolio(
    portfolioReq: PortfolioRequest,
  ): Observable<PortfolioResponse | void>;
  deletePortfolio(portfolioId: number): Observable<void>;
}

@Injectable({
  providedIn: 'root',
})
export class PortfolioService implements IPortfolioService {
  private portfolioSubject = new BehaviorSubject<Portfolio | null>(null);

  apiUrl = environment.apiUrl;
  constructor(
    private http: HttpClient,
    private cs: CentralizedStateService,
    private router: Router,
  ) {
    // this.fetchPortfolio().subscribe((portfolio: Portfolio | null) => {
    //   this.portfolioSubject.next(portfolio);
    // });
  }

  getPortfolioStats(): Observable<StatsResponse> {
    return this.http
      .get<StatsResponse>(
        `${this.apiUrl}portfolio-service/api/v1/portfolio/statistics`,
      )
      .pipe(catchError((err) => customErrorHandler(err)));
  }

  hasPortfolio(): Observable<boolean> {
    return this.http
      .get<PortfolioResponse>(
        `${this.apiUrl}portfolio-service/api/v1/has-portfolio`,
        { context: new HttpContext().set(SkipLoading, true) },
      )
      .pipe(
        map((response: PortfolioResponse) => !!response.data), //!! converts to boolean
        catchError((err) => {
          if (err.status === 404) {
            console.log(err);
            return of(false);
          } else {
            customErrorHandler(err);
            return of(false);
          }
        }),
      );
  }

  // Fetches portfolio data from the backend and returns an observable
  fetchPortfolio(): Observable<Portfolio | null> {
    return this.http
      .get<PortfolioResponse>(
        `${this.apiUrl}portfolio-service/api/v1/portfolio-by-email`,
        { context: new HttpContext().set(SkipLoading, true) },
      )
      .pipe(
        map((response: PortfolioResponse) =>
          this.mapToPortfolio(response.data),
        ),
        catchError((err) => {
          customErrorHandler(err);
          return of(null); // Return null in case of an error
        }),
        shareReplay(1),
      );
  }

  // Method to trigger portfolio data update
  triggerUpdatePortfolioData(): void {
    this.fetchPortfolio().subscribe({
      next: (portfolio) => this.portfolioSubject.next(portfolio),
      error: (error: any) => customErrorHandler(error),
    });
  }

  createPortfolio(
    portfolioReq: PortfolioRequest,
  ): Observable<PortfolioResponse | void> {
    return this.http
      .post<PortfolioResponse>(
        `${this.apiUrl}portfolio-service/api/v1/portfolio`,
        portfolioReq,
      )
      .pipe(
        tap((response: PortfolioResponse) => {
          if (response.data) {
            this.portfolioSubject.next(this.mapToPortfolio(response.data));
          } else {
            this.portfolioSubject.next(null);
          }
        }),
        catchError((err) => customErrorHandler(err)),
      );
  }

  getPortfolio(): Observable<Portfolio | null> {
    return this.portfolioSubject.asObservable();
  }

  deletePortfolio(portfolioId: number): Observable<void> {
    return this.http
      .delete<void>(
        `${this.apiUrl}portfolio-service/api/v1/portfolio/${portfolioId}`,
      )
      .pipe(catchError((error: any) => customErrorHandler(error)));
  }

  private mapToPortfolio(data: Portfolio): Portfolio {
    return {
      id: data.id,
      portfolioName: data.portfolioName,
      userEmail: data.userEmail,
      assets: data.assets.map((asset: Asset) => this.mapToAsset(asset)),
      creationDate: new Date(data.creationDate),
      totalValue: data.totalValue,
      totalProfitAndLossAmount: data.totalProfitAndLossAmount,
      totalCapitalInvested: data.totalCapitalInvested,
      totalPlPercent: data.totalPlPercent,
    };
  }

  private mapToAsset(assetData: Asset): Asset {
    return {
      id: assetData.id,
      symbol: assetData.symbol,
      coinId: assetData.coinId,
      avgBuyPrice: assetData.avgBuyPrice,
      currentPrice: assetData.currentPrice,
      name: assetData.name,
      totalValue: assetData.totalValue,
      totalQuantity: assetData.totalQuantity,
      realizedProfitLossAmount: assetData.realizedProfitLossAmount,
      unrealizedProfitLossAmount: assetData.unrealizedProfitLossAmount,
      totalProfitAndLossAmount: assetData.totalProfitAndLossAmount,
      purchaseDate: new Date(assetData.purchaseDate),
      transactions: assetData.transactions,
    };
  }
}
