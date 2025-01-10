import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

type TCenterlizedStateService = {
  refresh$: Observable<boolean>;
  triggerRefresh(): void;
};

@Injectable({
  providedIn: 'root',
})
export class CentralizedStateService implements TCenterlizedStateService {
  private refreshSubject = new BehaviorSubject<boolean>(true);
  refresh$: Observable<boolean> = this.refreshSubject.asObservable();

  triggerRefresh() {
    this.refreshSubject.next(true);
  }
}
