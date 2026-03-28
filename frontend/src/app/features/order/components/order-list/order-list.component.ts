import { Component } from '@angular/core';

@Component({
  selector: 'app-order-list',
  template: `
    <div class="row">
      <div class="col-12">
        <h2>Your Orders</h2>
        <p>Order history will be displayed here.</p>
      </div>
    </div>
  `,
  standalone: false
})
export class OrderListComponent {}
