package com.cariboa.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cariboa.app.data.repository.TripRepository
import com.cariboa.app.domain.model.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class TrendingDestination(val name: String, val country: String)

data class HomeUiState(
    val recentTrips: List<Trip> = emptyList(),
    val trendingDestinations: List<TrendingDestination> = DEFAULT_TRENDING,
)

private val DEFAULT_TRENDING = listOf(
    TrendingDestination("Tokyo", "Japan"),
    TrendingDestination("Barcelona", "Spain"),
    TrendingDestination("Bali", "Indonesia"),
    TrendingDestination("Cape Town", "South Africa"),
    TrendingDestination("Reykjavik", "Iceland"),
    TrendingDestination("Medellin", "Colombia"),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    tripRepository: TripRepository,
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = tripRepository.getLocalTrips()
        .map { trips -> HomeUiState(recentTrips = trips.take(5)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())
}
