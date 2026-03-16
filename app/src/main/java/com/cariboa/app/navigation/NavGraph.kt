package com.cariboa.app.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cariboa.app.ui.auth.AuthScreen
import com.cariboa.app.ui.onboarding.OnboardingScreen

@Composable
fun CaribouNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { PlaceholderScreen("Splash") }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(onComplete = {
                navController.navigate(Screen.Auth.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Auth.route) {
            AuthScreen(onAuthenticated = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Auth.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Home.route) { PlaceholderScreen("Home") }
        composable(Screen.MyTrips.route) { PlaceholderScreen("My Trips") }
        composable(Screen.HiddenGems.route) { PlaceholderScreen("Hidden Gems") }
        composable(Screen.Profile.route) { PlaceholderScreen("Profile") }
        composable(Screen.Wizard.route) { PlaceholderScreen("Trip Wizard") }
        composable(Screen.Itinerary.route) { PlaceholderScreen("Itinerary") }
        composable(Screen.HotelSearch.route) { PlaceholderScreen("Hotel Search") }
        composable(Screen.Paywall.route) { PlaceholderScreen("Paywall") }
    }
}

@Composable
private fun PlaceholderScreen(name: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(name, style = MaterialTheme.typography.headlineMedium)
    }
}
