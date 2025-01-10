import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { authGuard } from './auth/auth.guard';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { SignupComponent } from './components/signup/signup.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AssetsTableComponent } from './components/assets-table/assets-table.component';
import { TransactionFormComponent } from './components/transaction-form/transaction-form.component';
import { TransactionsComponent } from './components/transactions/transactions.component';
import { LoadingIndicatorComponent } from './loading-indicator/loading-indicator.component';
import { PiChartComponent } from './components/pi-chart/pi-chart.component';
import { HeaderComponent } from './components/header/header.component';
import { LoginComponent } from './components/login/login.component';
import { WizardComponent } from './components/wizard/wizard.component';
import { PortfolioComponent } from './components/new-portfolio-form/new-portfolio-form.component';
import { NoPortfolioComponent } from './components/no-portfolio/no-portfolio.component';

import { slideInAnimation } from './animations'; // Adjust the path as necessary
import { animation } from '@angular/animations';
import { LineChartComponent } from './components/line-chart/line-chart.component';
import { BarChartComponent } from './components/bar-chart/bar-chart.component';
import { ForgotPasswordPageComponent } from './components/forgot-password-page/forgot-password-page.component';
import { ResetPasswordFormComponent } from './components/reset-password-form/reset-password-form.component';
import { noPortfolioGuard } from './guards/no-portfolio.guard';
import { portfolioGuard } from './guards/portfolio.guard';
import { resetPasswordGuard } from './guards/reset-password.guard';

export const routes: Routes = [
  {
    path: '',
    title: 'home page',
    component: HomeComponent,
    data: { animation: 'HomePage' },
  },

  {
    path: 'dashboard',
    component: DashboardComponent,
    title: 'dashboard',
    canActivate: [authGuard, portfolioGuard],
    data: { animation: 'DashboardPage' },
  },

  {
    path: 'signup',
    component: SignupComponent,
    title: 'signup',
  },

  {
    path: 'login',
    component: LoginComponent,
    title: 'login',
  },

  {
    path: 'reset-password',
    component: ForgotPasswordPageComponent,
    title: 'reset password',
  },

  {
    path: 'reset-form',
    component: ResetPasswordFormComponent,
    canActivate: [resetPasswordGuard],
    title: 'reset password',
  },

  {
    path: 'no-portfolio',
    component: NoPortfolioComponent,
    title: 'no portfolio',
    canActivate: [authGuard, noPortfolioGuard],
  },

  {
    path: 'wizard',
    component: WizardComponent,
  },

  {
    path: 'portfolio',
    component: PortfolioComponent,
  },

  {
    path: 'transactions/:id',
    component: TransactionsComponent,
  },

  {
    path: 'loading',
    component: LoadingIndicatorComponent,
  },

  // {
  //   path: '',
  //   redirectTo: '/home',
  //   pathMatch: 'full',
  // },

  {
    path: 'table',
    component: AssetsTableComponent,
  },
  {
    path: 'form',
    component: TransactionFormComponent,
  },

  {
    path: 'pi-chart',
    component: PiChartComponent,
  },

  {
    path: 'header',
    component: HeaderComponent,
  },

  {
    path: 'line-chart',
    component: LineChartComponent,
  },

  {
    path: 'bar-chart',
    component: BarChartComponent,
  },

  {
    path: '**',
    component: PageNotFoundComponent,
  },
];
