package com.cariboa.app.ui.wizard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cariboa.app.data.remote.dto.GenerateItineraryRequest
import com.cariboa.app.domain.model.BudgetLevel
import com.cariboa.app.domain.model.TravelInterest
import com.cariboa.app.domain.usecase.CheckUsageLimitsUseCase
import com.cariboa.app.domain.usecase.GenerateItineraryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class WizardUiState(
    val currentStep: Int = 0,
    val destination: String = "",
    val startDate: Long? = null,
    val endDate: Long? = null,
    val travelers: Int = 1,
    val interests: Set<TravelInterest> = emptySet(),
    val budgetLevel: BudgetLevel = BudgetLevel.MODERATE,
    val isGenerating: Boolean = false,
    val generatedTripId: String? = null,
    val showPaywall: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class WizardViewModel @Inject constructor(
    private val checkUsageLimits: CheckUsageLimitsUseCase,
    private val generateItinerary: GenerateItineraryUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WizardUiState())
    val uiState: StateFlow<WizardUiState> = _uiState.asStateFlow()

    fun setDestination(destination: String) {
        _uiState.update { it.copy(destination = destination) }
    }

    fun setDates(startDate: Long?, endDate: Long?) {
        _uiState.update { it.copy(startDate = startDate, endDate = endDate) }
    }

    fun setTravelers(travelers: Int) {
        _uiState.update { it.copy(travelers = travelers.coerceAtLeast(1)) }
    }

    fun toggleInterest(interest: TravelInterest) {
        _uiState.update { state ->
            val updated = if (interest in state.interests) {
                state.interests - interest
            } else {
                state.interests + interest
            }
            state.copy(interests = updated)
        }
    }

    fun setBudget(budgetLevel: BudgetLevel) {
        _uiState.update { it.copy(budgetLevel = budgetLevel) }
    }

    fun nextStep() {
        _uiState.update { it.copy(currentStep = (it.currentStep + 1).coerceAtMost(3)) }
    }

    fun previousStep() {
        _uiState.update { it.copy(currentStep = (it.currentStep - 1).coerceAtLeast(0)) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun dismissPaywall() {
        _uiState.update { it.copy(showPaywall = false) }
    }

    fun generate() {
        val state = _uiState.value
        if (state.destination.isBlank()) {
            _uiState.update { it.copy(error = "Please enter a destination") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, error = null) }
            try {
                val canGenerate = checkUsageLimits.canGenerateItinerary()
                if (!canGenerate) {
                    _uiState.update { it.copy(isGenerating = false, showPaywall = true) }
                    return@launch
                }

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val startDateStr = state.startDate?.let { dateFormat.format(Date(it)) } ?: ""
                val endDateStr = state.endDate?.let { dateFormat.format(Date(it)) } ?: ""

                val request = GenerateItineraryRequest(
                    destination = state.destination,
                    startDate = startDateStr,
                    endDate = endDateStr,
                    travelers = state.travelers,
                    interests = state.interests.map { it.name.lowercase() },
                    budgetLevel = state.budgetLevel.name.lowercase(),
                )

                val trip = generateItinerary(request)
                _uiState.update { it.copy(isGenerating = false, generatedTripId = trip.id) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isGenerating = false, error = e.message ?: "Failed to generate itinerary")
                }
            }
        }
    }
}
