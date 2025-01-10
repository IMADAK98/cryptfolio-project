import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { AuthService } from '../../auth/auth.service';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [ButtonModule, CommonModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit {
  currentUrl: string = '';

  Links = [
    { name: 'Home', route: '' },
    { name: 'Dashboard', route: '/dashboard' },
    { name: 'Login', route: '/login' },
    { name: 'Logout', route: '/logout' },
  ];

  constructor(
    private router: Router,
    private auth: AuthService,
  ) {}

  ngOnInit() {
    // Update the URL tracking
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        this.currentUrl = event.urlAfterRedirects;
      });
  }

  navigate(route: string) {
    if (route === '/logout') {
      this.onLogout();
    } else {
      this.router.navigateByUrl(route);
    }
  }

  onLogout() {
    this.auth.logout();
  }

  // isActive(route: string): boolean {
  //   return this.currentUrl.includes(route);
  // }
  isActive(route: string): boolean {
    if (route === '') {
      return this.currentUrl === '/'; // Match only the exact root URL
    }
    return this.currentUrl.startsWith(route);
  }

  getFilteredLinks() {
    return this.Links.filter((link) => {
      if (link.route === '/login') {
        return !this.auth.isLoggedIn();
      }
      if (link.route === '/logout') {
        return this.auth.isLoggedIn();
      }
      return true;
    });
  }
}
