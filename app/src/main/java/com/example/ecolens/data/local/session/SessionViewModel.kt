package com.example.ecolens.data.local.session

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SessionViewModel(private val sessionManager: SessionManager) : ViewModel() {

    private val _userEmail = mutableStateOf<String?>(null)
    val userEmail: State<String?> = _userEmail

    init {
        viewModelScope.launch {
            val email = sessionManager.getLoggedUserEmail()
            _userEmail.value = email
        }
    }

    fun clearSession() {
        viewModelScope.launch {
            sessionManager.logout()
            _userEmail.value = null
        }
    }
}
