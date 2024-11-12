import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

import { Helper } from './helper';
import { MessageService } from './message.service';
import {Need} from "./need";

/**
 * Provides Helper API functionality to any component that needs it.
 */
@Injectable({
  providedIn: 'root'
})
export class HelperService {

  // API's URL
  private helperUrl = 'http://localhost:8080/helpers'

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  constructor(
    private http: HttpClient,
    private messageService: MessageService) { }


  /** GET helpers from server */
  getHelpers(): Observable<Helper[]> {
    return this.http.get<Helper[]>(this.helperUrl + "/all")
      .pipe(
        tap(_ => this.log('fetched helpers')),
        catchError(this.handleError<Helper[]>('getHelpers', []))
      );
  }

  /** GET helper object from server */
  getHelper(username: string): Observable<Helper> {
    return this.http.get<Helper>(this.helperUrl + "?username=" + username)
      .pipe(
        tap(_ => this.log('fetched helper ' + username)),
        catchError(this.handleError<Helper>('getHelper', undefined))
      );
  }

  /** DELETE need from basket from server */
  removeNeedFromBasket(username: string, id: number): Observable<Need> {
    return this.http.delete<Need>(this.helperUrl + "/remove?username=" + username + "&id=" + id)
        .pipe(
          tap(_ => this.log('deleted need ' + id + ' for ' + username)),
            catchError(this.handleError<Need>('removeNeedFromBasket', undefined))
        )
  }

  /** PUT function to add need to basket */
  addNeedToBasket(username: string, id: number): Observable<Need> {
    return this.http.get<Need>(this.helperUrl + "/add?username=" + username + "&id=" + id)
      .pipe(
        tap(_ => this.log('added need ' + id + ' to ' + username + '\'s funding basket')),
        catchError(this.handleError<Need>('getHelper', undefined))
      );
  }

  /** GET function to checkout needs from basket */
  checkout(username: string): Observable<Need> {
    return this.http.get<Need>(this.helperUrl + "/checkout?username=" + username)
        .pipe(
            tap(_ => this.log('checked out needs for ' + username)),
            catchError(this.handleError<Need>('checkout', undefined))
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

  /** Log a HelperService message with the MessageService */
  private log(message: string) {
    this.messageService.add(`HelperService: ${message}`);
  }
}
