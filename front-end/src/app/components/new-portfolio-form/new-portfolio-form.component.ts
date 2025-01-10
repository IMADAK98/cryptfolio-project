import { Component } from '@angular/core';
import { StepperModule } from 'primeng/stepper';
import { ButtonModule } from 'primeng/button';
import { DividerModule } from 'primeng/divider';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import {
  FormsModule,
  FormGroup,
  FormBuilder,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PortfolioService } from '../../services/portfolio.service';
import { PortfolioRequest } from '../../interfaces/portfolio.interface';
import { Router } from '@angular/router';

@Component({
  selector: 'portfolio-form',
  standalone: true,
  imports: [ButtonModule, ReactiveFormsModule, InputTextModule, CommonModule],
  templateUrl: './new-portfolio-form.component.html',
  styleUrl: './new-portfolio-form.component.css',
})
export class PortfolioComponent {
  portfolioForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private ps: PortfolioService,
    private router: Router,
  ) {
    this.portfolioForm = this.fb.group({
      portfolioName: ['', Validators.required],
    });
  }

  onSubmit() {
    if (this.portfolioForm.valid) {
      const pReq: PortfolioRequest = {
        portfolioName: this.portfolioName?.value,
      };
      this.ps.createPortfolio(pReq).subscribe({
        next: () => this.router.navigate(['/dashboard']),
      });
    }
  }

  get portfolioName() {
    return this.portfolioForm.get('portfolioName');
  }
}
