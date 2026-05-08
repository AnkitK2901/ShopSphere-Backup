import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { CartService } from '../../core/services/cart.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  isLoggedIn$ = this.authService.isLoggedIn$;
  cartItemCount: number = 0;
  isDropdownOpen: boolean = false;
  userRole: string = '';

  constructor(
    public authService: AuthService,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    // Listen to the cart to update the badge number
    this.cartService.cartItems$.subscribe(items => {
      this.cartItemCount = items.length;
    });

    // Get role to show correct links in dropdown
    this.authService.isLoggedIn$.subscribe(loggedIn => {
      if (loggedIn) {
        this.userRole = this.authService.getUserRole();
      }
    });
  }

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  // Closes dropdown if user clicks a link inside it
  closeDropdown(): void {
    this.isDropdownOpen = false;
  }

  logout(): void {
    this.closeDropdown();
    this.authService.logout();
  }
}