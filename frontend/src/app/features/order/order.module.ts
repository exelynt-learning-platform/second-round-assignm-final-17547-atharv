import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { OrderListComponent } from './components/order-list/order-list.component';
import { CheckoutComponent } from './components/checkout/checkout.component';

@NgModule({
  declarations: [OrderListComponent, CheckoutComponent],
  imports: [
    CommonModule,
    SharedModule,
    RouterModule.forChild([
      { path: '', component: OrderListComponent },
      { path: 'checkout', component: CheckoutComponent }
    ])
  ]
})
export class OrderModule { }
