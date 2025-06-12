package com.example.ecolens.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecolens.data.local.dao.UserStatsDao
import com.example.ecolens.data.local.entities.UserStatsEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class UserStatsViewModel(
    private val userStatsDao: UserStatsDao
) : ViewModel() {

    private val _stats = MutableStateFlow<UserStatsEntity?>(null)
    val stats: StateFlow<UserStatsEntity?> = _stats.asStateFlow()

    fun loadStatsByUserId(userId: Int) {
        viewModelScope.launch {
            userStatsDao.getStatsByUser(userId).collect { statsEntity ->
                if (statsEntity != null) {
                    _stats.value = statsEntity
                } else {
                    val newStats = UserStatsEntity(userId = userId)
                    userStatsDao.insertStats(newStats)
                    _stats.value = newStats
                }
            }
        }
    }

    fun incrementRecyclings(userId: Int, increment: Int) {
        viewModelScope.launch {
            userStatsDao.incrementRecycling(userId, increment)
        }
    }

    fun addEcoPoints(userId: Int, points: Int) {
        viewModelScope.launch {
            userStatsDao.addEcoPoints(userId, points)
        }
    }

    fun addSteps(userId: Int, steps: Int) {
        viewModelScope.launch {
            userStatsDao.addSteps(userId, steps)
        }
    }

    fun incrementAchievements(userId: Int, increment: Int) {
        viewModelScope.launch {
            userStatsDao.incrementAchievements(userId, increment)
        }
    }
}

class UserStatsViewModelFactory(
    private val userStatsDao: UserStatsDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserStatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserStatsViewModel(userStatsDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}