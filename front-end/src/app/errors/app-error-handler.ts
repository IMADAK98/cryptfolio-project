import { ErrorHandler } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { MessageService } from 'primeng/api';
@Injectable({ providedIn: 'root' })
export class AppErrorHandler implements ErrorHandler {
  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private messageService: MessageService,
  ) {}

  handleError(error: any): void {
    if (isPlatformBrowser(this.platformId)) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'An unexpected error occurred!',
        life: 5000, // Toast display duration
      });
      console.error(error);
    } else {
      console.error('An error occurred:', error); // Log the error on server-side
    }
  }
}
