package com.cariboa.app.ui.itinerary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cariboa.app.data.repository.TripRepository
import com.cariboa.app.domain.model.Trip
import com.cariboa.app.domain.model.TripStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItineraryUiState(
    val trip: Trip? = null,
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class ItineraryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tripRepository: TripRepository,
) : ViewModel() {
    private val tripId: String = savedStateHandle["tripId"] ?: ""

    private val _uiState = MutableStateFlow(ItineraryUiState())
    val uiState: StateFlow<ItineraryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            tripRepository.getLocalTrips()
                .map { trips -> trips.find { it.id == tripId } }
                .collect { trip ->
                    _uiState.update {
                        it.copy(trip = trip, isLoading = false,
                            isSaved = trip?.status == TripStatus.SAVED || trip?.status == TripStatus.ACTIVE || trip?.status == TripStatus.COMPLETED)
                    }
                }
        }
    }

    fun saveTrip() {
        viewModelScope.launch {
            val trip = _uiState.value.trip ?: return@launch
            tripRepository.saveTrip(trip.copy(status = TripStatus.SAVED, updatedAt = System.currentTimeMillis()))
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
