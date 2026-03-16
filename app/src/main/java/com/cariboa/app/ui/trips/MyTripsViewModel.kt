package com.cariboa.app.ui.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cariboa.app.domain.model.Trip
import com.cariboa.app.domain.usecase.DeleteTripUseCase
import com.cariboa.app.domain.usecase.GetTripsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyTripsUiState(
    val trips: List<Trip> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class MyTripsViewModel @Inject constructor(
    private val getTripsUseCase: GetTripsUseCase,
    private val deleteTripUseCase: DeleteTripUseCase,
) : ViewModel() {

    val uiState: StateFlow<MyTripsUiState> = getTripsUseCase()
        .map { trips -> MyTripsUiState(trips = trips, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MyTripsUiState())

    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            deleteTripUseCase(tripId)
        }
    }
}
