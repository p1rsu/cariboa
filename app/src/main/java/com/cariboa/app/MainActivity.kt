package com.cariboa.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cariboa.app.navigation.CaribouNavGraph
import com.cariboa.app.navigation.Screen
import com.cariboa.app.ui.theme.CaribouTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CaribouTheme {
                val navController = rememberNavController()
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

                val showBottomBar = currentRoute in listOf(
                    Screen.Home.route, Screen.MyTrips.route,
                    Screen.HiddenGems.route, Screen.Profile.route,
                )

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            CaribouBottomBar(currentRoute) { route ->
                                navController.navigate(route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }
                ) { padding ->
                    Box(Modifier.padding(padding)) {
                        CaribouNavGraph(navController)
                    }
                }
            }
        }
    }
}

private data class NavItem(val label: String, val icon: ImageVector, val route: String)

@Composable
private fun CaribouBottomBar(currentRoute: String?, onNavigate: (String) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        val items = listOf(
            NavItem("Home", Icons.Default.Home, Screen.Home.route),
            NavItem("My Trips", Icons.Default.DateRange, Screen.MyTrips.route),
            NavItem("Gems", Icons.Default.LocationOn, Screen.HiddenGems.route),
            NavItem("Profile", Icons.Default.Person, Screen.Profile.route),
        )
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
            )
        }
    }
}
