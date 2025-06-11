package com.example.ecolens.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecolens.data.local.dao.QrScanDao
import com.example.ecolens.data.local.entities.QrScanEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QrScanViewModel(
    private val qrScanDao: QrScanDao
) : ViewModel() {

    private val _scans = MutableStateFlow<List<QrScanEntity>>(emptyList())
    val scans: StateFlow<List<QrScanEntity>> = _scans.asStateFlow()

    fun insertScanIfNotExists(scan: QrScanEntity, onSuccess: () -> Unit, onDuplicate: () -> Unit) {
        viewModelScope.launch {
            val exists = qrScanDao.isScanRegistered(scan.userId, scan.content) > 0
            if (!exists) {
                qrScanDao.insertScan(scan)
                loadScansByUser(scan.userId)
                onSuccess()
            } else {
                onDuplicate()
            }
        }
    }

    fun loadScansByUser(userId: Int) {
        viewModelScope.launch {
            qrScanDao.getScansByUser(userId).collect { userScans ->
                _scans.value = userScans
            }
        }
    }
}

class QrScanViewModelFactory(
    private val qrScanDao: QrScanDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QrScanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QrScanViewModel(qrScanDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}