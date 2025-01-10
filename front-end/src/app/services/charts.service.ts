import { HttpClient, HttpContext, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import {
  BehaviorSubject,
  Observable,
  catchError,
  combineLatest,
  merge,
  shareReplay,
  switchMap,
} from 'rxjs';
import { customErrorHandler } from '../errors/handleError';
import {
  BarChartResponse,
  ChartTypeParams,
  LineChartParams,
  PiChartResponse,
  SnapshotResponse,
} from '../interfaces/charts.interfaces';
import { CentralizedStateService } from './centralized-state.service';
import { SkipLoading } from '../loading-indicator/loading-utils/loadin.interceptor';

export interface IChartsService {
  getPieChart(): Observable<PiChartResponse>;
  getBarChartData(input?: ChartTypeParams): Observable<BarChartResponse>;
  getLineChartData(): Observable<SnapshotResponse>;
  triggerFetch(): void;
}

@Injectable({
  providedIn: 'root',
})
export class ChartsService implements IChartsService {
  private readonly url: string = environment.apiUrl;
  private readonly refreshTrigger$ = new BehaviorSubject<void>(undefined);
  private readonly lineChartFilter$ = new BehaviorSubject<LineChartParams>(
    LineChartParams.DAY,
  );

  private readonly sharedRefresh$ = merge(
    this.refreshTrigger$,
    this.cs.refresh$,
  ).pipe(shareReplay(1));

  readonly pieChart$ = this.createChartObservable(() => this.fetchPieChart());
  readonly barChart$ = this.createChartObservable(() =>
    this.fetchBarChartData(),
  );
  readonly lineChart$ = this.createLineChartObservable();

  constructor(
    private http: HttpClient,
    private cs: CentralizedStateService,
  ) {}

  private createChartObservable<T>(
    fetchFn: () => Observable<T>,
  ): Observable<T> {
    return this.sharedRefresh$.pipe(
      switchMap(() => fetchFn()),
      shareReplay(1),
    );
  }

  private createLineChartObservable(): Observable<SnapshotResponse> {
    return combineLatest([this.sharedRefresh$, this.lineChartFilter$]).pipe(
      switchMap(([_, filter]) => this.fetchLineChartData(filter)),
      shareReplay(1),
    );
  }
  private fetchPieChart(): Observable<PiChartResponse> {
    return this.http
      .get<PiChartResponse>(
        `${this.url}portfolio-service/api/v1/charts/pie-chart`,
      )
      .pipe(catchError(customErrorHandler));
  }

  // private fetchLineChartData(
  //   params: LineChartParams,
  // ): Observable<SnapshotResponse> {
  //   return this.http
  //     .get<SnapshotResponse>(
  //       `${this.url}portfolio-service/api/v1/charts/line-chart-snapshots`,
  //       { params: { params } },
  //     )
  //     .pipe(catchError(customErrorHandler));
  // }
  private fetchLineChartData(
    params: LineChartParams,
  ): Observable<SnapshotResponse> {
    return this.http
      .get<SnapshotResponse>(
        `${this.url}portfolio-service/api/v1/charts/line-chart-snapshots`,
        {
          params: { period: params },
          context: new HttpContext().set(SkipLoading, true),
        },
      )
      .pipe(catchError(customErrorHandler));
  }

  private fetchBarChartData(
    input?: ChartTypeParams,
  ): Observable<BarChartResponse> {
    let params = new HttpParams();
    if (input) {
      params = params.append('input', input);
    }
    return this.http
      .get<BarChartResponse>(
        `${this.url}portfolio-service/api/v1/charts/bar-chart`,
        { params },
      )
      .pipe(catchError(customErrorHandler));
  }

  getPieChart(): Observable<PiChartResponse> {
    return this.pieChart$;
  }

  getBarChartData(input?: ChartTypeParams): Observable<BarChartResponse> {
    return this.barChart$;
  }

  getLineChartData(): Observable<SnapshotResponse> {
    return this.lineChart$;
  }

  triggerFetch(): void {
    this.refreshTrigger$.next();
  }

  updateLineChartFilter(filter: LineChartParams): void {
    this.lineChartFilter$.next(filter);
  }
}
