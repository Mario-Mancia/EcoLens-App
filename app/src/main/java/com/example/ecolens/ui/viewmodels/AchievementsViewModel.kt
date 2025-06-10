package com.example.ecolens.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecolens.data.local.entities.AchievementsEntity
import com.example.ecolens.data.local.dao.ArchievementsDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AchievementsViewModel(private val achievementsDao: ArchievementsDao) : ViewModel() {

    private val _achievements = MutableStateFlow<List<AchievementsEntity>>(emptyList())
    val achievements: StateFlow<List<AchievementsEntity>> = _achievements.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadAchievements() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                achievementsDao.getAllAchievements().collect { list ->
                    _achievements.value = list
                    _error.value = null
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar logros: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class AchievementsViewModelFactory(
    private val achievementsDao: ArchievementsDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AchievementsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AchievementsViewModel(achievementsDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}