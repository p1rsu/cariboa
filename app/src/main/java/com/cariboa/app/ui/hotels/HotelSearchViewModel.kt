package com.cariboa.app.ui.hotels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cariboa.app.domain.model.Hotel
import com.cariboa.app.domain.usecase.CheckUsageLimitsUseCase
import com.cariboa.app.domain.usecase.SearchHotelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HotelSearchUiState(
    val destination: String = "",
    val checkIn: String = "",
    val checkOut: String = "",
    val priceLevel: Int? = null,
    val minRating: Double? = null,
    val results: List<Hotel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showPaywall: Boolean = false,
    val hasSearched: Boolean = false,
)

@HiltViewModel
class HotelSearchViewModel @Inject constructor(
    private val searchHotels: SearchHotelsUseCase,
    private val checkUsageLimits: CheckUsageLimitsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HotelSearchUiState())
    val uiState: StateFlow<HotelSearchUiState> = _uiState.asStateFlow()

    fun onDestinationChange(value: String) {
        _uiState.update { it.copy(destination = value, error = null) }
    }

    fun onCheckInChange(value: String) {
        _uiState.update { it.copy(checkIn = value, error = null) }
    }

    fun onCheckOutChange(value: String) {
        _uiState.update { it.copy(checkOut = value, error = null) }
    }

    fun onPriceLevelChange(value: Int?) {
        _uiState.update { it.copy(priceLevel = value) }
    }

    fun onMinRatingChange(value: Double?) {
        _uiState.update { it.copy(minRating = value) }
    }

    fun dismissPaywall() {
        _uiState.update { it.copy(showPaywall = false) }
    }

    fun search() {
        val state = _uiState.value
        val destination = state.destination.trim()
        val checkIn = state.checkIn.trim()
        val checkOut = state.checkOut.trim()

        if (destination.isBlank()) {
            _uiState.update { it.copy(error = "Please enter a destination") }
            return
        }
        if (checkIn.isBlank()) {
            _uiState.update { it.copy(error = "Please enter a check-in date") }
            return
        }
        if (checkOut.isBlank()) {
            _uiState.update { it.copy(error = "Please enter a check-out date") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val allowed = runCatching { checkUsageLimits.canSearchHotels() }.getOrDefault(false)
            if (!allowed) {
                _uiState.update { it.copy(isLoading = false, showPaywall = true) }
                return@launch
            }

            runCatching {
                searchHotels(destination, checkIn, checkOut, state.priceLevel, state.minRating)
            }
                .onSuccess { hotels ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            results = hotels,
                            hasSearched = true,
                            error = null,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Something went wrong",
                            hasSearched = true,
                        )
                    }
                }
        }
    }
}
