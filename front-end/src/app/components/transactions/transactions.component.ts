import { Component, TemplateRef, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  AssetDto,
  AssetResponse,
  TransactionDto,
} from '../../interfaces/crypto.interfaces';
import { Observable, map } from 'rxjs';
import { AsyncPipe } from '@angular/common';
import { NgOptimizedImage } from '@angular/common';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { Menu, MenuModule } from 'primeng/menu';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService, MenuItem, MessageService } from 'primeng/api';
import { TransactionService } from '../../services/transaction.service';
import { ToastModule } from 'primeng/toast';
import { EditTransactionFormComponent } from '../edit-transaction-form/edit-transaction-form.component';
import { DialogModule } from 'primeng/dialog';
import { LoadingIndicatorComponent } from '../../loading-indicator/loading-indicator.component';
import { Action } from '../../interfaces/portfolio.interface';
import { TransactionFormComponent } from '../transaction-form/transaction-form.component';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [
    AsyncPipe,
    NgOptimizedImage,
    CommonModule,
    CardModule,
    TableModule,
    ButtonModule,
    MenuModule,
    ConfirmDialogModule,
    ToastModule,
    EditTransactionFormComponent,
    DialogModule,
    LoadingIndicatorComponent,
    TransactionFormComponent,
  ],
  templateUrl: './transactions.component.html',
  styleUrl: './transactions.component.css',
  providers: [ConfirmationService, MessageService],
})
export class TransactionsComponent {
  assetId: number | undefined;
  transactionPageData$: Observable<AssetDto | undefined> | undefined;
  items: MenuItem[] | undefined;
  selectedItem: any = null;
  visible: boolean = false;
  transactionId: number | undefined;

  action = Action;
  @ViewChild('menu') menu!: Menu;
  coinID: any;

  isAddDialogVisible: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private transactionService: TransactionService,
  ) {}

  ngOnInit(): void {
    this.initObservable();
    this.initMenuItems();
  }

  initObservable() {
    const id = this.route.snapshot.paramMap.get('id');
    this.assetId = parseInt(id!, 10);

    this.transactionPageData$ = this.transactionService
      .fetchTransactionsByAssetId(this.assetId)
      .pipe(
        map((response: AssetResponse) => {
          if (response.data) {
            return response.data;
          } else {
            this.router.navigate(['/dashboard']);
            return undefined;
          }
        }),
      );
  }

  initMenuItems() {
    this.items = [
      {
        label: 'Options',
        items: [
          {
            label: 'Edit Transaction',
            icon: 'pi pi-pencil',
            command: () => {
              this.visible = true;
            },
          },
          {
            label: 'Delete Transaction',
            icon: 'pi  pi-trash',
            command: () => {
              this.confirmDelete(this.selectedItem);
            },
          },
        ],
      },
    ];
  }

  onCompletion(event: string) {
    this.visible = false;
    if (event === 'Success') {
      this.messageService.add({
        severity: 'success',
        summary: 'Success',
        detail: 'Transaction updated successfully.',
        life: 5000,
      });
      this.initObservable();
    } else if (event === 'Error') {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'An error occurred',
        life: 5000,
      });
    }
  }

  confirmDelete(transaction: TransactionDto) {
    this.confirmationService.confirm({
      message: 'Are you sure that you want to delete this transaction ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      acceptIcon: 'none',
      rejectIcon: 'none',
      rejectButtonStyleClass: 'p-button-text',
      accept: () => {
        this.onDelete(transaction.id);
      },
    });
  }

  onDelete(transactionId: number) {
    this.transactionService.deleteTransactionById(transactionId).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Deleted',
          detail: `Deleted successfully`,
          life: 5000,
        });
        this.initObservable();
      },
    });
  }

  handleMenuClick(transaction: TransactionDto, event: Event) {
    this.selectedItem = transaction;
    this.transactionId = transaction.id;
    this.menu.toggle(event);
  }

  onBack() {
    this.router.navigateByUrl('/dashboard');
  }

  showDialog(transactionId: number) {
    this.visible = true;
    this.transactionId = transactionId;
  }

  showAddDialog(coinId: string) {
    this.isAddDialogVisible = !this.isAddDialogVisible;
    this.coinID = coinId;
  }

  getFormattedAmount(transaction: TransactionDto): string {
    const sign = transaction.action === Action.BUY ? '+' : '-';
    return `${sign} ${transaction.transactionAmount}`;
  }

  handleFormCompletion(event: string) {
    this.isAddDialogVisible = false;
    if (event === 'Success') {
      this.messageService.add({
        severity: 'success',
        summary: 'Success',
        detail: 'Transaction updated successfully.',
        life: 5000,
      });
      this.initObservable();
    } else if (event === 'Error') {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'An error occurred',
        life: 5000,
      });
    }
  }
}
