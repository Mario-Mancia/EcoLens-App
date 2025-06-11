package com.example.ecolens.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecolens.data.local.entities.StepsEntity
import com.example.ecolens.data.local.dao.StepsDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class StepsViewModel(private val stepsDao: StepsDao) : ViewModel() {

    private val _stepsList = MutableStateFlow<List<StepsEntity>>(emptyList())
    val stepsList: StateFlow<List<StepsEntity>> = _stepsList.asStateFlow()

    private val _totalSteps = MutableStateFlow(0)
    val totalSteps: StateFlow<Int> = _totalSteps.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _todaySteps = MutableStateFlow<StepsEntity?>(null)
    val todaySteps: StateFlow<StepsEntity?> = _todaySteps.asStateFlow()

    fun loadSteps(userId: Int) {
        viewModelScope.launch {
            try {
                stepsDao.getStepsByUser(userId).collect { steps ->
                    _stepsList.value = steps
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar los pasos: ${e.message}"
            }
        }
    }

    fun loadTotalSteps(userId: Int) {
        viewModelScope.launch {
            try {
                val total = stepsDao.getTotalSteps(userId) ?: 0
                _totalSteps.value = total
            } catch (e: Exception) {
                _error.value = "Error al obtener pasos totales: ${e.message}"
            }
        }
    }

    fun insertOrUpdateSteps(userId: Int, stepCount: Int, date: LocalDate) {
        viewModelScope.launch {
            try {
                val existing = stepsDao.getStepsByDate(userId, date)
                if (existing != null) {
                    val updated = existing.copy(stepCount = stepCount)
                    stepsDao.updateSteps(updated)
                } else {
                    val newSteps = StepsEntity(userId = userId, stepCount = stepCount, date = date.toString())
                    stepsDao.insertSteps(newSteps)
                }
                loadSteps(userId)
                loadTotalSteps(userId)
            } catch (e: Exception) {
                _error.value = "Error al guardar pasos: ${e.message}"
            }
        }
    }

    fun loadTodaySteps(userId: Int, date: LocalDate) {
        viewModelScope.launch {
            try {
                _todaySteps.value = stepsDao.getStepsByDate(userId, date)
            } catch (e: Exception) {
                _error.value = "Error al obtener pasos de hoy: ${e.message}"
            }
        }
    }
}

class StepsViewModelFactory(private val stepsDao: StepsDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StepsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StepsViewModel(stepsDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}