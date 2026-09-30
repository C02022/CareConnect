import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

import { Need } from './need';
import { MessageService } from './message.service';

/**
 * Provides Cupboard API functionality to any component that needs it.
 */
@Injectable({
  providedIn: 'root'
})
export class NeedService {
  // API's Cupboard URL, Requests URL, and URL of all Funded Needs.
  private cupboardUrl = 'http://localhost:8080/cupboard'
  private requestsUrl = 'http://localhost:8080/requests'
  private fundedUrl = "http://localhost:8080/funded"

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };
  constructor(
    private http: HttpClient,
    private messageService: MessageService) { }

  /** GET needs from the server */
  getNeeds(): Observable<Need[]> {
    return this.http.get<Need[]>(this.cupboardUrl)
      .pipe(
        tap(_ => this.log('fetched cupboard')),
        catchError(this.handleError<Need[]>('getNeeds', []))
      );
  }

  /** GET funded needs from the server */
  getFundedNeeds(): Observable<Need[]> {
    return this.http.get<Need[]>(this.fundedUrl)
      .pipe(
        tap(_ => this.log('fetched all funded needs')),
        catchError(this.handleError<Need[]>('getFundedNeeds', []))
      );
  }

  /** GET requests from the server */
  getRequestedNeeds(): Observable<Need[]> {
    return this.http.get<Need[]>(this.requestsUrl)
      .pipe(
        tap(_ => this.log('fetched requests')),
        catchError(this.handleError<Need[]>('getRequestedNeeds', []))
      );
  }

  /** POST: add a new need to the server */
  addNeed(need: Need): Observable<Need> {
    return this.http.post<Need>(this.cupboardUrl, need, this.httpOptions).pipe(
      tap((newNeed: Need) => this.log(`added need w/ id=${newNeed.id}`)),
      catchError(this.handleError<Need>('addNeed'))
    );
  }

  /** POST: add a new requested need to the server */
  addRequestedNeed(need: Need): Observable<Need> {
    return this.http.post<Need>(this.requestsUrl, need, this.httpOptions).pipe(
      tap((newNeed: Need) => this.log(`added need request w/ id=${newNeed.id}`)),
      catchError(this.handleError<Need>('addRequestedNeed'))
    );
  }

  /** PUT: edit an existing need in the server */
  updateNeed(need: Need): Observable<Need> {
    return this.http.put<Need>(this.cupboardUrl, need, this.httpOptions).pipe(
      tap((updatedNeed: Need) => this.log(`updated need w/ id=${updatedNeed.id}`)),
      catchError(this.handleError<Need>('editNeed'))
    );
  }

  /** DELETE: delete the need from the server */
  deleteNeed(id: number): Observable<Need> {
    const url = `${this.cupboardUrl}/${id}`;

    return this.http.delete<Need>(url, this.httpOptions).pipe(
      tap(_ => this.log(`deleted need id=${id}`)),
      catchError(this.handleError<Need>('deleteNeed'))
    );
  }

  /** DELETE: delete the requested need from the server */
  deleteRequestedNeed(id: number): Observable<Need> {
    const url = `${this.requestsUrl}/${id}`;

    return this.http.delete<Need>(url, this.httpOptions).pipe(
      tap(_ => this.log(`deleted requested need id=${id}`)),
      catchError(this.handleError<Need>('deleteRequestedNeed'))
    );
  }

  /* GET needs whose name contains search term */
  searchNeeds(term: string): Observable<Need[]> {
    if (!term.trim()) {
      // if not search term, return empty need array.
      return of([]);
    }
    return this.http.get<Need[]>(`${this.cupboardUrl}/?name=${term}`).pipe(
      tap(x => x.length ?
        this.log(`found needs matching "${term}"`) :
        this.log(`no needs matching "${term}"`)),
      catchError(this.handleError<Need[]>('searchNeeds', []))
    );
  }

  /** GET need by id. Return `undefined` when id not found */
  getNeedNo404<Data>(id: number): Observable<Need> {
    const url = `${this.cupboardUrl}/?id=${id}`;
    return this.http.get<Need[]>(url)
      .pipe(
        map(needs => needs[0]), // returns a {0|1} element array
        tap(n => {
          const outcome = n ? 'fetched' : 'did not find';
          this.log(`${outcome} need id=${id}`);
        }),
        catchError(this.handleError<Need>(`getNeed id=${id}`))
      );
  }

  /** GET need by id. Will 404 if id not found */
  getNeed(id: number): Observable<Need> {
    const url = `${this.cupboardUrl}/${id}`;
    return this.http.get<Need>(url).pipe(
      tap(_ => this.log(`fetched need id=${id}`)),
      catchError(this.handleError<Need>(`getNeed id=${id}`))
    );
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   *
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {


      console.error(error);


      this.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  /** Log a NeedService message with the MessageService */
  private log(message: string) {
    this.messageService.add(`NeedService: ${message}`);
  }
}
