import { Component, OnInit } from '@angular/core';
import { loadStripe } from '@stripe/stripe-js';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-checkout',
  template: `
    <div class="container mt-4">
      <h2>Checkout</h2>
      <p>Stripe Payment Integration Placeholder.</p>
      <button mat-raised-button color="accent" (click)="pay()">Pay Now</button>
    </div>
  `,
  standalone: false
})
export class CheckoutComponent implements OnInit {
  stripePromise = loadStripe(environment.stripePk);

  ngOnInit() {}

  async pay() {
    console.log('Initiating Stripe checkout...');
    // Stripe Element Logic Here
  }
}
