import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { AuthService } from '../../auth/auth.service';
import { ResetPasswordRequest } from '../../interfaces/auth.interfaces';
@Component({
  selector: 'app-reset-password-form',
  standalone: true,
  imports: [
    InputTextModule,
    ButtonModule,
    ToastModule,
    ReactiveFormsModule,
    CommonModule,
  ],
  templateUrl: './reset-password-form.component.html',
  styleUrl: './reset-password-form.component.css',
  providers: [MessageService],
})
export class ResetPasswordFormComponent implements OnInit {
  token: string | null = null;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private messageService: MessageService,
    private authService: AuthService,
    private router: Router,
  ) {}

  resetPasswordForm = this.fb.group(
    {
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required, Validators.minLength(8)]],
    },
    {
      validators: this.mustMatch('password', 'confirmPassword'), // Form-level validator
    },
  );

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token');
  }

  mustMatch(password: string, confirmPassword: string) {
    return (formGroup: AbstractControl): ValidationErrors | null => {
      const passControl = formGroup.get(password);
      const confirmPasswordControl = formGroup.get(confirmPassword);

      if (!passControl || !confirmPassword) {
        return null;
      }

      if (passControl.value !== confirmPasswordControl?.value) {
        confirmPasswordControl?.setErrors({ mustMatch: true });
        return { mustMatch: true };
      } else {
        confirmPasswordControl?.setErrors(null);
        return null;
      }
    };
  }

  onSubmit() {
    if (this.resetPasswordForm.invalid) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Please fill all required fields.',
      });
    }

    const req: ResetPasswordRequest = {
      email: this.email?.value!,
      password: this.password?.value!,
      confirmPassword: this.confirmPassword?.value!,
      token: this.token!,
    };

    this.authService.resetPassword(req).subscribe({
      next: (res) => {
        this.messageService.add({
          severity: 'success',
          summary: 'Password Reset',
          detail: 'Password reset successful.',
        });

        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1000);
      },

      error: (err) => {
        console.error('Error resetting password:', err);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to reset password. Please try again later.',
        });
      },
    });
  }

  get email() {
    return this.resetPasswordForm.get('email');
  }

  get password() {
    return this.resetPasswordForm.get('password');
  }

  get confirmPassword() {
    return this.resetPasswordForm.get('confirmPassword');
  }
}
