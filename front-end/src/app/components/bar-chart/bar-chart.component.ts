import {
  Component,
  Inject,
  OnDestroy,
  OnInit,
  PLATFORM_ID,
} from '@angular/core';
import { ChartModule } from 'primeng/chart';
import { ChartsService } from '../../services/charts.service';
import {
  BarChartDto,
  BarChartResponse,
} from '../../interfaces/charts.interfaces';
import { Subject, takeUntil } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'bar-chart',
  standalone: true,
  imports: [ChartModule],
  templateUrl: './bar-chart.component.html',
  styleUrl: './bar-chart.component.css',
})
export class BarChartComponent implements OnInit, OnDestroy {
  basicData: any;
  basicOptions: any;
  destroy$: Subject<void> = new Subject();

  constructor(
    private chartService: ChartsService,
    @Inject(PLATFORM_ID) private platformId: Object,
  ) {}

  ngOnInit() {
    this.chartService
      .getBarChartData()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res: BarChartResponse) => {
          if (res && res.data.length > 0) {
            this.basicData = this.transformData(res.data);
          }
        },
      });

    this.setupChartOptions();
  }
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
  setupChartOptions() {
    const textColor = this.getStyleValue('--text-color', '#495057');
    const textColorSecondary = this.getStyleValue(
      '--text-color-secondary',
      '#6c757d',
    );
    const surfaceBorder = this.getStyleValue('--surface-border', '#ced4da');

    this.basicOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false,
        },
      },
      layout: {
        padding: {
          top: 10,
          right: 20,
          bottom: 10,
          left: 20,
        },
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            color: textColorSecondary,
          },
          grid: {
            color: surfaceBorder,
            drawBorder: false,
          },
        },
        x: {
          ticks: {
            color: textColorSecondary,
          },
          display: true, // Turn off the x-axis grid lines
        },
      },
    };
  }

  getStyleValue(varName: string, fallback: string): string {
    if (isPlatformBrowser(this.platformId)) {
      const style = getComputedStyle(document.documentElement);
      return style.getPropertyValue(varName) || fallback;
    }
    return fallback;
  }

  transformData(responseData: BarChartDto[]): any {
    const labels = responseData.map((data) => data.assetSymbol);
    const data = responseData.map((data) => data.assetValue);
    const backgroundColors = this.generateColors(responseData.length);
    const hoverBackgroundColors = backgroundColors;

    return {
      labels: labels,
      datasets: [
        {
          label: 'Asset Value',
          data: data,
          backgroundColor: backgroundColors,
          hoverBackgroundColor: hoverBackgroundColors,
        },
      ],
    };
  }

  generateColors(count: number): string[] {
    const colors = [];
    for (let i = 0; i < count; i++) {
      colors.push(this.getRandomColor());
    }
    return colors;
  }

  getRandomColor(): string {
    const letters = '0123456789ABCDEF';
    let color = '#';
    for (let i = 0; i < 6; i++) {
      color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
  }
}
