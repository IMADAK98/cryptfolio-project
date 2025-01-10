import { Injectable } from '@angular/core';
import { BehaviorSubject, timer } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class LoadingService {
  private loadingSubject = new BehaviorSubject(false);

  loading$ = this.loadingSubject.asObservable();

  constructor() {}

  loadingOn() {
    this.loadingSubject.next(true);
  }

  loadingOff() {
    // Adding a delay before turning off the loading indicator
    timer(0).subscribe(() => {
      this.loadingSubject.next(false);
    });
  }
}
