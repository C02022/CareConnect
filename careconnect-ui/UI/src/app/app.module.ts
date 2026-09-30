import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { LoginComponent } from './login/login.component';
import { HelperDashboardComponent } from './helper-dashboard/helper-dashboard.component';
import { FundingBasketComponent } from './funding-basket/funding-basket.component';
import { HttpClientModule } from '@angular/common/http';
import { RecipientDashboardComponent } from './recipient-dashboard/recipient-dashboard.component';
import { LandingPageComponent } from './landing-page/landing-page.component';

@NgModule({
  declarations: [
    AppComponent,
    AdminDashboardComponent,
    LoginComponent,
    HelperDashboardComponent,
    FundingBasketComponent,
    RecipientDashboardComponent,
    LandingPageComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
