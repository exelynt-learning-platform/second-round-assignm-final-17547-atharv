import { Component } from '@angular/core';

@Component({
  selector: 'app-cart',
  template: `
    <div class="container mt-4">
      <h2>Your Shopping Cart</h2>
      <p>Cart features placeholder. Items will be displayed here.</p>
      <button mat-raised-button color="primary" routerLink="/orders/checkout">Proceed to Checkout</button>
    </div>
  `,
  standalone: false
})
export class CartComponent {}
