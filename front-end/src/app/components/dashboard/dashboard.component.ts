import { Component, ElementRef, ViewChild } from '@angular/core';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { map, Observable } from 'rxjs';
import {
  Portfolio,
  StatsDto,
  StatsResponse,
} from '../../interfaces/portfolio.interface';
import { CommonModule } from '@angular/common';
import { PortfolioService } from '../../services/portfolio.service';
import { SkeletonModule } from 'primeng/skeleton';
import { AssetsTableComponent } from '../assets-table/assets-table.component';
import { CardModule } from 'primeng/card';
import { PiChartComponent } from '../pi-chart/pi-chart.component';
import { NgOptimizedImage } from '@angular/common';
import { MenuActionsComponent } from '../menu-actions/menu-actions.component';
import { WizardComponent } from '../wizard/wizard.component';

import { LineChartComponent } from '../line-chart/line-chart.component';
import { BarChartComponent } from '../bar-chart/bar-chart.component';
import {
  trigger,
  state,
  style,
  transition,
  animate,
} from '@angular/animations';

import { TooltipModule } from 'primeng/tooltip';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
  imports: [
    DialogModule,
    ButtonModule,
    WizardComponent,
    CommonModule,
    SkeletonModule,
    AssetsTableComponent,
    CardModule,
    PiChartComponent,
    NgOptimizedImage,
    MenuActionsComponent,
    LineChartComponent,
    BarChartComponent,
    TooltipModule,
  ],

  animations: [
    // Existing slide-in from left animation
    trigger('slideInFromLeft', [
      state('void', style({ transform: 'translateX(-100%)', opacity: 0 })),
      state('*', style({ transform: 'translateX(0)', opacity: 1 })),
      transition('void => *', [animate('500ms ease-out')]),
    ]),
    // New slide-in from right animation
    trigger('slideInFromRight', [
      state('void', style({ transform: 'translateX(100%)', opacity: 0 })),
      state('*', style({ transform: 'translateX(0)', opacity: 1 })),
      transition('void => *', [animate('500ms ease-out')]),
    ]),
  ],
})
export class DashboardComponent {
  portfolio$!: Observable<Portfolio | null>;
  portfolioStats$!: Observable<StatsDto | null>;

  @ViewChild('assetsSection', { static: false }) assetsSection!: ElementRef;
  inView = false;
  visible: boolean = false;

  observerInitialized = false;

  showFormDialog = false;

  constructor(private pService: PortfolioService) {}

  ngOnInit(): void {
    this.pService.triggerUpdatePortfolioData();
    this.portfolio$ = this.pService.getPortfolio();
    this.portfolioStats$ = this.pService
      .getPortfolioStats()
      .pipe(map((res: StatsResponse) => res.data));
  }

  // ngAfterViewChecked() {
  //   if (this.assetsSection && !this.observerInitialized) {
  //     this.observerInitialized = true; // Prevent multiple initializations

  //     const observer = new IntersectionObserver(
  //       (entries) => {
  //         entries.forEach((entry) => {
  //           if (entry.isIntersecting) {
  //             this.inView = true;
  //             observer.unobserve(entry.target); // Stop observing once in view
  //           }
  //         });

  //       },

  //       { threshold: 0.1 }, // Trigger when 10% of the element is in view
  //     );

  //     observer.observe(this.assetsSection.nativeElement);
  //   }
  // }

  showDialog() {
    this.visible = true;
  }

  hideDialog() {
    this.visible = false;
    // this.loading = true;
    this.ngOnInit();
  }

  showForm() {
    this.showFormDialog = true;
  }

  ngOnDestroy(): void {
    //Called once, before the instance is destroyed.
    //Add 'implements OnDestroy' to the class.
  }
}
