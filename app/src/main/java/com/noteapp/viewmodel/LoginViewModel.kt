package com.noteapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noteapp.data.model.SessionManager
import com.noteapp.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        // Basic validation
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Please fill in all fields")
            return
        }

        if (!isValidEmail(email)) {
            _loginState.value = LoginState.Error("Please enter a valid email address")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            authRepository.login(email, password)
                .onSuccess { user ->
                    sessionManager.saveUser(user.id, user.email)
                    _loginState.value = LoginState.Success
                }
                .onFailure { exception ->
                    _loginState.value = LoginState.Error(
                        exception.message ?: "Login failed"
                    )
                }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}