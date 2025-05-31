package com.noteapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noteapp.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ) {
        // Basic validation
        if (email.isBlank() || password.isBlank() || firstName.isBlank() || lastName.isBlank()) {
            _registerState.value = RegisterState.Error("Please fill in all fields")
            return
        }

        if (!isValidEmail(email)) {
            _registerState.value = RegisterState.Error("Please enter a valid email address")
            return
        }

        if (password.length < 6) {
            _registerState.value = RegisterState.Error("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            authRepository.register(email, password, firstName, lastName)
                .onSuccess {
                    _registerState.value = RegisterState.Success
                }
                .onFailure { exception ->
                    _registerState.value = RegisterState.Error(
                        exception.message ?: "Registration failed"
                    )
                }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}