package com.cariboa.app.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Auth : Screen("auth")
    data object Home : Screen("home")
    data object MyTrips : Screen("my_trips")
    data object HiddenGems : Screen("hidden_gems")
    data object Profile : Screen("profile")
    data object Wizard : Screen("wizard")
    data object Itinerary : Screen("itinerary/{tripId}") {
        fun createRoute(tripId: String) = "itinerary/$tripId"
    }
    data object HotelSearch : Screen("hotel_search/{destination}") {
        fun createRoute(destination: String) = "hotel_search/$destination"
    }
    data object Paywall : Screen("paywall")
}
