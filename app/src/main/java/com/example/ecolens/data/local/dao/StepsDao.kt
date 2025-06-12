package com.example.ecolens.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ecolens.data.local.entities.StepsEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface StepsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: StepsEntity): Long

    @Update
    suspend fun updateSteps(steps: StepsEntity)

    @Query("SELECT * FROM steps")
    suspend fun getAll(): List<StepsEntity>

    @Delete
    suspend fun deleteSteps(steps: StepsEntity)

    @Query("SELECT * FROM steps WHERE userId = :userId ORDER BY date DESC")
    fun getStepsByUser(userId: Int): Flow<List<StepsEntity>>

    @Query("SELECT * FROM steps WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getStepsByDate(userId: Int, date: LocalDate): StepsEntity?

    @Query("SELECT SUM(stepCount) FROM steps WHERE userId = :userId")
    suspend fun getTotalSteps(userId: Int): Int?

    //Esta es la nueva función que añadí:
    @Query("SELECT SUM(stepCount) FROM steps WHERE userId = :userId AND date = :date")
    suspend fun getTodaySteps(userId: Int, date: LocalDate): Int
}