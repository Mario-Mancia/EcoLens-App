package com.example.ecolens.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolens.data.local.dao.UserDao
import com.example.ecolens.data.local.entities.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.ecolens.data.local.session.SessionManager

class LoginViewModel(
    private val userDao: UserDao,
    private val context: Context
) : ViewModel() {

    private val _loginSuccess = MutableStateFlow<Boolean?>(null)
    val loginSuccess: StateFlow<Boolean?> = _loginSuccess

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user: UserEntity? = userDao.getUserByEmail(email)

            if (user != null && user.password == password) {
                // Guardar la sesión
                SessionManager(context).saveLoginSession(user.email)
                _loginSuccess.value = true
            } else {
                _loginSuccess.value = false
            }
        }
    }

    fun resetLoginState() {
        _loginSuccess.value = null
    }
}