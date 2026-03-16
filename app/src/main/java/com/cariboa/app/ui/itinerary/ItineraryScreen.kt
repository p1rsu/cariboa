package com.cariboa.app.ui.itinerary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cariboa.app.domain.model.Activity
import com.cariboa.app.domain.model.HiddenGem
import com.cariboa.app.domain.model.Hotel
import com.cariboa.app.domain.model.ItineraryDay
import com.cariboa.app.domain.model.TravelInterest
import com.cariboa.app.ui.components.boa.BoaAnimation
import com.cariboa.app.ui.components.boa.BoaAnimationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryScreen(
    onBack: () -> Unit,
    viewModel: ItineraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.trip?.destination ?: "Itinerary",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    if (uiState.isSaved) {
                        BoaAnimation(
                            state = BoaAnimationState.Celebrating,
                            size = 48.dp,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                    } else if (!uiState.isLoading && uiState.trip != null) {
                        TextButton(onClick = viewModel::saveTrip) {
                            Text(
                                text = "Save",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            when {
                uiState.isLoading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        BoaAnimation(state = BoaAnimationState.Thinking)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading your itinerary...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        )
                    }
                }

                uiState.error != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp),
                    ) {
                        BoaAnimation(state = BoaAnimationState.Confused)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.error!!,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }

                uiState.trip != null -> {
                    val trip = uiState.trip!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 24.dp),
                    ) {
                        trip.itinerary.forEach { day ->
                            DaySection(day = day)
                        }

                        if (trip.hotels.isNotEmpty()) {
                            HotelsSection(hotels = trip.hotels)
                        }

                        if (trip.hiddenGems.isNotEmpty()) {
                            HiddenGemsSection(gems = trip.hiddenGems)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DaySection(day: ItineraryDay) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Day ${day.day}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        )

        day.activities.forEach { activity ->
            ActivityCard(activity = activity)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ActivityCard(activity: Activity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
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
                    text = activity.time,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                InterestChip(interest = activity.type)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = activity.title,
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = activity.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
private fun HotelsSection(hotels: List<Hotel>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Hotels",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        )

        hotels.forEach { hotel ->
            HotelCard(hotel = hotel)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun HotelCard(hotel: Hotel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = hotel.name,
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                RatingStars(rating = hotel.rating)
                PriceLevelIndicator(priceLevel = hotel.priceLevel)
            }
        }
    }
}

@Composable
private fun RatingStars(rating: Double) {
    val fullStars = rating.toInt()
    val hasHalf = (rating - fullStars) >= 0.5
    val totalStars = 5

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = buildString {
                repeat(fullStars.coerceAtMost(totalStars)) { append("\u2605") }
                if (hasHalf && fullStars < totalStars) append("\u00BD")
                val displayed = fullStars + if (hasHalf) 1 else 0
                repeat((totalStars - displayed).coerceAtLeast(0)) { append("\u2606") }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = String.format("%.1f", rating),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
    }
}

@Composable
private fun PriceLevelIndicator(priceLevel: Int) {
    val filled = priceLevel.coerceIn(0, 4)
    val empty = 4 - filled
    Text(
        text = "\$".repeat(filled) + "\$".repeat(empty).let { grey -> grey },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    )
}

@Composable
private fun HiddenGemsSection(gems: List<HiddenGem>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Hidden Gems",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        )

        gems.forEach { gem ->
            GemCard(gem = gem)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun GemCard(gem: HiddenGem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
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
                    text = gem.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                InterestChip(interest = gem.category)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = gem.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )

            if (!gem.aiReason.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = gem.aiReason,
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                )
            }
        }
    }
}

@Composable
private fun InterestChip(interest: TravelInterest) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(
            text = interest.name.lowercase()
                .replaceFirstChar { it.uppercaseChar() },
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}
