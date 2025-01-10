import { Component, OnDestroy } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { ToastModule } from 'primeng/toast';
import { Subject } from 'rxjs';
import { AuthService } from '../../auth/auth.service';
import { ResetEmailRequest } from '../../interfaces/auth.interfaces';
import { HttpErrorResponse } from '@angular/common/http';
import { NotFoundError } from '../../errors/not-found-error';

@Component({
  selector: 'app-forgot-password-page',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    InputTextModule,
    ButtonModule,
    ToastModule,
    CardModule,
    RouterLink,
  ],
  templateUrl: './forgot-password-page.component.html',
  styleUrl: './forgot-password-page.component.css',
  providers: [MessageService],
})
export class ForgotPasswordPageComponent implements OnDestroy {
  destroy$: Subject<void> = new Subject();
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private messageService: MessageService,
  ) {}

  resetForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
  });

  handleSubmit() {
    if (this.resetForm.invalid) {
      console.error('Invalid Values provided');
      return;
    }

    const req: ResetEmailRequest = {
      email: this.email?.value!,
    };

    this.authService.sendResetPasswordEmail(req).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Password Reset',
          detail: 'A password reset link has been sent to your email.',
        });
      },
      error: (error: any) => {
        console.error('Error resetting password:', error);
        if (error instanceof NotFoundError) {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'No User Found with this email address',
          });
        } else
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to reset password. Please try again later.',
          });
      },
    });
  }

  ngOnDestroy(): void {
    this.resetForm.reset();
    this.destroy$.next();
    this.destroy$.complete();
  }

  get email() {
    return this.resetForm.get('email');
  }
}
