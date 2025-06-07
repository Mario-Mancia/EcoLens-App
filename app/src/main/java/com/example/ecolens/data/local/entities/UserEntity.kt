package com.example.ecolens.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


//Entidad para manejar la tabla de usuarios.
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val email: String,
    val country: String,
    val gender: String,
    val password: String,
    val createdAt: Long = System.currentTimeMillis()
)