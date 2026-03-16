package com.cariboa.app.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cariboa.app.ui.auth.AuthScreen
import com.cariboa.app.ui.hiddengems.HiddenGemsScreen
import com.cariboa.app.ui.home.HomeScreen
import com.cariboa.app.ui.itinerary.ItineraryScreen
import com.cariboa.app.ui.onboarding.OnboardingScreen
import com.cariboa.app.ui.wizard.WizardScreen

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
        composable(Screen.Home.route) {
            HomeScreen(
                onPlanTrip = { navController.navigate(Screen.Wizard.route) },
                onTripClick = { tripId -> navController.navigate(Screen.Itinerary.createRoute(tripId)) },
            )
        }
        composable(Screen.MyTrips.route) { PlaceholderScreen("My Trips") }
        composable(Screen.HiddenGems.route) {
            HiddenGemsScreen(
                onNavigateToPaywall = { navController.navigate(Screen.Paywall.route) },
            )
        }
        composable(Screen.Profile.route) { PlaceholderScreen("Profile") }
        composable(Screen.Wizard.route) {
            WizardScreen(
                onNavigateToItinerary = { tripId ->
                    navController.navigate(Screen.Itinerary.createRoute(tripId)) {
                        popUpTo(Screen.Wizard.route) { inclusive = true }
                    }
                },
                onNavigateToPaywall = { navController.navigate(Screen.Paywall.route) },
            )
        }
        composable(
            Screen.Itinerary.route,
            arguments = listOf(navArgument("tripId") { type = NavType.StringType }),
        ) {
            ItineraryScreen(onBack = { navController.popBackStack() })
        }
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
