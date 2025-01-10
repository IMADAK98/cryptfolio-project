import { Component, Inject, Input, PLATFORM_ID, inject } from '@angular/core';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { Observable, filter, tap } from 'rxjs';
import { LoadingService } from './loading-utils/loading.service';
import { AsyncPipe, isPlatformBrowser } from '@angular/common';
import {
  NavigationCancel,
  NavigationEnd,
  NavigationError,
  NavigationStart,
  RouteConfigLoadEnd,
  RouteConfigLoadStart,
  Router,
} from '@angular/router';

@Component({
  selector: 'loading-indicator',
  standalone: true,
  imports: [ProgressSpinnerModule, AsyncPipe],
  templateUrl: './loading-indicator.component.html',
  styleUrl: './loading-indicator.component.css',
})
export class LoadingIndicatorComponent {
  @Input()
  detectRouteTransitions = false;

  loading$: Observable<boolean>;
  platformId: any = inject(PLATFORM_ID);
  constructor(
    private loadingService: LoadingService,
    private router: Router,
  ) {
    this.loading$ = this.loadingService.loading$;
  }

  ngOnInit(): void {
    if (this.detectRouteTransitions) {
      this.router.events
        .pipe(
          filter(() => isPlatformBrowser(this.platformId)), // Check platform before tap
          tap((event) => {
            if (event instanceof NavigationStart) {
              this.loadingService.loadingOn();
            } else if (
              event instanceof NavigationEnd ||
              event instanceof NavigationCancel ||
              event instanceof NavigationError
            ) {
              setTimeout(() => {
                this.loadingService.loadingOff();
              }, 500);
            }
          }),
        )
        .subscribe();
    }
  }
}
