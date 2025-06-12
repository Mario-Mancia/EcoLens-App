package com.example.ecolens.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecolens.data.local.dao.UserDao
import com.example.ecolens.data.local.entities.UserEntity
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserViewModel(private val userDao: UserDao) : ViewModel() {

    private val _user = mutableStateOf<UserEntity?>(null)
    val user: State<UserEntity?> = _user

    fun loadUserByEmail(email: String) {
        viewModelScope.launch {
            val userEntity = userDao.getUserByEmail(email)
            _user.value = userEntity
        }
    }
    fun updateUsername(userId: Int, newUsername: String) {
        viewModelScope.launch {
            userDao.updateUsername(userId, newUsername)
            _user.value = userDao.getUserById(userId) // refresca
        }
    }

    fun updatePassword(userId: Int, currentPassword: String, newPassword: String): Boolean {
        return runBlocking {
            val user = userDao.getUserById(userId)
            if (user != null && user.password == currentPassword) {
                userDao.updatePassword(userId, newPassword)
                _user.value = userDao.getUserById(userId)
                true
            } else {
                false
            }
        }
    }
}

class UserViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(userDao) as T
    }
}