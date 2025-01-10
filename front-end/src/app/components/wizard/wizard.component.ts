import { Component, EventEmitter, Output } from '@angular/core';
import { StepperModule } from 'primeng/stepper';
import { CommonModule } from '@angular/common';
import { NgOptimizedImage } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { TransactionFormComponent } from '../transaction-form/transaction-form.component';
import { CoinSearchComponent } from '../coin-search/coin-search.component';

//TODO implement caching for performance optimization
@Component({
  selector: 'app-wizard',
  standalone: true,
  imports: [
    StepperModule,
    NgOptimizedImage,
    CommonModule,
    FormsModule,
    TransactionFormComponent,
    InputTextModule,
    CoinSearchComponent,
    ButtonModule,
  ],
  templateUrl: './wizard.component.html',
  styleUrl: './wizard.component.css',
})
export class WizardComponent {
  @Output() complete = new EventEmitter();

  constructor() {}

  coinID!: string;

  handleCoinSelected(coinID: string, nextCallback: EventEmitter<any>) {
    this.coinID = coinID;
    nextCallback.emit(); // Pass the selected coin to the next step
  }

  handleComplete() {
    this.complete.emit();
  }
}
