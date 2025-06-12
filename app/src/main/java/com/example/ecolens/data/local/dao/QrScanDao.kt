package com.example.ecolens.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.ecolens.data.local.entities.QrScanEntity

@Dao
interface QrScanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: QrScanEntity)

    @Query("SELECT * FROM qr_scans WHERE userId = :userId ORDER BY datetime DESC")
    fun getScansByUser(userId: Int): Flow<List<QrScanEntity>>

    @Query("SELECT COUNT(*) FROM qr_scans WHERE userId = :userId AND content = :content")
    suspend fun isScanRegistered(userId: Int, content: String): Int

    @Query("SELECT COUNT(DISTINCT content) FROM qr_scans WHERE userId = :userId")
    suspend fun getUniqueScanCount(userId: Int): Int
}