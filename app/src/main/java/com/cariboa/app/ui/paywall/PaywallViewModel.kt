package com.cariboa.app.ui.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cariboa.app.data.repository.SubscriptionRepository
import com.cariboa.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaywallUiState(
    val isLoading: Boolean = true,
    val isPurchasing: Boolean = false,
    val error: String? = null,
    val isWelcomeBack: Boolean = false,
)

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val subscriptionRepository: SubscriptionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaywallUiState())
    val uiState: StateFlow<PaywallUiState> = _uiState.asStateFlow()

    init {
        loadSubscriptionState()
    }

    private fun loadSubscriptionState() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val user = userRepository.getUser()
                // "Welcome back" variant: user was previously Pro (has a stored expiry) but is now on Trial
                val isWelcomeBack = user?.subscriptionExpiry != null
                _uiState.update { it.copy(isLoading = false, isWelcomeBack = isWelcomeBack) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onSubscribeClicked(productId: String) {
        // Placeholder: actual Google Play Billing launch requires BillingClient
        // and is wired up from the Activity/Fragment via a callback.
        // Here we simply set isPurchasing = true and simulate a no-op for now.
        viewModelScope.launch {
            _uiState.update { it.copy(isPurchasing = true, error = null) }
            // TODO: launch BillingClient.launchBillingFlow(activity, billingFlowParams)
            _uiState.update { it.copy(isPurchasing = false) }
        }
    }

    fun onRestorePurchases() {
        viewModelScope.launch {
            _uiState.update { it.copy(isPurchasing = true, error = null) }
            try {
                // TODO: query BillingClient.queryPurchasesAsync to restore entitlements
                subscriptionRepository.checkAndUpdateSubscriptionStatus()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isPurchasing = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
