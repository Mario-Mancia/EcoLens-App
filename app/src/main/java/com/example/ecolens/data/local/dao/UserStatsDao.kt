package com.example.ecolens.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ecolens.data.local.entities.UserStatsEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface UserStatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStats(stats: UserStatsEntity): Long

    @Update
    suspend fun updateStats(stats: UserStatsEntity)

    @Query("SELECT * FROM user_stats WHERE userId = :userId LIMIT 1")
    fun getStatsByUser(userId: Int): Flow<UserStatsEntity?>

    @Query("UPDATE user_stats SET totalRecyclings = totalRecyclings + :increment WHERE userId = :userId")
    suspend fun incrementRecycling(userId: Int, increment: Int)

    @Query("UPDATE user_stats SET ecoPoints = ecoPoints + :points WHERE userId = :userId")
    suspend fun addEcoPoints(userId: Int, points: Int)

    @Query("UPDATE user_stats SET totalSteps = totalSteps + :steps WHERE userId = :userId")
    suspend fun addSteps(userId: Int, steps: Int)
}