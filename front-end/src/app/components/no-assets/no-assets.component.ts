import { Component } from '@angular/core';
import { NgOptimizedImage } from '@angular/common';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'no-assets',
  standalone: true,
  imports: [NgOptimizedImage, ButtonModule],
  templateUrl: './no-assets.component.html',
  styleUrl: './no-assets.component.css',
})
export class NoAssetsComponent {}
