package com.cariboa.app.ui.onboarding

import androidx.lifecycle.ViewModel
import com.cariboa.app.domain.model.TravelInterest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class OnboardingUiState(
    val currentPage: Int = 0,
    val selectedInterests: Set<TravelInterest> = emptySet(),
    val isComplete: Boolean = false,
)

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun toggleInterest(interest: TravelInterest) {
        _uiState.update {
            val updated = it.selectedInterests.toMutableSet()
            if (interest in updated) updated.remove(interest) else updated.add(interest)
            it.copy(selectedInterests = updated)
        }
    }

    fun completeOnboarding() {
        _uiState.update { it.copy(isComplete = true) }
    }
}
