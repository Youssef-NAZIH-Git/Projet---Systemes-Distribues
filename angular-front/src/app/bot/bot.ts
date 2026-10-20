import { AsyncPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-bot',
  imports: [FormsModule, AsyncPipe],
  templateUrl: './bot.html',
  styleUrl: './bot.css',
})
export class Bot {
  private http = inject(HttpClient);

  query = '';
  response$!: Observable<string>;

  askAgent(): void {
    this.response$ = this.http.get(
      'http://localhost:9999/EBANK-BOT/chat?query=' +
      encodeURIComponent(this.query),
      { responseType: 'text' },
    );
  }
}
