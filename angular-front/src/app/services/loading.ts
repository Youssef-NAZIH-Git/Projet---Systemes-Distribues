import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class LoadingService {
  private loading = signal<boolean>(false);

  readonly isLoading = this.loading.asReadonly();

  setLoading(value: boolean): void {
    this.loading.set(value);
  }
}
