import { Component, OnInit, OnDestroy, HostListener, ElementRef, ViewChild } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { CartService } from '../../core/services/cart.service';
import { Observable, Subscription } from 'rxjs';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {
  isLoggedIn$!: Observable<boolean>;
  userRole: string = '';
  cartItemCount: number = 0;
  isDropdownOpen: boolean = false;
  
  private authSub!: Subscription;
  private cartSub!: Subscription;

  // FIX: Reference to the dropdown menu in the HTML
  @ViewChild('profileMenuRef') profileMenuRef!: ElementRef;

  constructor(
    private authService: AuthService,
    private cartService: CartService
  ) {}

  // FIX: Listens for clicks anywhere on the website. If the click is OUTSIDE the menu, close it.
  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (this.isDropdownOpen && this.profileMenuRef && !this.profileMenuRef.nativeElement.contains(event.target)) {
      this.isDropdownOpen = false;
    }
  }

  ngOnInit(): void {
    this.isLoggedIn$ = this.authService.isLoggedIn$;
    this.authSub = this.authService.isLoggedIn$.subscribe(status => {
      this.userRole = status ? this.authService.getUserRole() : '';
    });
    this.cartSub = this.cartService.cartItems$.subscribe(items => {
      this.cartItemCount = items.reduce((count, item) => count + item.quantity, 0);
    });
  }

  // FIX: Added event parameter to stop the click from bubbling up to the Document Listener
  toggleDropdown(event: Event): void {
    event.stopPropagation();
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  closeDropdown(): void {
    this.isDropdownOpen = false;
  }

  logout(): void {
    this.closeDropdown();
    this.authService.logout();
  }

  ngOnDestroy(): void {
    if (this.authSub) this.authSub.unsubscribe();
    if (this.cartSub) this.cartSub.unsubscribe();
  }
}