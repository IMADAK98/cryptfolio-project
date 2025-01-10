import { Component, OnInit } from '@angular/core';
import { ChartsService } from '../../services/charts.service';
import { PiChart } from '../../interfaces/charts.interfaces';
import { ChartModule } from 'primeng/chart';
import { Subject, takeUntil } from 'rxjs';
@Component({
  selector: 'app-pie-chart',
  standalone: true,
  templateUrl: './pi-chart.component.html',
  styleUrls: ['./pi-chart.component.css'],
  imports: [ChartModule],
})
export class PiChartComponent implements OnInit {
  data: any;
  options: any;

  destroy$: Subject<void> = new Subject();
  constructor(private chartService: ChartsService) {}

  ngOnInit() {
    this.chartService
      .getPieChart()
      .pipe(takeUntil(this.destroy$))
      .subscribe((responseData) => {
        if (responseData && responseData.data.length > 0) {
          this.data = this.transformData(responseData.data);
        }
      });
    this.options = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          labels: {
            usePointStyle: true,
          },
        },
      },
    };
  }

  ngOnDestroy(): void {
    //Called once, before the instance is destroyed.
    //Add 'implements OnDestroy' to the class.
    this.destroy$.next();
    this.destroy$.complete();
  }

  transformData(responseData: PiChart[]): any {
    const labels = responseData.map((data) => data.coinName);
    const data = responseData.map((data) => data.piPercentage);
    const backgroundColors = this.generateColors(responseData.length);
    const hoverBackgroundColors = backgroundColors; // Same colors for hover effect

    return {
      labels: labels,
      datasets: [
        {
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
