import {
  Component,
  Inject,
  OnDestroy,
  OnInit,
  PLATFORM_ID,
  ViewChild,
} from '@angular/core';
import { ChartModule } from 'primeng/chart';
import { ChartsService } from '../../services/charts.service';
import {
  LineChartParams,
  SnapshotResponse,
} from '../../interfaces/charts.interfaces';
import { Subject, takeUntil } from 'rxjs';
import {
  SelectButtonChangeEvent,
  SelectButtonModule,
} from 'primeng/selectbutton';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SkeletonModule } from 'primeng/skeleton';
import { UIChart } from 'primeng/chart';
import { Chart, ChartOptions, registerables } from 'chart.js';
import 'chartjs-adapter-date-fns';
import { enUS } from 'date-fns/locale';

Chart.register(...registerables);

@Component({
  selector: 'app-line-chart',
  standalone: true,
  imports: [
    FormsModule,
    ChartModule,
    SelectButtonModule,
    CommonModule,
    SkeletonModule,
  ],
  templateUrl: './line-chart.component.html',
  styleUrl: './line-chart.component.css',
})
export class LineChartComponent implements OnInit, OnDestroy {
  @ViewChild('chart') chart!: UIChart;

  data: any;
  options!: ChartOptions;
  destroy$: Subject<void> = new Subject();
  stateOptions: any[] = [
    { label: '1h', value: LineChartParams.HOUR },
    { label: '24h', value: LineChartParams.DAY },
    { label: '7d', value: LineChartParams.WEEK },
    { label: '30d', value: LineChartParams.MONTH },
    // { label: 'All', value: LineChartParams.ALL },
  ];
  value: LineChartParams = LineChartParams.DAY;

  documentStyle: any;
  textColor: any;
  textColorSecondary: any;
  surfaceBorder: any;

  constructor(
    private chartService: ChartsService,
    @Inject(PLATFORM_ID) private platformId: Object,
  ) {}

  ngOnInit() {
    this.initializeStyles();
    this.fetchChartData();
  }

  getStyleValue(varName: string, fallback: string): string {
    if (isPlatformBrowser(this.platformId)) {
      const style = getComputedStyle(document.documentElement);
      return style.getPropertyValue(varName) || fallback;
    }
    return fallback;
  }

