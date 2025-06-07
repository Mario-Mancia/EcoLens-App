package com.example.ecolens.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ecolens.data.local.entities.AchievementsEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ArchievementsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: AchievementsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAchievements(achievements: List<AchievementsEntity>)

    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<AchievementsEntity>>

    @Query("SELECT * FROM achievements WHERE id = :achievementId LIMIT 1")
    suspend fun getAchievementById(achievementId: Int): AchievementsEntity?

    @Delete
    suspend fun deleteAchievement(achievement: AchievementsEntity)
}