import { Component, EventEmitter, Output } from '@angular/core';
import { TableModule } from 'primeng/table';
import { PanelModule } from 'primeng/panel';
import { AssetService } from '../../services/asset.service';
import { Observable, Subject, catchError, of, switchMap } from 'rxjs';
import { Asset } from '../../interfaces/crypto.interfaces';
import { AsyncPipe } from '@angular/common';
import { NgOptimizedImage } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TransactionFormComponent } from '../transaction-form/transaction-form.component';
import { CommonModule } from '@angular/common';
import { MenuModule } from 'primeng/menu';
import { MenuItem } from 'primeng/api';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { Router } from '@angular/router';
import { PortfolioService } from '../../services/portfolio.service';
@Component({
  selector: 'app-assets-table',
  standalone: true,
  imports: [
    TableModule,
    PanelModule,
    AsyncPipe,
    NgOptimizedImage,
    ButtonModule,
    DialogModule,
    TransactionFormComponent,
    CommonModule,
    MenuModule,
    ConfirmDialogModule,
    ToastModule,
  ],
  templateUrl: './assets-table.component.html',
  styleUrl: './assets-table.component.css',
  providers: [ConfirmationService, MessageService],
})
export class AssetsTableComponent {
  @Output() complete = new EventEmitter();

  items: MenuItem[] | undefined;
  $event: Event | undefined;

  selectedItem: any = null;
  visible: boolean = false;
  coinID = '';
  destroy$: Subject<void> | undefined;

  constructor(
    private assetService: AssetService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private router: Router,
    private ps: PortfolioService,
  ) {}

  assets$: Observable<Asset[]> = of([]); // Initialize as empty array

  ngOnInit(): void {
    this.initObvservable();
    this.initMenuItems();
  }
  initObvservable() {
    this.assets$ = this.assetService.data$.pipe(
      switchMap((portfolio) => (portfolio ? of(portfolio.assets) : of([]))),
      catchError((error) => {
        console.error('Error fetching assets:', error);
        return of([]); // Handle errors gracefully
      }),
    );
  }

  initMenuItems() {
    this.items = [
      {
        label: 'Options',
        items: [
          {
            label: 'View transactions',
            icon: 'pi pi-link',
            command: () => {
              this.router.navigate(['/transactions', this.selectedItem.id]);
            },
          },
          {
            label: 'Delete Asset',
            icon: 'pi pi-trash',
            command: () => {
              this.confirm(this.selectedItem);
            },
          },
        ],
      },
    ];
  }

  handleFormCompletion() {
    this.visible = false;
    this.complete.emit();
  }

  showDialog(assetCoinID: string) {
    this.visible = true;
    this.coinID = assetCoinID;
  }

  onDelete(assetId: number) {
    this.assetService.deleteAsset(assetId).subscribe({
      complete: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Deleted',
          detail: `Deleted successfully`,
        });
        this.complete.emit();
      },
    });
  }

  confirm(asset: Asset) {
    this.confirmationService.confirm({
      message:
        'Are you sure that you want to delete this asset and all associated transactions ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      acceptIcon: 'none',
      rejectIcon: 'none',
      rejectButtonStyleClass: 'p-button-text',
      accept: () => {
        this.onDelete(asset.id);
      },
    });
  }

  getFontStyle(value: number) {
    if (value > 0) {
      return 'text-green-600';
    } else if (value < 0) {
      return 'text-red-600';
    } else return 'text-gray-900';
  }
}
