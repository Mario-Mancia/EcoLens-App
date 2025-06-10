package com.example.ecolens.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecolens.data.local.dao.RecyclingDao
import com.example.ecolens.data.local.entities.RecyclingEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecyclingViewModel(private val recyclingDao: RecyclingDao) : ViewModel() {

    private val _recyclingHistory = MutableStateFlow<List<RecyclingEntity>>(emptyList())
    val recyclingHistory: StateFlow<List<RecyclingEntity>> = _recyclingHistory.asStateFlow()

    fun insertRecycling(recycling: RecyclingEntity) {
        viewModelScope.launch {
            recyclingDao.InsertRecycling(recycling)
            recycling.userId?.let { loadRecyclingHistory(it) }
        }
    }

    fun loadRecyclingHistory(userId: Int) {
        viewModelScope.launch {
            recyclingDao.getRecyclingByUser(userId).collectLatest { list ->
                _recyclingHistory.value = list
            }
        }
    }

    fun deleteRecycling(recycling: RecyclingEntity) {
        viewModelScope.launch {
            recyclingDao.deleteRecycling(recycling)
        }
    }
}

class RecyclingViewModelFactory(
    private val recyclingDao: RecyclingDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecyclingViewModel::class.java)) {
            return RecyclingViewModel(recyclingDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}