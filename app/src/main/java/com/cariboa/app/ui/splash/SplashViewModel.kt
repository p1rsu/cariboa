package com.cariboa.app.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cariboa.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _authReady = MutableStateFlow(false)
    val authReady: StateFlow<Boolean> = _authReady.asStateFlow()

    private var timerComplete = false
    private var authChecked = false

    init {
        viewModelScope.launch {
            authRepository.authState.collect { user ->
                _isLoggedIn.value = user != null
                authChecked = true
                maybeNavigate()
            }
        }
    }

    fun markTimerComplete() {
        timerComplete = true
        maybeNavigate()
    }

    private fun maybeNavigate() {
        if (timerComplete && authChecked) {
            _authReady.value = true
        }
    }
}
