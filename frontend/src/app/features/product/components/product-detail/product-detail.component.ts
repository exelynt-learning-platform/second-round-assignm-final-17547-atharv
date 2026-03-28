import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductService } from '../../../../core/services/product.service';
import { Product } from '../../../../core/models/product.model';

@Component({
  selector: 'app-product-detail',
  template: `
    <div class="container" *ngIf="product">
      <h2>{{ product.name }}</h2>
      <p class="lead">{{ product.price | currency }}</p>
      <p>{{ product.description }}</p>
      <p>Stock: {{ product.stock }}</p>
      <button mat-raised-button color="primary">Add to Cart</button>
    </div>
  `,
  standalone: false
})
export class ProductDetailComponent implements OnInit {
  product: Product | undefined;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.productService.getProduct(id).subscribe(p => this.product = p);
    }
  }
}
