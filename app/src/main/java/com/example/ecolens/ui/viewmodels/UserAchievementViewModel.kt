package com.example.ecolens.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecolens.data.local.dao.UserArchievementsDao
import com.example.ecolens.data.local.entities.UserAchievementsEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserAchievementsViewModel(
    private val userAchievementsDao: UserArchievementsDao
) : ViewModel() {

    private val _userAchievements = MutableStateFlow<List<UserAchievementsEntity>>(emptyList())
    val userAchievements: StateFlow<List<UserAchievementsEntity>> = _userAchievements.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadUserAchievements(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userAchievementsDao.getAchievementsByUser(userId).collect { list ->
                    _userAchievements.value = list
                    _error.value = null
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar logros del usuario: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class UserAchievementsViewModelFactory(
    private val userAchievementsDao: UserArchievementsDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserAchievementsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserAchievementsViewModel(userAchievementsDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}