# Stylist Shopping Application

A modern Android e-commerce application built with Kotlin and Jetpack Compose, featuring a comprehensive shopping experience with user authentication, product browsing, cart management, and order processing.

## Table of Contents
- [Project Overview](#project-overview)
- [Key Features](#key-features)
- [Screenshots](#screenshots)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Features Implemented](#features-implemented)
- [Installation](#installation)
- [Contributing](#contributing)

## Project Overview

The Stylist Shopping Application is a full-featured e-commerce Android app that provides users with a seamless shopping experience. Built with modern Android development practices, it includes user authentication, product browsing, cart management, wishlist functionality, and a complete checkout process.

## Key Features

### Authentication
- Firebase Authentication integration
- Google Sign-In support
- Email/password registration and login
- Profile management with image upload

### Product Browsing
- Category-based product navigation
- Featured and trending products display
- Product search functionality
- Detailed product views with images and descriptions

### Shopping Experience
- Add/remove products from cart
- Wishlist functionality
- Multi-step checkout process
- Order history tracking

### UI/UX
- Modern Material Design 3 implementation
- Responsive layouts for all screen sizes
- Smooth animations and transitions
- Dark theme support

## Screenshots

*(Add screenshots of your app here)*

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room Database, Firebase Firestore
- **Authentication**: Firebase Authentication
- **Image Loading**: Coil
- **Navigation**: Jetpack Navigation
- **Build System**: Gradle with Kotlin DSL
- **Dependency Injection**: Manual DI
- **Testing**: JUnit, Espresso

## Architecture

The app follows the MVVM architectural pattern with a clean separation of concerns:

```
app/
├── data/
│   ├── local/           # Room Database entities and DAOs
│   ├── remote/          # Firebase integration
│   └── repository/      # Data repository implementations
├── domain/
│   ├── model/           # Data models
│   └── repository/      # Repository interfaces
├── presentation/
│   ├── screens/         # Compose UI screens
│   ├── ViewModel/       # ViewModels for UI logic
│   └── scafold/         # Custom scaffold components
└── ui/
    └── theme/           # Material theme definitions
```

## Features Implemented

### 1. User Authentication
- Firebase Authentication with email/password
- Google Sign-In integration
- User profile management
- Profile image upload and storage

### 2. Home Screen
- Auto-scrolling banner section with page indicators
- Category browsing (Beauty, Fashion, Kids, Mens, Womens)
- Featured products horizontal scrolling
- Deal of the Day section
- Special Offers card
- Flat and Heels section
- Trending products display
- Summer Sale banner with animations

### 3. Product Catalog
- Product listing by categories
- Search functionality
- Product detail screens
- Image gallery support

### 4. Shopping Cart
- Add/remove products
- Quantity adjustment
- Real-time price calculation
- Persistent storage

### 5. Wishlist
- Save favorite products
- Quick access to desired items

### 6. Checkout Process
- Address management
- Payment method selection
- Order summary
- Order confirmation

### 7. Order Management
- Order history
- Order status tracking
- Reorder functionality

### 8. Profile Management
- Personal information editing
- Profile image management
- Settings configuration

### 9. UI Components
- Custom animated banners
- Product cards with wishlist and cart buttons
- Category chips
- Search bar with suggestions
- Bottom navigation
- Custom dialogs and alerts

## Installation

### Prerequisites
- Android Studio Arctic Fox or later
- Kotlin 1.5+
- Android SDK API level 21+

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/StylistShoppingApplication.git
   ```

2. Open the project in Android Studio

3. Add your Firebase configuration file (`google-services.json`) to the `app/` directory

4. Build and run the project

### Firebase Setup
1. Create a new Firebase project at https://console.firebase.google.com/
2. Add an Android app to your Firebase project
3. Download the `google-services.json` file
4. Place the file in the `app/` directory
5. Enable Authentication methods (Email/Password, Google Sign-In)
6. Set up Firestore database

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Icons provided by Material Design
- Images from various free sources
- Inspired by modern e-commerce applications