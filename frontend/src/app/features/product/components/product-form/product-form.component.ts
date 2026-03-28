import { Component } from '@angular/core';
import { ProductService } from '../../../../core/services/product.service';

@Component({
  selector: 'app-product-form',
  template: `
    <div class="row">
      <div class="col-12">
        <h2>Manage Product</h2>
        <p>Admin product form placeholder.</p>
      </div>
    </div>
  `,
  standalone: false
})
export class ProductFormComponent {
  constructor(private productService: ProductService) {}
}
