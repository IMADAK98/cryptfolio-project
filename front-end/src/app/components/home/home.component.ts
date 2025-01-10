import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { Router } from '@angular/router';

import { NgxTypedWriterModule } from 'ngx-typed-writer';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [NgxTypedWriterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent implements OnInit {
  videoSource = 'assets/stocks_vid.mp4';
  imageSource = 'assets/bg-image-white.png';
  imageState = 'normal';

  readonly sentencies: string[] = [
    'Simplify Your Portfolio Management!',
    'Stay Ahead with Real-Time Tracking.',
    'All Your Crypto in One Dashboard.',
    'Analyze.Optimize.Grow.',
    'Track Gains,Minimize Losses.',
    'Visualize Performance Effortlessly.',
    'Stay Informed,Stay Empowered.',
  ];

  constructor(
    private auth: AuthService,
    private router: Router,
  ) {}
  ngOnInit(): void {}

  onAccess(redirectTo: string) {
    this.router.navigate(['/dashboard'], { state: { redirectTo } });
  }

  // ngAfterViewInit() {
  //   const video = document.querySelector('video');
  //   video?.load();
  // }
  onClickLogout() {
    this.auth.logout();
  }
}
