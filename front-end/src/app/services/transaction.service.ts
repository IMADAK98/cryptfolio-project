import {
  HttpClient,
  HttpContext,
  HttpContextToken,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import {
  BehaviorSubject,
  Observable,
  catchError,
  map,
  of,
  retry,
  tap,
} from 'rxjs';
import {
  AssetResponse,
  DeleteResponse,
  TransactionRequest,
  TransactionResponse,
  UpdateTransactionDto,
} from '../interfaces/crypto.interfaces';
import { customErrorHandler } from '../errors/handleError';
import { CentralizedStateService } from './centralized-state.service';
import { Portfolio } from '../interfaces/portfolio.interface';
import { SkipLoading } from '../loading-indicator/loading-utils/loadin.interceptor';

export interface ITransactionService {
  createTransaction(transaction: TransactionRequest): Observable<boolean>;
  triggerFetch(): void;
}

@Injectable({
  providedIn: 'root',
})
export class TransactionService implements ITransactionService {
  private dataSubject = new BehaviorSubject<void>(undefined);

  data$: Observable<Portfolio | null> | undefined;
  constructor(
    private http: HttpClient,
    private cs: CentralizedStateService,
  ) {}
  url = environment.apiUrl;
  createTransaction(transaction: TransactionRequest): Observable<boolean> {
    return this.http
      .post<TransactionRequest>(
        `${this.url}portfolio-service/api/v1/transaction`,
        transaction,
      )
      .pipe(
        map(() => true),
        tap(() => this.cs.triggerRefresh()),
        catchError((err) => {
          customErrorHandler(err);
          return of(false);
        }),
      );
  }

  fetchTransactionsByAssetId(assetId: number): Observable<AssetResponse> {
    return this.http
      .get<AssetResponse>(
        `${this.url}portfolio-service/api/v1/assets/transactions/${assetId}`,
        { context: new HttpContext().set(SkipLoading, true) },
      )
      .pipe(
        retry({ count: 3, delay: 2000 }),
        catchError((err) => customErrorHandler(err)),
      );
  }

  fetchTransactionById(transactionId: number): Observable<TransactionResponse> {
    return this.http
      .get<TransactionResponse>(
        `${this.url}portfolio-service/api/v1/transaction/${transactionId}`,
        { context: new HttpContext().set(SkipLoading, true) },
      )
      .pipe(
        retry({ count: 3, delay: 2000 }),
        catchError((err) => customErrorHandler(err)),
      );
  }

  updateTransaction(
    input: UpdateTransactionDto,
  ): Observable<TransactionResponse> {
    return this.http
      .put<TransactionResponse>(
        `${this.url}portfolio-service/api/v1/transaction`,
        input,
        { context: new HttpContext().set(SkipLoading, true) },
      )
      .pipe(catchError((err) => customErrorHandler(err)));
  }

  deleteTransactionById(transactionId: number) {
    return this.http
      .delete<DeleteResponse>(
        `${this.url}portfolio-service/api/v1/transaction/${transactionId}`,
      )
      .pipe(
        tap(() => this.cs.triggerRefresh()),
        catchError((err) => customErrorHandler(err)),
      );
  }

  triggerFetch() {
    this.dataSubject.next();
  }
}
