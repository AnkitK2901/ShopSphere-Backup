import { Component, OnInit } from '@angular/core';
import { ToastService, ToastMessage } from './core/services/toast.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'ShopSphere-Frontend-v16';
  toasts: ToastMessage[] = [];

  constructor(private toastService: ToastService) {}

  ngOnInit() {
    this.toastService.toast$.subscribe(toast => {
      this.toasts.push(toast);
      // Auto-remove after 3.5 seconds
      setTimeout(() => this.toasts.shift(), 3500);
    });
  }

  removeToast(index: number) {
    this.toasts.splice(index, 1);
  }
}