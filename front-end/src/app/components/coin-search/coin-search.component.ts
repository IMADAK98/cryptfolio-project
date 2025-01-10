import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  BehaviorSubject,
  Observable,
  debounceTime,
  distinctUntilChanged,
  map,
  of,
  switchMap,
} from 'rxjs';
import { NgOptimizedImage } from '@angular/common';
import { Coin } from '../../interfaces/crypto.interfaces';
import { CryptoService } from '../../services/crypto.service';

@Component({
  selector: 'app-coin-search',
  standalone: true,
  imports: [AsyncPipe, FormsModule, NgOptimizedImage],
  templateUrl: './coin-search.component.html',
  styleUrl: './coin-search.component.css',
})
export class CoinSearchComponent implements OnInit {
  @Output() next = new EventEmitter();
  @Output() coinSelected = new EventEmitter();

  searchTerm: string = '';
  coins$: Observable<Coin[]> = new Observable<Coin[]>();
  protected onInput$ = new BehaviorSubject<string>('');

  constructor(public service: CryptoService) {}

  ngOnInit(): void {
    this.coins$ = this.fetchInitialTop100Coins();

    this.onInput$
      .pipe(
        debounceTime(1000), // Wait for 1000ms after each keystroke
        distinctUntilChanged(), // Ignore if the search term hasn't changed
        switchMap((term) => this.searchCoins(term)),
      )
      .subscribe((filteredCoins) => {
        this.coins$ = of(filteredCoins);
      });
  }

  /**
   * Fetches the initial top 100 coins sorted by rank.
   * @returns {Observable<Coin[]>} An observable emitting the top 100 coins.
   */
  fetchInitialTop100Coins(): Observable<Coin[]> {
    return this.service
      .getAllCoins()
      .pipe(
        map((coins) => coins.sort((a, b) => a.rank - b.rank).slice(0, 100)),
      );
  }

  /**
   * Handles the click event on a coin.
   * @param {string} coinID - The ID of the selected coin.
   * @param {EventEmitter<any>} next - The event emitter to emit the next event.
   */
  async handleClick(coinID: string, next: EventEmitter<any>): Promise<void> {
    next.emit();
    this.coinSelected.emit(coinID);
  }

  /**
   * Handles the input event from the search field.
   * @param {Event} event - The input event.
   */
  onInput(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    this.searchTerm = inputElement.value.trim();
    this.onInput$.next(this.searchTerm);
  }

  /**
   * Searches for coins based on the provided term.
   * @param {string} term - The search term.
   * @returns {Observable<Coin[]>} An observable emitting the filtered coins.
   */
  searchCoins(term: string): Observable<Coin[]> {
    if (term === '') {
      return this.fetchInitialTop100Coins();
    } else {
      return this.service
        .getAllCoins()
        .pipe(map((coins) => this.filterCoins(coins, term).slice(0, 20)));
    }
  }

  /**
   * Filters coins based on the search term.
   * @param {Coin[]} coins - The list of coins.
   * @param {string} term - The search term.
   * @returns {Coin[]} The filtered list of coins.
   */
  filterCoins(coins: Coin[], term: string): Coin[] {
    return coins.filter(
      (coin) =>
        coin.name.toLowerCase().includes(term.toLowerCase()) ||
        coin.symbol.toLowerCase().includes(term.toLowerCase()),
    );
  }
}
