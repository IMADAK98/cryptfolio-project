import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { CalendarModule } from 'primeng/calendar';
import { InputNumberModule } from 'primeng/inputnumber';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DialogModule } from 'primeng/dialog';
import { LoadingIndicatorComponent } from '../../loading-indicator/loading-indicator.component';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { TransactionService } from '../../services/transaction.service';
import { Action } from '../../interfaces/portfolio.interface';
import { debounceTime, Subject, takeUntil } from 'rxjs';
import {
  TransactionDto,
  TransactionResponse,
  UpdateTransactionDto,
} from '../../interfaces/crypto.interfaces';
import { LoadingService } from '../../loading-indicator/loading-utils/loading.service';

interface EditForm {
  date: FormControl<Date | null>;
  amount: FormControl<number | null>;
  price: FormControl<number | null>;
  total: FormControl<number | null>;
  action: FormControl<Action | null>;
}

@Component({
  selector: 'app-edit-transaction-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    InputNumberModule,
    CalendarModule,
    InputNumberModule,
    ButtonModule,
    CommonModule,
    DialogModule,
    LoadingIndicatorComponent,
    ConfirmDialogModule,
    ToastModule,
  ],
  templateUrl: './edit-transaction-form.component.html',
  styleUrl: './edit-transaction-form.component.css',
  providers: [ConfirmationService, MessageService],
})
export class EditTransactionFormComponent implements OnInit, OnDestroy {
  @Input() transactionId: number | undefined;
  @Input() assetId: number | undefined;
  @Output() completed = new EventEmitter();
  editForm!: FormGroup<EditForm>;
  today: Date | undefined;
  destroy$: Subject<void> = new Subject();
  constructor(
    private fb: FormBuilder,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private transactionService: TransactionService,
    private loadingService: LoadingService,
  ) {}

  ngOnInit(): void {
    this.initEditForm();
    this.today = new Date();
    this.subscribeToValueChanges();
  }
  ngOnDestroy(): void {
    this.editForm.reset();
    this.destroy$.next();
    this.destroy$.complete();
  }

  initEditForm(): void {
    this.editForm = this.fb.group<EditForm>({
      date: new FormControl(new Date(), { validators: [Validators.required] }),
      amount: new FormControl(0, { validators: [Validators.required] }),
      price: new FormControl(0, { validators: [Validators.required] }),
      total: new FormControl(0, {
        validators: [Validators.required, Validators.min(0.1)],
      }),
      action: new FormControl(null),
    });

    if (this.transactionId) {
      this.assignTransactionDate(this.transactionId);
    }
  }

  private assignTransactionDate(transactionId: number) {
    this.transactionService
      .fetchTransactionById(transactionId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res: TransactionResponse) => this.populateFormData(res.data),
      });
  }

  populateFormData(data: TransactionDto) {
    this.editForm.patchValue({
      date: new Date(data.timestamp),
      amount: data.transactionAmount,
      price: data.price,
      total: data.transactionAmount * data.price,
      action: data.action,
    });
  }

  private subscribeToValueChanges(): void {
    this.editForm?.valueChanges
      .pipe(takeUntil(this.destroy$), debounceTime(300)) // Debounce to reduce the frequency of value changes
      .subscribe(() => {
        this.calculateTotal();
      });
  }

  private calculateTotal() {
    const price = this.price?.value;
    const amount = this.amount?.value;

    if (!price || !amount || amount <= 0) {
      this.total?.setValue(0, { emitEvent: false });
      return;
    }
    // two total variables definetly shit practices ...
    let total: number;
    total = price * amount;

    this.total?.setValue(total, { emitEvent: false });
  }

  onSubmit() {
    if (this.editForm.invalid) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Please fill all required fields.',
      });
      return;
    }

    const requestDto: UpdateTransactionDto = {
      id: this.transactionId!,
      price: this.price?.value!,
      transactionAmount: this.amount?.value!,
      timestamp: this.date?.value!,
      action: this.action?.value!,
      assetId: this.assetId!,
    };

    // this.loadingService.loadingOn();

    this.transactionService
      .updateTransaction(requestDto)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.completed.emit('Success');
          this.editForm.reset();
        },
        error: (error) => {
          this.completed.emit('Error');
          console.error(error);
        },
      });
  }

  get ActionEnum() {
    return Action;
  }

  get action() {
    return this.editForm?.get('action');
  }

  get price() {
    return this.editForm?.get('price');
  }

  get amount() {
    return this.editForm?.get('amount');
  }

  get date() {
    return this.editForm?.get('date');
  }

  get total() {
    return this.editForm?.get('total');
  }
}
