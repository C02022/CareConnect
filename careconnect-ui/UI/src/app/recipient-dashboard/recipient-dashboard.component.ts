import { Component } from '@angular/core';
import {NeedService} from "../need.service";
import {Need} from "../need";

/**
 * Defines the functions used by the Recipient Dashboard GUI.
 */
@Component({
  selector: 'app-recipient-dashboard',
  templateUrl: './recipient-dashboard.component.html',
  styleUrls: ['./recipient-dashboard.component.css']
})
export class RecipientDashboardComponent {
  // Current Requests
  needs: Need[] = [];

  constructor (private needService: NeedService) {}

  // Get the requests when page is initialized
  ngOnInit(): void {
    this.getNeeds();
  }

  /**
   * Puts the contents of the cupboard within the needs array.
   */
  getNeeds(): void {
    this.needService.getRequestedNeeds()
      .subscribe(needs => this.needs = needs);
  }

  /**
   * Adds a need to the requested needs file from user input.
   * @param name - Name of Need
   * @param type - Type of Need
   * @param quantity - Need Quantity
   * @param cost - Need Cost
   */
  add(name: string, type: string, quantity: number, cost: number): void {
    name = name.trim();
    if (!name) { return; }
    this.needService.addRequestedNeed({ name, type, quantity, cost } as Need)
      .subscribe(need => {
        this.getNeeds();
      });
  }

  protected readonly Number = Number;
}
