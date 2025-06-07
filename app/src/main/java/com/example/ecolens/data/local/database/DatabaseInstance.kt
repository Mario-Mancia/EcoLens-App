package com.example.ecolens.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ecolens.data.local.dao.*
import com.example.ecolens.data.local.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseInstance {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "ecolens_database"
            )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        // Agregar datos por defecto
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { database ->
                                database.userDao().insertUser(
                                    UserEntity(
                                        username = "admin",
                                        email = "admin123@gmail.com",
                                        country = "El Salvador",
                                        gender = "M",
                                        password = "admin123"
                                    )
                                )
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()

            INSTANCE = instance
            instance
        }
    }
}