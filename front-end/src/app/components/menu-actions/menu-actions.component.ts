import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { MenuModule } from 'primeng/menu';
import { ButtonModule } from 'primeng/button';
import { PortfolioService } from '../../services/portfolio.service';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'menu-actions',
  standalone: true,
  imports: [MenuModule, ButtonModule, ToastModule, ConfirmDialogModule],
  templateUrl: './menu-actions.component.html',
  styleUrl: './menu-actions.component.css',
  providers: [ConfirmationService, MessageService],
})
export class MenuActionsComponent implements OnInit {
  @Input() portfolioId!: number;
  @Output() complete = new EventEmitter();

  items: MenuItem[] | undefined;

  constructor(
    private ps: PortfolioService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private router: Router,
    private authService: AuthService,
  ) {}
  ngOnInit(): void {
    this.items = [
      {
        items: [
          // {
          //   label: 'Edit Portfolio',
          //   icon: 'pi pi-pencil',
          //   command: () => {
          //     console.log('Edit Portfolio');
          //   },
          // },
          {
            label: 'Delete Portfolio',
            icon: 'pi pi-trash',
            command: () => {
              this.confirm();
            },
          },
          {
            label: 'logout',
            icon: 'pi pi-power-off',
            command: () => {
              this.authService.logout();
            },
          },
        ],
      },
    ];
  }

  onDelete(portfolioId: number) {
    this.ps.deletePortfolio(portfolioId).subscribe({
      complete: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Deleted',
          detail: `Deleted successfully`,
        });

        setTimeout(() => {
          this.complete.emit(), window.location.reload();
        }, 1000);
      },
    });
  }

  confirm() {
    this.confirmationService.confirm({
      message:
        'Are you sure that you want to delete this portfolio and all associated assets ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      acceptIcon: 'none',
      rejectIcon: 'none',
      rejectButtonStyleClass: 'p-button-text',
      accept: () => {
        this.onDelete(this.portfolioId);
      },
    });
  }
}
