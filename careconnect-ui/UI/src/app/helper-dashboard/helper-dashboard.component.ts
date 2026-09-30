import { Component, OnInit } from '@angular/core';
import { Need } from '../need';
import { NeedService } from '../need.service';
import { UsernameService } from '../username.service';
import {HelperService} from "../helper.service";

/**
 * Defines the functions used by the Helper Dashboard GUI.
 */
@Component({
  selector: 'app-helper-dashboard',
  templateUrl: './helper-dashboard.component.html',
  styleUrls: ['./helper-dashboard.component.css']
})
export class HelperDashboardComponent {
  // All needs in cupboard - each array is sorted on type
  needs: Need[] = [];
  operation: Need[] = [];
  medication: Need[] = [];
  equipment: Need[] = [];
  other: Need[] = [];
  // Variable used to store an ID as a number.
  idQuery: number = 0;
  // Displays a success message for any Need added to cupboard.
  showSuccessOperation: boolean[] = [];
  showSuccessMedication: boolean[] = [];
  showSuccessEquipment: boolean[] = [];
  showSuccessOther: boolean[] = [];
  showSuccessMatching: boolean[] = [];
  // Success message for an added Need searched by ID query.
  idQueryAddedSuccess: boolean = false;
  // Needs with the matching search name/ID.
  matchingNeeds: Need[] = [];
  isSearch: boolean = false;
  // Initializes a variable to store Helper's username.
  username: string = "";

  constructor (private needService: NeedService,
               private usernameService: UsernameService,
               private helperService: HelperService) {}

  // Get the logged-in Helper's Username when page is initialized.
  ngOnInit(): void {
    this.getNeeds();
    this.username = this.usernameService.getUsername();
  }

  /**
   * Clears the logged-in Username upon logging out.
   */
  logout(){
    this.usernameService.resetUsername();
  }

  /**
   * Gets the current Cupboard.
   */
  getNeeds(): void {
    this.needService.getNeeds()
      .subscribe(needs => {
        this.needs = needs;
        this.sortNeeds();
      }
      );
  }

  /**
   * Sorts needs into correct array based on type
   */
  sortNeeds(): void {
    this.operation = [];
    this.medication = [];
    this.equipment = [];
    this.other = [];

    for (let i = 0; i < this.needs.length; i++) {
      if(this.needs[i].type == "OPERATION") this.operation.push(this.needs[i]);
      else if(this.needs[i].type == "MEDICATION") this.medication.push(this.needs[i]);
      else if(this.needs[i].type == "EQUIPMENT") this.equipment.push(this.needs[i]);
      else this.other.push(this.needs[i]);
    }
  }

  /**
   * Adds a Need to the Helper's Funding Basket.
   * @param id - ID of the Need to add
   * @param index - Index of the Need, used for handling GUI display upon adding.
   * @param type - Type of need, operation equipment medication or other
   */
  addNeedToBasket(id: number, index: number, type: string): void {
    this.toggleShowSuccess(index, type);
    this.helperService.addNeedToBasket(this.username, id)
      .subscribe();
  }

  /**
   * Searches for a Need by Name.
   * @param term - The search term, a Name.
   */
  searchNeeds(term: string): void {
    this.matchingNeeds = [];
    if (term.trim() == ""){
      this.getNeeds();
      return;
    }
    this.needService.searchNeeds(term)
      .subscribe(needs => {
        this.matchingNeeds = needs
        this.isSearch = true;
      });
  }

  /**
   * Searches for a Need by ID
   * @param id - The ID of the Need
   */
  searchNeedID(id: number): void {
    this.matchingNeeds = [];
    for(let i = 0; i < this.needs.length; i++) {
      if(this.needs[i].id == id) this.matchingNeeds.push(this.needs[i]);
    }
    this.isSearch = true;
  }

  /**
   * Toggles the success display of an added Need and removes the Add Need button.
   * @param index - index of added Need in Needs. ID Queries have a special index -1.
   * @param type - type of need
   */
  toggleShowSuccess(index: number, type: string) {
    if (index == -1){
      this.idQueryAddedSuccess = true;
      return;
    }

    if(type == "operation") this.showSuccessOperation[index] = !this.showSuccessOperation[index];
    if(type == "medication") this.showSuccessMedication[index] = !this.showSuccessMedication[index];
    if(type == "equipment") this.showSuccessEquipment[index] = !this.showSuccessEquipment[index];
    if(type == "other") this.showSuccessOther[index] = !this.showSuccessOther[index];
    if(type == "matching") this.showSuccessMatching[index] = !this.showSuccessMatching[index];
  }

  protected readonly Number = Number;
}
