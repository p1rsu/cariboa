package com.cariboa.app.ui.hiddengems

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cariboa.app.domain.model.HiddenGem
import com.cariboa.app.domain.model.TravelInterest
import com.cariboa.app.domain.usecase.CheckUsageLimitsUseCase
import com.cariboa.app.domain.usecase.FindHiddenGemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HiddenGemsUiState(
    val destination: String = "",
    val selectedCategories: Set<TravelInterest> = emptySet(),
    val results: List<HiddenGem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showPaywall: Boolean = false,
    val hasSearched: Boolean = false,
)

@HiltViewModel
class HiddenGemsViewModel @Inject constructor(
    private val findHiddenGems: FindHiddenGemsUseCase,
    private val checkUsageLimits: CheckUsageLimitsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HiddenGemsUiState())
    val uiState: StateFlow<HiddenGemsUiState> = _uiState.asStateFlow()

    fun onDestinationChange(value: String) {
        _uiState.update { it.copy(destination = value, error = null) }
    }

    fun toggleCategory(category: TravelInterest) {
        _uiState.update { state ->
            val updated = if (category in state.selectedCategories) {
                state.selectedCategories - category
            } else {
                state.selectedCategories + category
            }
            state.copy(selectedCategories = updated)
        }
    }

    fun dismissPaywall() {
        _uiState.update { it.copy(showPaywall = false) }
    }

    fun search() {
        val destination = _uiState.value.destination.trim()
        if (destination.isBlank()) {
            _uiState.update { it.copy(error = "Please enter a destination") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val allowed = runCatching { checkUsageLimits.canSearchHiddenGems() }.getOrDefault(false)
            if (!allowed) {
                _uiState.update { it.copy(isLoading = false, showPaywall = true) }
                return@launch
            }

            val categories = _uiState.value.selectedCategories
                .takeIf { it.isNotEmpty() }
                ?.map { it.name.lowercase() }

            runCatching { findHiddenGems(destination, categories) }
                .onSuccess { gems ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            results = gems,
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
