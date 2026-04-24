import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink], // This allows us to use clickable links in the HTML
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss'
})
export class Navbar {}