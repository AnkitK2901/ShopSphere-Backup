import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LogisticsService } from '../../../core/services/logistics.service';

@Component({
  selector: 'app-packing',
  templateUrl: './packing.component.html',
  styleUrls: ['./packing.component.css']
})
export class PackingComponent implements OnInit {
  shipment: any = null;
  isLoading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private logisticsService: LogisticsService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.logisticsService.getShipmentById(id).subscribe({
        next: (data) => {
          this.shipment = data;
          this.isLoading = false;
        },
        error: (err) => {
          console.error(err);
          alert('Failed to load shipment details.');
          this.router.navigate(['/logistics/queue']);
        }
      });
    }
  }

  markAsPacked(): void {
    if (this.shipment) {
      // FIX: Using the newly secured updateStatus method
      this.logisticsService.updateStatus(this.shipment.orderId, 'PACKED').subscribe({
        next: () => {
          alert('Shipment marked as PACKED. Ready for dispatch.');
          this.router.navigate(['/logistics/queue']);
        },
        error: (err) => {
          console.error(err);
          alert('Failed to update status.');
        }
      });
    }
  }
}