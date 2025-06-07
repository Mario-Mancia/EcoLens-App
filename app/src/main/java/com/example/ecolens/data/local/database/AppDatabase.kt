package com.example.ecolens.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ecolens.data.local.entities.*
import com.example.ecolens.data.local.dao.*
import com.example.ecolens.data.local.Converters

@Database(
    entities = [
        UserEntity::class,
        RecyclingEntity::class,
        UserStatsEntity::class,
        UserAchievementsEntity::class,
        AchievementsEntity::class,
        StepsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recyclingDao(): RecyclingDao
    abstract fun userStatsDao(): UserStatsDao
    abstract fun userArchievementsDao(): UserArchievementsDao
    abstract fun archievementsDao(): ArchievementsDao
    abstract fun stepsDao(): StepsDao
}
