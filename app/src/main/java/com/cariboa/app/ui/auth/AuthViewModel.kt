package com.cariboa.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cariboa.app.data.repository.AuthRepository
import com.cariboa.app.data.repository.UserRepository
import com.cariboa.app.domain.model.SubscriptionTier
import com.cariboa.app.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val firebaseUser = authRepository.signInWithGoogle(idToken)
                val existingUser = userRepository.getUser()
                if (existingUser == null) {
                    userRepository.createUser(User(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        displayName = firebaseUser.displayName ?: "",
                        photoUrl = firebaseUser.photoUrl?.toString(),
                        subscriptionTier = SubscriptionTier.TRIAL,
                        subscriptionExpiry = null,
                        interests = emptyList(),
                    ))
                }
                _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                authRepository.signInWithEmail(email, password)
                _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun createAccount(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val firebaseUser = authRepository.createAccountWithEmail(email, password)
                userRepository.createUser(User(
                    uid = firebaseUser.uid,
                    email = email,
                    displayName = "",
                    photoUrl = null,
                    subscriptionTier = SubscriptionTier.TRIAL,
                    subscriptionExpiry = null,
                    interests = emptyList(),
                ))
                _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
