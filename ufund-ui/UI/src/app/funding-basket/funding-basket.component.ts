import { Component } from '@angular/core';
import {HelperService} from "../helper.service";
import {UsernameService} from "../username.service";
import {Helper} from "../helper";

/**
 * Defines the functions used by the Funding Basket GUI.
 */
@Component({
  selector: 'app-funding-basket',
  templateUrl: './funding-basket.component.html',
  styleUrls: ['./funding-basket.component.css']
})
export class FundingBasketComponent {
  // Used to store the logged-in Helper's Username
  username: string = "";
  // Helper object representing the logged-in Helper.
  helper!: Helper;
  // Boolean variable to store whether checkout occurred or not
  checkoutSuccess: boolean = false;

  constructor (private helperService: HelperService, private usernameService: UsernameService) {}

  // Initializes the logged-in Helper's data.
  ngOnInit(): void {
    this.username = this.usernameService.getUsername();
    this.getHelper(this.username);
  }

  /**
   * Gets the data of the Helper currently logged in.
   * @param username - Username of the logged-in Helper.
   */
  getHelper(username: string): void {
    this.helperService.getHelper(username)
      .subscribe(helper => this.helper = helper);
  }

  /**
   * Removes a Need from the Helper's Funding Basket.
   * @param id - ID of the Need to be removed
   */
  removeNeed(id: number): void {
    this.helperService.removeNeedFromBasket(this.username, id).subscribe(
      need => {
        this.getHelper(this.username)
      }
    );
  }

  /**
   * Performs a check-out on the Helper's Funding Basket.
   */
  checkout(): void {
    this.helperService.checkout(this.username).subscribe(
      need => {
        this.getHelper(this.username)
        this.checkoutSuccess = true;
      }
    );
  }
}
