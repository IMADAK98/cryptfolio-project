import { Component } from '@angular/core';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { CardModule } from 'primeng/card';
import { RouterLink } from '@angular/router';
import { MessagesModule } from 'primeng/messages';
import { NgClass } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { AppError } from '../../errors/app-error';
import { ConflictError } from '../../errors/conflict-error';
import { UserSignup } from '../../interfaces/auth.interfaces';
@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    InputTextModule,
    ButtonModule,
    ToastModule,
    CardModule,
    RouterLink,
    MessagesModule,
    NgClass,
  ],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.css',
  providers: [MessageService],
})
export class SignupComponent {
  //to do : what happend when this is true?
  invalidLogin = false;
  usedEmail = false;
  value = '';
  labelColor = 'text-gray-950';

  profileForm = this.fb.group({
    firstName: ['', Validators.required],
    lastName: [''],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]], // todo add regexp validation
  });

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private messageService: MessageService,
  ) {}

  handleSubmit() {
    const { firstName, lastName, email, password } = this.profileForm.value;

    const signUp: UserSignup = {
      firstName: firstName!,
      lastName: lastName!,
      email: email!,
      password: password!,
    };

    this.auth.signUp(signUp).subscribe({
      next: (res) => {
        if (res) {
          let returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
          this.router.navigate([returnUrl || '/dashboard']);
        } else this.invalidLogin = true;
      },
      error: (err: AppError) => {
        if (err instanceof ConflictError) {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: `Email address is already in use`,
          });
          this.usedEmail = true;
          this.profileForm.get('email')?.setErrors({ conflict: true });
          console.error(err);
        } else throw err;
      },
    });
  }

  get firstName() {
    return this.profileForm.get('firstName');
  }

  get lastName() {
    return this.profileForm.get('lastName');
  }

  get email() {
    return this.profileForm.get('email');
  }

  get password() {
    return this.profileForm.get('password');
  }
  isInvalidAndTouched(controlName: string): boolean {
    const control = this.profileForm.get(controlName);
    return !!control && control.invalid && control.touched;
  }
}
