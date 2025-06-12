package com.example.ecolens.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecolens.data.local.entities.AchievementsEntity
import com.example.ecolens.data.local.dao.ArchievementsDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
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

    fun ensureDefaultAchievementsExist() {
        viewModelScope.launch {
            val existing = achievementsDao.getAllAchievements().firstOrNull()
            if (existing.isNullOrEmpty()) {
                val defaultAchievements = listOf(
                    AchievementsEntity(name = "Reciclador principiante", description = "Clasifica tu primer residuo con EcoLens.", type = "reciclaje"),
                    AchievementsEntity(name = "Maestro del plástico", description = "Has clasificado un total de 50 ítems de plástico.", type = "reciclaje"),
                    AchievementsEntity(name = "Héroe del vidrio", description = "Tu compromiso con el vidrio es cristalino.", type = "reciclaje"),
                    AchievementsEntity(name = "Caminante urbano", description = "Has dado tus primeros 1,000 pasos Eco.", type = "pasos"),
                    AchievementsEntity(name = "Activista del reciclaje", description = "Alcanza los 500 Puntos Eco.", type = "reciclaje"),
                    AchievementsEntity(name = "Explorador sostenible", description = "Has escaneado tu primer código QR de producto/punto sostenible.", type = "qr"),
                    AchievementsEntity(name = "Consumidor consciente", description = "Has escaneado 5 códigos QR de productos sostenibles diferentes.", type = "qr"),
                    AchievementsEntity(name = "Pionero EcoLens", description = "Uno de los primeros 10 usuarios en unirse a la comunidad EcoLens.", type = "otros"),
                    AchievementsEntity(name = "EcoUsuario activo", description = "Abre la app durante 3 días consecutivos.", type = "otros"),
                    AchievementsEntity(name = "Primer paso Eco", description = "Registra tu primer paso con el sensor.", type = "pasos")
                )
                achievementsDao.insertAllAchievements(defaultAchievements)
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