package com.example.ecolens.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecolens.data.local.dao.UserDao
import com.example.ecolens.data.local.entities.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val userDao: UserDao) : ViewModel() {

    private val _registerSuccess = MutableStateFlow<Boolean?>(null)
    val registerSuccess: StateFlow<Boolean?> = _registerSuccess

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun registerUser(
        username: String,
        email: String,
        country: String,
        gender: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            if (username.isBlank() || email.isBlank() || country.isBlank() ||
                gender.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                _errorMessage.value = "Todos los campos son obligatorios"
                _registerSuccess.value = false
                return@launch
            }

            if (password != confirmPassword) {
                _errorMessage.value = "Las contraseñas no coinciden"
                _registerSuccess.value = false
                return@launch
            }

            val emailExists = userDao.isEmailRegistered(email) > 0
            if (emailExists) {
                _errorMessage.value = "El correo ya está registrado"
                _registerSuccess.value = false
                return@launch
            }

            val newUser = UserEntity(
                username = username,
                email = email,
                country = country,
                gender = gender,
                password = password
            )

            val result = userDao.insertUser(newUser)
            _registerSuccess.value = result > 0
        }
    }

    fun resetState() {
        _registerSuccess.value = null
        _errorMessage.value = null
    }
}