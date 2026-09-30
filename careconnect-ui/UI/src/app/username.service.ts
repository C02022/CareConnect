import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {MessageService} from "./message.service";
import {Observable, of} from "rxjs";
import {catchError, tap} from "rxjs/operators";
import {Helper} from "./helper";

/**
 * Stores information regarding the current logged-in User.
 */
@Injectable({
  providedIn: 'root'
})
export class UsernameService {
  // Stores User's username, empty by default
  private username = ''

  // URL for authenticating a user/password
  private authenticateURL = "http://localhost:8080/helpers/login?username="
  // URL for creating an account
  private createAccountURL = "http://localhost:8080/helpers"

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };
  constructor(
    private http: HttpClient,
    private messageService: MessageService
  ) {}

  /**
   * Authenticates a helper's username and password
   * @param user - User's username
   * @param password - User's password
   */
  authenticate(user: string, password: string): Observable<boolean> {
    return this.http.get<boolean>(this.authenticateURL + user + "&password=" + password)
      .pipe(
        tap(_ => this.log("Authenticated user " + user + " with result: " + _)),
        catchError(this.handleError<boolean>("Authenticate " + user))
      );
  }

  /**
   * Creates a new helper account in the backend
   * @param helper - Helper with username/password to be created
   */
  createAccount(helper: Helper): Observable<Helper> {
    return this.http.post<Helper>(this.createAccountURL, helper, this.httpOptions)
      .pipe(
        tap((newHelper: Helper) => this.log(`Added Helper ` + helper.username)),
        catchError(this.handleError<Helper>('createAccount'))
      );
  }

  /**
   * Stores the User's Username for use across components.
   * @param user - User's Username
   */
  setUsername(user: string): void {
    this.username = user;
  }

  /**
   * Gets the logged-in User's username.
   * @returns - the Username of the current logged-in User.
   */
  getUsername(): string {
    return this.username;
  }

  /**
   * Resets the logged-in User to empty. Used during Log-out.
   */
  resetUsername(): void {
    this.username = '';
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
