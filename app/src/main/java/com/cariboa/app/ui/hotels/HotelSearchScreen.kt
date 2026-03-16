package com.cariboa.app.ui.hotels

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cariboa.app.domain.model.Hotel
import com.cariboa.app.ui.components.boa.BoaAnimation
import com.cariboa.app.ui.components.boa.BoaAnimationState

private val PRICE_LEVEL_LABELS = listOf("Any", "$", "$$", "$$$", "$$$$")
private val RATING_OPTIONS = listOf(null, 3.0, 3.5, 4.0, 4.5)
private val RATING_LABELS = listOf("Any", "3+", "3.5+", "4+", "4.5+")

@Composable
fun HotelSearchScreen(
    initialDestination: String = "",
    onNavigateToPaywall: () -> Unit,
    viewModel: HotelSearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showPaywall) {
        onNavigateToPaywall()
        viewModel.dismissPaywall()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Hotel Search",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Find the perfect hotel for your stay",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.destination,
            onValueChange = viewModel::onDestinationChange,
            label = { Text("Destination") },
            placeholder = { Text("e.g. Paris, France") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = uiState.checkIn,
                onValueChange = viewModel::onCheckInChange,
                label = { Text("Check-in") },
                placeholder = { Text("YYYY-MM-DD") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
            )
            OutlinedTextField(
                value = uiState.checkOut,
                onValueChange = viewModel::onCheckOutChange,
                label = { Text("Check-out") },
                placeholder = { Text("YYYY-MM-DD") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { viewModel.search() }),
            )
        }

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = uiState.error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Price level",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        )

        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(PRICE_LEVEL_LABELS.indices.toList()) { index ->
                val priceValue = if (index == 0) null else index
                val selected = uiState.priceLevel == priceValue
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.onPriceLevelChange(if (selected) null else priceValue) },
                    label = { Text(PRICE_LEVEL_LABELS[index]) },
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Minimum rating",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        )

        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(RATING_OPTIONS.indices.toList()) { index ->
                val ratingValue = RATING_OPTIONS[index]
                val selected = uiState.minRating == ratingValue
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.onMinRatingChange(if (selected) null else ratingValue) },
                    label = { Text(RATING_LABELS[index]) },
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = viewModel::search,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading,
        ) {
            Text("Search Hotels")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        BoaAnimation(state = BoaAnimationState.Searching)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Searching for hotels...",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }

            uiState.hasSearched && uiState.results.isEmpty() && uiState.error == null -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        BoaAnimation(state = BoaAnimationState.Confused)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No hotels found for this search.",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            text = "Try adjusting your filters or dates.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        )
                    }
                }
            }

            uiState.hasSearched && uiState.results.isNotEmpty() -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                ) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                BoaAnimation(
                                    state = BoaAnimationState.Excited,
                                    size = 120.dp,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${uiState.results.size} hotels found!",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                    items(uiState.results) { hotel ->
                        HotelCard(hotel = hotel)
                    }
                }
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 32.dp),
                    ) {
                        BoaAnimation(state = BoaAnimationState.Idle)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Enter a destination and dates to find hotels",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HotelCard(hotel: Hotel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    text = hotel.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "\$".repeat(hotel.priceLevel.coerceIn(1, 4)),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                RatingStars(rating = hotel.rating)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "%.1f".format(hotel.rating),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }
        }
    }
}

@Composable
private fun RatingStars(rating: Double) {
    val fullStars = rating.toInt().coerceIn(0, 5)
    val hasHalf = (rating - fullStars) >= 0.5
    val emptyStars = (5 - fullStars - if (hasHalf) 1 else 0).coerceAtLeast(0)
    Row {
        repeat(fullStars) {
            Text(text = "\u2605", color = MaterialTheme.colorScheme.primary)
        }
        if (hasHalf) {
            Text(text = "\u00BD", color = MaterialTheme.colorScheme.primary)
        }
        repeat(emptyStars) {
            Text(text = "\u2606", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
        }
    }
}
