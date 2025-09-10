# MyLocationTracker

An **Android-based** location tracking application that integrates **Google Maps SDK** and **ML Kit scanning functionality**. It provides features such as location recording, route planning, and QR code location sharing.

## ✨ Features

- **Real-time Location**
    
    Get the user’s current location and display it on the map.
    
- **Route Planning**
    
    Tap on the map to set multiple markers, and the total distance will be calculated automatically.
    
- **Geocoding**
    - Enter a place name → convert it to latitude/longitude and display it on the map
    - Enter latitude/longitude → convert it to an actual address and display it
- **QR Code Features**
    - Generate QR code: convert the current location (latitude/longitude) into a QR code for easy sharing
    - Scan QR code: extract location information from a QR code and display it on the map
- **UI Components**
    - `RecyclerView` supports list, grid, and staggered grid layouts
    - Custom scanning interface (300x300 scanning frame)
    - Dynamic map markers, circular overlays, and boundary constraints

## 📲 Usage Guide

1. Clone the project
    
    ```bash
    git clone <https://github.com/dogwang9/MyLocationTracker.git>
    cd MyLocationTracker
    
    ```
    
2. Configure the Google Maps API Key  
   Create a `secrets.properties` file in the project root directory and add:
    
    ```bash
    MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY
    
    ```
    
3. Open the project in Android Studio and ensure the following are installed:
    - Android SDK 34+
    - Google Play services
    - AndroidX libraries
4. Run the app, grant location permissions, and start using it.

