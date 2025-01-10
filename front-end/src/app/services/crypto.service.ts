import { HttpClient, HttpContext } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  BehaviorSubject,
  Observable,
  catchError,
  map,
  of,
  retry,
  shareReplay,
} from 'rxjs';
import { Coin, CoinResponse } from '../interfaces/crypto.interfaces';
import { customErrorHandler } from '../errors/handleError';
import { SkipLoading } from '../loading-indicator/loading-utils/loadin.interceptor';
import { environment } from '../../environments/environment.development';

@Injectable({
  providedIn: 'root',
})
export class CryptoService {
  private coinsSubject = new BehaviorSubject<Coin[]>([]);

  private coins$!: Observable<Coin[]>;

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {
    this.fetchAllCoinsList().subscribe((coins) => {
      this.coinsSubject.next(coins);
    });
  }

  queryCoin(query: string): Observable<Coin[]> {
    return this.http.get<Coin[]>(
      `${this.apiUrl}exchange-service/api/v1/coins`,
      {
        params: { query },
      },
    );
  }

  fetchPrice(id: string): Observable<CoinResponse> {
    return this.http
      .get<CoinResponse>(`${this.apiUrl}exchange-service/api/v1/price`, {
        params: { coinID: id },
        context: new HttpContext().set(SkipLoading, true),
      })
      .pipe(catchError((err) => customErrorHandler(err)));
  }

  private fetchAllCoinsList(): Observable<Coin[]> {
    return this.http
      .get<Coin[]>(`${this.apiUrl}exchange-service/api/v1/allCoins`)
      .pipe(
        retry({ count: 3, delay: 2000 }),
        catchError((err) => customErrorHandler(err)),
        shareReplay(1),
      );
  }

  getAllCoins(): Observable<Coin[]> {
    return this.coinsSubject
      .asObservable()
      .pipe(map((coin) => coin.filter((coin) => !!coin)));
  }
}
