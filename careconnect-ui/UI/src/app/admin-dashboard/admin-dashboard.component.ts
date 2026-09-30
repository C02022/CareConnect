import { Component, OnInit } from '@angular/core';
import { Need } from '../need'
import { NeedService } from '../need.service';

/**
 * Defines the functions used by the Admin Dashboard GUI.
 */
@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  // The cupboard
  needs: Need[] = [];
  // The pending requests
  requests: Need[] = [];

  // Dictates display for Add-Need input box
  showAddNeedForm = false;
  // Dictates display for Edit-Need input boxes
  showEditNeedList: boolean[] = [];
  // Whether the requests are displayed
  displayRequests: boolean = false;
  // Is the requests array empty?
  requestsAreEmpty: boolean = false;

  constructor (private needService: NeedService) {}

  // Get the cupboard when page is initialized
  ngOnInit(): void {
    this.getNeeds();
  }

  /**
   * Puts the contents of the cupboard within the needs array.
   * Puts the pending requests within the requests array and evaluates whether it is empty.
   */
  getNeeds(): void {
    this.needService.getNeeds()
      .subscribe(needs => this.needs = needs);

    this.needService.getRequestedNeeds()
      .subscribe(requests => {
        this.requests = requests;
        if(this.requests.length == 0) this.requestsAreEmpty = true;
        console.log(this.requests.length);
      });


  }

  /**
   * Adds a need to the cupboard from user input.
   * @param name - Name of Need
   * @param type - Type of Need
   * @param quantity - Need Quantity
   * @param cost - Need Cost
   */
  add(name: string, type: string, quantity: number, cost: number): void {
    name = name.trim();
    if (!name) { return; }
    this.needService.addNeed({ name, type, quantity, cost } as Need)
      .subscribe(_ => {
        this.getNeeds()
      });
  }

  /**
   * Replaces a Need with the User's input.
   * @param id - ID of Need
   * @param name - Name of Need
   * @param type - Type of Need
   * @param quantity - Need Quantity
   * @param cost - Need Cost
   */
  update(index: number, id: number, name: string, type: string, quantity: number, cost: number): void {
    name = name.trim();
    if(!name) { return; }
    this.needService.updateNeed({ id, name, type, quantity, cost } as Need)
      .subscribe(_ => {
        this.getNeeds();
      })

    this.toggleEditNeedForm(index);
  }

  /**
   * Deletes the selected Need from the Cupboard.
   * @param need - The Need to be deleted
   */
  delete(need: Need): void {
    this.needService.deleteNeed(need.id).subscribe(_ => {this.getNeeds()});
  }

  /**
   * Approves a requested need to be added to the cupboard
   * @param need - Need to be approved
   */
  approve(need: Need) {
    this.needService.addNeed(need)
      .subscribe(_ => {
      this.getNeeds()
    })

    this.needService.deleteRequestedNeed(need.id)
      .subscribe(_ => {
        this.getNeeds();
      })
  }

  /**
   * Denies a requested need, deleting it from the requests file
   * @param need - Need to be denied
   */
  deny(need: Need) {
    this.needService.deleteRequestedNeed(need.id).subscribe(_ => {this.getNeeds()});
  }

  /**
   * Toggles whether an Edit-Need input box is displayed.
   * @param index - the index of the Need array that is to be edited.
   */
  toggleEditNeedForm(index: number) {
    this.showEditNeedList[index] = !this.showEditNeedList[index];
  }

  protected readonly Number = Number;
}
