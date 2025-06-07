package com.example.ecolens.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

//Entidad para el manejo de la tabla de estadísticas del usuario.
@Entity(
    tableName = "user_stats",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserStatsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val totalRecyclings: Int = 0,
    val totalAchievements: Int = 0,
    val ecoPoints: Int = 0,
    val totalSteps: Int = 0
)