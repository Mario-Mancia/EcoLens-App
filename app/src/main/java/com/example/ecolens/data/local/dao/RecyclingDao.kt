package com.example.ecolens.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ecolens.data.local.entities.RecyclingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecyclingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun InsertRecycling(recycling: RecyclingEntity): Long

    @Query("SELECT * FROM recycling WHERE userId = :userId ORDER BY datetime DESC LIMIT 15")
    fun getRecyclingByUser(userId: Int): Flow<List<RecyclingEntity>>

    @Query("SELECT * FROM recycling WHERE id = :id")
    suspend fun getRecyclingById(id: Int): RecyclingEntity?

    @Query("SELECT SUM(quantity) FROM recycling WHERE userId = :userId")
    suspend fun getTotalRecycledByUser(userId: Int): Int?

    @Delete
    suspend fun deleteRecycling(recycling: RecyclingEntity)
}