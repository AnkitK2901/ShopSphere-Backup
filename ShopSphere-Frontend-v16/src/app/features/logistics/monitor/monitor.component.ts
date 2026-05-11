import { Component, OnInit } from '@angular/core';
import { LogisticsService } from '../../../core/services/logistics.service';

@Component({
  selector: 'app-monitor',
  templateUrl: './monitor.component.html',
  styleUrls: ['./monitor.component.css']
})
export class MonitorComponent implements OnInit {
  allShipments: any[] = [];
  isLoading: boolean = true;

  constructor(private logisticsService: LogisticsService) {}

  ngOnInit(): void {
    this.logisticsService.getAllShipments().subscribe({
      next: (data) => {
        // Sort newest first
        this.allShipments = data ? data.sort((a: any, b: any) => b.id - a.id) : [];
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load global monitor', err);
        this.isLoading = false;
      }
    });
  }
}