  onFilterChange(event: SelectButtonChangeEvent) {
    if (event.value !== null && event.value !== undefined) {
      this.value = event.value;
      this.chartService.updateLineChartFilter(event.value);
      this.fetchChartData();
    }
  }
  setChartData(chartData: any) {
    // Notice we're using chartData.datasets directly instead of trying to reconstruct it
    this.data = {
      labels: chartData.labels,
      datasets: chartData.datasets, // Use the datasets array directly
    };

    this.options = this.getChartOptions();

    // Force chart update
    if (this.chart && this.chart.chart) {
      this.chart.chart.update();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeStyles() {
    if (isPlatformBrowser(this.platformId)) {
      this.documentStyle = getComputedStyle(document.documentElement);

      this.textColor = this.documentStyle.getPropertyValue('--text-color');
      this.textColorSecondary = this.documentStyle.getPropertyValue(
        '--text-color-secondary',
      );
      this.surfaceBorder =
        this.documentStyle.getPropertyValue('--surface-border');
    }
  }

  private fetchChartData() {
    this.chartService
      .getLineChartData()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: SnapshotResponse) => {
          const chartData = this.transformData(response.data);
          this.setChartData(chartData);
        },
        error: (err: any) => {
          console.error('Error fetching snapshot data', err);
        },
      });
  }

  private transformData(data: any[]) {
    // First, sort the data chronologically
    const sortedData = [...data].sort(
      (a, b) =>
        new Date(a.snapshotDate).getTime() - new Date(b.snapshotDate).getTime(),
    );

    // Filter data based on the selected time range
    const now = new Date();
    const filteredData = sortedData.filter((snapshot) => {
      const snapshotDate = new Date(snapshot.snapshotDate);
      switch (this.value) {
        case LineChartParams.HOUR:
          return now.getTime() - snapshotDate.getTime() <= 60 * 60 * 1000;
        case LineChartParams.DAY:
          return now.getTime() - snapshotDate.getTime() <= 24 * 60 * 60 * 1000;
        case LineChartParams.WEEK:
          return (
            now.getTime() - snapshotDate.getTime() <= 7 * 24 * 60 * 60 * 1000
          );
        case LineChartParams.MONTH:
          return (
            now.getTime() - snapshotDate.getTime() <= 30 * 24 * 60 * 60 * 1000
          );
        default:
          return true;
      }
    });

    return {
      labels: filteredData.map((snapshot) => new Date(snapshot.snapshotDate)),
      datasets: [
        {
          label: 'Portfolio Value',
          data: filteredData.map((snapshot) => ({
            x: new Date(snapshot.snapshotDate),
            y: snapshot.totalValue,
          })),
          fill: {
            target: 'origin',
            above: `${this.documentStyle.getPropertyValue('--blue-400')}20`, // 20 is opacity in hex
          },
          borderColor: this.documentStyle.getPropertyValue('--blue-400'),
          tension: 0.6,
          pointRadius: 0,
          borderWidth: 2,
          cubicInterpolationMode: 'monotone',
        },
      ],
    };
  }

  private getChartOptions(): ChartOptions {
    const timeConfig = this.getTimeConfig();

    return {
      responsive: true,
      maintainAspectRatio: false,
      aspectRatio: 0.6,
      plugins: {
        legend: {
          labels: {
            color: this.textColor,
          },
        },
        tooltip: {
          mode: 'index',
          intersect: false,
          callbacks: {
            label: function (context: any) {
              let value = context.raw.y;
              return `Portfolio Value: ${new Intl.NumberFormat('en-US', {
                style: 'currency',
                currency: 'USD',
                minimumFractionDigits: 2,
                maximumFractionDigits: 2,
              }).format(value)}`;
            },
          },
        },
      },
      scales: {
        x: {
          type: 'time',
          time: {
            unit: timeConfig.unit,
            displayFormats: timeConfig.displayFormats,
          },
          adapters: {
            date: {
              locale: enUS,
            },
          },
          ticks: {
            color: this.textColorSecondary,
            maxTicksLimit: this.getMaxTicksLimit(),
          },
          grid: {
            display: false,
          },
        },
        y: {
          beginAtZero: false, // Change this to false to allow dynamic starting point
          ticks: {
            color: this.textColorSecondary,
            // Add number formatting for better readability of large numbers
            callback: (value: any) => {
              return new Intl.NumberFormat('en-US', {
                style: 'currency',
                currency: 'USD',
                notation: 'compact',
                maximumFractionDigits: 1,
              }).format(value);
            },
          },
          grid: {
            color: this.surfaceBorder,
          },

          // // Add suggested minimum/maximum padding to prevent data from touching edges
          // suggestedMin: (context: any) => {
          //   const min = Math.min(
          //     ...context.chart.data.datasets[0].data.map(
          //       (point: any) => point.y,
          //     ),
          //   );
          //   return min - min * 0.05; // 5% padding below minimum
          // },
          // suggestedMax: (context: any) => {
          //   const max = Math.max(
          //     ...context.chart.data.datasets[0].data.map(
          //       (point: any) => point.y,
          //     ),
          //   );
          //   return max + max * 0.05; // 5% padding above maximum
          // },
        },
      },
    };
  }

  private getTimeConfig(): {
    unit: 'minute' | 'hour' | 'day' | 'month';
    displayFormats: any;
    stepSize?: number;
  } {
    switch (this.value) {
      case LineChartParams.HOUR:
        return {
          unit: 'minute',
          stepSize: 5,
          displayFormats: {
            minute: 'HH:mm',
          },
        };
      case LineChartParams.DAY:
        return {
          unit: 'hour',
          stepSize: 2,
          displayFormats: {
            hour: 'HH:mm',
          },
        };
      case LineChartParams.WEEK:
        return {
          unit: 'day',
          stepSize: 1,
          displayFormats: {
            day: 'MMM dd',
          },
        };
      case LineChartParams.MONTH:
        return {
          unit: 'day',
          stepSize: 2,
          displayFormats: {
            day: 'MMM dd',
          },
        };
      default:
        return {
          unit: 'month',
          displayFormats: {
            month: 'MMM yyyy',
          },
        };
    }
  }

  private getMaxTicksLimit() {
    switch (this.value) {
      case LineChartParams.HOUR:
        return 12;
      case LineChartParams.DAY:
        return 24;
      case LineChartParams.WEEK:
        return 7;
      case LineChartParams.MONTH:
        return 10;
      default:
        return 12;
    }
  }
}
