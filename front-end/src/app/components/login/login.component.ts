import { Component, OnDestroy, OnInit } from '@angular/core';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { CardModule } from 'primeng/card';
import {
  FormBuilder,
  Validators,
  FormsModule,
  ReactiveFormsModule,
  FormControl,
  FormGroup,
} from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { LoginForm, UserLogin } from '../../interfaces/auth.interfaces';
import { AppError } from '../../errors/app-error';
import { Unauthorized } from '../../errors/unauthorized-error';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    InputTextModule,
    ButtonModule,
    ToastModule,
    CardModule,
    RouterLink,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
  providers: [MessageService],
})
export class LoginComponent implements OnInit, OnDestroy {
  invalidLogin: boolean = false;
  labelColor: string = 'text-gray-950';
  loginForm!: FormGroup;
  destroy$: Subject<void> = new Subject();
  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private messageService: MessageService,
  ) {}

  ngOnInit(): void {
    this.initLoginForm();
  }

  ngOnDestroy(): void {
    this.loginForm.reset();
    this.destroy$.next();
    this.destroy$.complete();
  }

  initLoginForm(): void {
    this.loginForm = this.fb.group<LoginForm>({
      email: new FormControl('', {
        validators: [Validators.email, Validators.required],
      }),
      password: new FormControl('', {
        validators: [Validators.required, Validators.minLength(8)],
      }),
    });
  }

  handleSubmit() {
    const { email, password } = this.loginForm.value;
    const userLogin: UserLogin = {
      email: email!,
      password: password!,
    };

    this.auth
      .logIn(userLogin)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res) => {
          if (res) {
            let returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
            this.router.navigate([returnUrl || '/dashboard']);
          } else this.invalidLogin = true;
        },
        error: (err: AppError) => {
          if (err instanceof Unauthorized) {
            this.messageService.add({
              severity: 'error',
              summary: 'Error',
              detail: `Email address or password incorrect`,
            });
            console.error(err);
          } else {
            this.messageService.add({
              severity: 'error',
              summary: 'Error',
              detail: `unexpected error occurred `,
            });
          }
        },
      });
  }

  get email() {
    return this.loginForm.get('email');
  }

  get password() {
    return this.loginForm.get('password');
  }
}
