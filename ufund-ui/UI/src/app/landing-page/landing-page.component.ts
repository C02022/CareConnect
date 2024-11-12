import { Component } from '@angular/core';
import { Need } from '../need'
import { Helper } from '../helper';
import { NeedService } from '../need.service';
import { HelperService } from '../helper.service';

/**
 * Defines the functions used by the Landing Page GUI.
 */
@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.css']
})
export class LandingPageComponent {
  // Total of all Funded Needs:
  totalNeeds: number = 0;
  // Sum of all Funded Needs' costs
  sumCosts: number = 0;
  // Total of all Helper accounts
  totalHelpers: number = 0;
  // Our Cupboard
  needs: Need[] = [];
  // Our Helpers
  helpers: Helper[] = []

  constructor (private needService: NeedService, private helperService: HelperService) {}

  // Gets info needed for statistics
  ngOnInit() {
    this.getFundedNeeds();
    this.getHelpers();
  }

  /**
   * Puts the data of all helpers within the helpers array and counts them.
   */
  getHelpers(): void {
    this.helperService.getHelpers()
      .subscribe(helpers => {
        this.helpers = helpers
        this.totalHelpers = helpers.length;
      });
  }

  /**
   * Puts the data of all funded needs within the needs array and calculates stats.
   */
  getFundedNeeds(): void {
    this.needService.getFundedNeeds()
      .subscribe(needs => {
        this.needs = needs
        this.calculateStats(this.needs);
      });
  }

  /**
   * Calculates sum of funded Needs, sum of all costs, and sum of helpers
   * @param fundedNeeds - Needs that have been funded
   */
  calculateStats(fundedNeeds: Need[]): void {
    this.totalHelpers = this.helpers.length;
    for (const index in fundedNeeds){
      this.totalNeeds = this.totalNeeds + fundedNeeds[index].quantity;
      this.sumCosts = this.sumCosts + (fundedNeeds[index].cost * fundedNeeds[index].quantity);
    }
  }

}
