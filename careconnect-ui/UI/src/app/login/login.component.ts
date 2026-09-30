import { Component } from '@angular/core';
import { UsernameService } from '../username.service';
import { Need } from '../need'
import {Helper} from "../helper";


/**
 * Defines the functions used by the Login GUI
 */
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  // Whether the GUI element for invalid credentials is displayed.
  loginError = false;
  // Whether the Admin's login success element is displayed.
  isAdmin = false;
  // Whether the Helper's login success element is displayed.
  isHelper = false;
  // Whether the account creation forms are displayed.
  makingAccount = false;
  // Stores the Username for use by the username service.
  username = "";
  // Checks whether login creds are correct.
  authenticated = false;
  // Confirms whether account was created or not.
  accountCreated = false;
  // Confirms whether account was created or not.
  creationFailure = false;

  constructor(private usernameService: UsernameService){}

  /**
   * Passes along the entered username to the next page when this page is destroyed.
   */
  ngOnDestroy(): void {
    this.usernameService.setUsername(this.username);
  }

  /**
   * Resets the page to its initial display.
   */
  resetCredentials(){
    this.loginError = false;
    this.isAdmin = false;
    this.isHelper = false;
  }

  /**
   * Toggles the Account Creation forms.
   */
  toggleAccountForm(){
    this.makingAccount = !this.makingAccount;
  }

  /**
   * Creates a helper account.
   * @param username - Helper's username
   * @param password - Helper's password
   */
  createAccount(username: string, password: string) {
    this.usernameService.createAccount({username, password} as Helper)
      .subscribe(helper => {
        this.username = helper.username;
        this.accountCreated = true;
        this.creationFailure = false;
      })

    if(!this.accountCreated) this.creationFailure = true;
  }

  /**
   * Verifies credentials of the User.
   * @param user - Username of the user. "Admin" is an admin.
   * @param password - Password of the user. "Admin" is the admin password
   */
  login(user: string, password: string){
    this.username = user.toLowerCase();
    this.resetCredentials();
    this.usernameService.authenticate(user, password).subscribe(answer => {
      this.authenticated = answer;
      if (user.toLowerCase() == "admin"){
        if(password == "admin") this.isAdmin = true;
        else this.loginError = true;
        return;
      }
      else if (user != "" && this.authenticated){
        this.isHelper = true;
        return;
      }
      else{
        this.loginError = true;
      }
    });
  }

}
