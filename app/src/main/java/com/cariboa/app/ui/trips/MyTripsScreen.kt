package com.cariboa.app.ui.trips

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cariboa.app.domain.model.Trip
import com.cariboa.app.domain.model.TripStatus
import com.cariboa.app.ui.components.boa.BoaAnimation
import com.cariboa.app.ui.components.boa.BoaAnimationState
import com.cariboa.app.ui.theme.ForestGreen
import com.cariboa.app.ui.theme.WarmBrown
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MyTripsScreen(
    onTripClick: (String) -> Unit,
    onPlanTrip: () -> Unit,
    viewModel: MyTripsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(title = { Text("My Trips") })
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.trips.isEmpty() -> {
                    EmptyTripsState(onPlanTrip = onPlanTrip)
                }

                else -> {
                    TripsList(
                        trips = uiState.trips,
                        onTripClick = onTripClick,
                        onDeleteTrip = { tripId -> viewModel.deleteTrip(tripId) },
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyTripsState(onPlanTrip: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BoaAnimation(state = BoaAnimationState.Confused, size = 180.dp)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No trips yet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your saved adventures will appear here",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onPlanTrip,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Plan your first trip")
        }
    }
}

@Composable
private fun TripsList(
    trips: List<Trip>,
    onTripClick: (String) -> Unit,
    onDeleteTrip: (String) -> Unit,
) {
    var tripPendingDelete by remember { mutableStateOf<Trip?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(trips, key = { it.id }) { trip ->
            SwipeToDeleteTripCard(
                trip = trip,
                onClick = { onTripClick(trip.id) },
                onDelete = { tripPendingDelete = trip },
            )
        }
    }

    tripPendingDelete?.let { trip ->
        DeleteConfirmationDialog(
            trip = trip,
            onConfirm = {
                onDeleteTrip(trip.id)
                tripPendingDelete = null
            },
            onDismiss = { tripPendingDelete = null },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteTripCard(
    trip: Trip,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
            }
            false // don't actually dismiss — dialog handles deletion
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
                    MaterialTheme.colorScheme.errorContainer
                else
                    Color.Transparent,
                label = "swipe_bg",
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(color),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete trip",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(end = 20.dp),
                )
            }
        },
        enableDismissFromStartToEnd = false,
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        TripCard(trip = trip, onClick = onClick)
    }
}

@Composable
private fun TripCard(trip: Trip, onClick: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = trip.destination,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                Spacer(modifier = Modifier.width(8.dp))

                TripStatusChip(status = trip.status)
            }

            if (trip.startDate > 0 && trip.endDate > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${dateFormat.format(Date(trip.startDate))} \u2013 ${dateFormat.format(Date(trip.endDate))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
        }
    }
}

@Composable
private fun TripStatusChip(status: TripStatus) {
    val (backgroundColor, contentColor) = when (status) {
        TripStatus.DRAFT -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        TripStatus.SAVED -> Color(0xFF1565C0).copy(alpha = 0.15f) to Color(0xFF1565C0)
        TripStatus.ACTIVE -> ForestGreen.copy(alpha = 0.15f) to ForestGreen
        TripStatus.COMPLETED -> WarmBrown.copy(alpha = 0.15f) to WarmBrown
    }

    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(
            text = status.name.lowercase().replaceFirstChar { it.uppercaseChar() },
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    trip: Trip,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            BoaAnimation(state = BoaAnimationState.Confused, size = 80.dp)
        },
        title = {
            Text("Delete trip?")
        },
        text = {
            Text(
                text = "Are you sure you want to delete your trip to ${trip.destination}? This cannot be undone.",
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
