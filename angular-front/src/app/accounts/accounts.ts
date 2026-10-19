import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {Component, inject} from '@angular/core';
import {AsyncPipe} from '@angular/common';
import {Account, AccountListState, RequestStatus} from '../model/account.model';
import {catchError, map, Observable, of} from 'rxjs';

@Component({
  selector: 'app-accounts',
  imports: [
    AsyncPipe
  ],
  templateUrl: './accounts.html',
  styleUrl: './accounts.css',
})
export class Accounts {
  private http = inject(HttpClient);
  accounts$: Observable<AccountListState> = this.http
    .get<Account[]>('http://localhost:9999/EBANK-SERVICE/accounts')
    .pipe(
      map((accounts): AccountListState => ({
        accounts,
        status: RequestStatus.SUCCESS,
      })),
      catchError((error: HttpErrorResponse) =>
        of<AccountListState>({
          status: RequestStatus.ERROR,
          errorMessage: error.statusText,
        }),
      ),
    );
}


// http://localhost:9999/EBANK-SERVICE/accounts
