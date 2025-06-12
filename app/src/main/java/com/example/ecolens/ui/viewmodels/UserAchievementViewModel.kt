package com.example.ecolens.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecolens.data.local.dao.UserArchievementsDao
import com.example.ecolens.data.local.entities.UserAchievementsEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    private val _newAchievementEvent = MutableSharedFlow<UserAchievementsEntity>()
    val newAchievementEvent = _newAchievementEvent.asSharedFlow()

    private val _achievementCount = MutableStateFlow(0)
    val achievementCount: StateFlow<Int> = _achievementCount

    fun loadUserAchievements(userId: Int) {
        userAchievementsDao.getAchievementsByUser(userId)
            .onEach { list ->
                Log.d("UserAchievementsVM", "Cargados logros: ${list.size}")
                _userAchievements.value = list
                _error.value = null
            }
            .catch { e ->
                _error.value = "Error al cargar logros del usuario: ${e.message}"
                Log.e("UserAchievementsVM", "Error al cargar logros", e)
            }
            .launchIn(viewModelScope)
    }
    fun insertUserAchievement(userAchievement: UserAchievementsEntity) {
        viewModelScope.launch {
            try {
                val existing = userAchievementsDao.getUserAchievement(
                    userAchievement.userId,
                    userAchievement.achievementId
                )
                if (existing == null) {
                    userAchievementsDao.insertUserAchievement(userAchievement)

                    // Vuelve a cargar después de insertar
                    loadUserAchievements(userAchievement.userId)

                    _error.value = null
                }
            } catch (e: Exception) {
                _error.value = "Error al insertar logro del usuario: ${e.message}"
            }
        }
    }

    fun insertUserAchievementIfNotExists(ua: UserAchievementsEntity, unlocked: Set<Int>) {
        viewModelScope.launch {
            try {
                if (!unlocked.contains(ua.achievementId)) {
                    val existing = userAchievementsDao.getUserAchievement(
                        ua.userId,
                        ua.achievementId
                    )
                    if (existing == null) {
                        userAchievementsDao.insertUserAchievement(ua)

                        // Emitimos el evento solo si se insertó exitosamente
                        _newAchievementEvent.emit(ua)

                        // Recargamos logros
                        loadUserAchievements(ua.userId)

                        _error.value = null
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error al insertar logro del usuario: ${e.message}"
            }
        }
    }

    fun countUserAchievements(userId: Int) {
        viewModelScope.launch {
            try {
                _achievementCount.value = userAchievementsDao.countAchievementsByUser(userId)
            } catch (e: Exception) {
                _error.value = "Error al contar logros: ${e.message}"
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