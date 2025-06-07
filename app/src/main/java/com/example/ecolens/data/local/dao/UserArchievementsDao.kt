package com.example.ecolens.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ecolens.data.local.entities.UserAchievementsEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface UserArchievementsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAchievement(userAchievement: UserAchievementsEntity): Long

    @Delete
    suspend fun deleteUserAchievement(userAchievement: UserAchievementsEntity)

    @Query("SELECT * FROM user_achievements WHERE userId = :userId")
    fun getAchievementsByUser(userId: Int): Flow<List<UserAchievementsEntity>>

    @Query("SELECT * FROM user_achievements WHERE userId = :userId AND achievementId = :achievementId LIMIT 1")
    suspend fun getUserAchievement(userId: Int, achievementId: Int): UserAchievementsEntity?
}