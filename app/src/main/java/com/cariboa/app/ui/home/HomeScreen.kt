package com.cariboa.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cariboa.app.domain.model.Trip
import com.cariboa.app.ui.components.boa.BoaAnimation
import com.cariboa.app.ui.components.boa.BoaAnimationState
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    onPlanTrip: () -> Unit,
    onTripClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
    ) {
        HeroCard(onPlanTrip = onPlanTrip)

        if (uiState.recentTrips.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            RecentTripsSection(
                trips = uiState.recentTrips,
                onTripClick = onTripClick,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        TrendingDestinationsSection(destinations = uiState.trendingDestinations)
    }
}

@Composable
private fun HeroCard(onPlanTrip: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BoaAnimation(
                state = BoaAnimationState.Idle,
                size = 160.dp,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Where to next?",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Let Boa plan your perfect adventure",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onPlanTrip,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Plan a Trip",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun RecentTripsSection(
    trips: List<Trip>,
    onTripClick: (String) -> Unit,
) {
    Column {
        Text(
            text = "Recent Trips",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(trips) { trip ->
                TripCard(trip = trip, onClick = { onTripClick(trip.id) })
            }
        }
    }
}

@Composable
private fun TripCard(trip: Trip, onClick: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("MMM d", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = trip.destination,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (trip.startDate > 0 && trip.endDate > 0) {
                Text(
                    text = "${dateFormat.format(Date(trip.startDate))} \u2013 ${dateFormat.format(Date(trip.endDate))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = trip.status.name.lowercase()
                        .replaceFirstChar { it.uppercaseChar() },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun TrendingDestinationsSection(destinations: List<TrendingDestination>) {
    Column {
        Text(
            text = "Trending Destinations",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(destinations) { destination ->
                DestinationCard(destination = destination)
            }
        }
    }
}

@Composable
private fun DestinationCard(destination: TrendingDestination) {
    Card(
        modifier = Modifier.width(150.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = destination.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = destination.country,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            )
        }
    }
}
